package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ExtensionArm extends AbstractModule
{
  private DcMotorEx extensionArmMotor = null;

  //Relative position for manually extending and contracting the arm
  private static final int MANUAL_POSITION_ADJUST = 80;

  //Preset positions we can extend the arm to
  public enum Position
  {
    FULLY_RETRACTED( 0 ),
    RETRACTED_WITH_SAMPLE( 30 ),
    FULLY_EXTENDED( 2000 ),
    EXTEND_TO_GRAB_SAMPLE( 1446 ),
    EXTEND_TO_GRAB_SPECIMEN( 1000 ),
    EXTEND_TO_DUMP_IN_BASKET( 670 ),
    EXTEND_TO_HANG_SAMPLE( 662 ),
    EXTEND_TO_TOUCH_BAR( 253 ),
    EXTEND_TO_CLIMB( 518 ),
    RETRACT_TO_CLIMB( 451 ),
    MAX_EXTENSION_WHILE_HIGH( 1360 );

    Position( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  //Various speeds for extending and retracting the arm
  public enum Speed
  {
    FAST( 1.0 ),
    MANUAL( 0.5 ),
    GRAB_SPECIMEN( 0.4 ),
    HANG_SPECIMEN( 1 );

    Speed( double value )
    {
      this.value = value;
    }

    public final double value;
  }

  private enum Action
  {
    MOVING,
    CLIMBING,
    STOPPED
  }

  private Action currentAction = Action.STOPPED;

  private boolean autoDetectStall = false;
  ElapsedTime stallTimer = null;

  public ExtensionArm( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  public boolean isMoving()
  {
    return currentAction != Action.STOPPED;
  }

  public void updateState()
  {
    if( currentAction == Action.STOPPED )
    { return; }

    final int current = extensionArmMotor.getCurrentPosition();
    final int target = extensionArmMotor.getTargetPosition();
    final int diff = Math.abs( current - target );

    if( diff <= 2 )
    {
      telemetry.log().add( String.format( "Arm stopping, current %s target %s", current, target ) );

      stop();
    }
    //Detect if the extension arm stalls when extending or retracting.
    //When a stall is detected stop the motor to avoid damaging the belt.
    //If the arm stalls when retracting fully reset the motor position since
    //this is due to belt slippage and the arm is likely fully retracted.
    else if( currentAction == Action.MOVING &&
             autoDetectStall &&
             Math.abs( extensionArmMotor.getVelocity() ) <= 0.2 &&
             stallTimer.milliseconds() > 3000 )
    {
      telemetry.log().add( "Stall detected, stopping extension arm!" );
      telemetry.log().add( String.format( "ellapsed: %s", stallTimer.milliseconds() ) );
      telemetry.log().add( String.format( "currentVelocity: %f", extensionArmMotor.getVelocity() ) );
      telemetry.log().add( String.format( "currentPosition: %s", current ) );
      telemetry.log().add( String.format( "targetPosition: %s", target ) );
      super.stop();
      currentAction = Action.STOPPED;
      autoDetectStall = false;

      //only reset the motor position if the difference is small and likely due to belt slippage
      //avoid resetting the motor position if the arm appears to have gotten hung up on the submersible
      if( target == Position.FULLY_RETRACTED.value &&
          current > target &&
          diff < 100 )
      {
        telemetry.log().add( "Resetting motor position" );
        extensionArmMotor.setMode( DcMotor.RunMode.STOP_AND_RESET_ENCODER );
        extensionArmMotor.setMode( DcMotor.RunMode.RUN_TO_POSITION );
      }
    }
  }

  public void fullyExtend()
  {
    setTargetPositionAndPower( Position.FULLY_EXTENDED.value, Speed.FAST.value );
  }

  public void fullyRetract()
  {
    setTargetPositionAndPower( Position.FULLY_RETRACTED.value, Speed.FAST.value );
  }

  public void climb()
  {
    extensionArmMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    extensionArmMotor.setTargetPosition( Position.FULLY_RETRACTED.value );
    extensionArmMotor.setPower( 1.0 );
    currentAction = Action.CLIMBING;
  }

  public void travelTo( int position )
  {
    travelToWithPower( position, Speed.FAST.value );
  }

  public void travelToWithPower( int position, double power )
  {
    setTargetPositionAndPower( position, power );
  }

  public boolean manuallyExtend( boolean liftIsHigh )
  {
    int nextPosition = extensionArmMotor.getCurrentPosition() + MANUAL_POSITION_ADJUST;

    if( liftIsHigh )
    { nextPosition = Math.min( nextPosition, Position.MAX_EXTENSION_WHILE_HIGH.value ); }

    // Ensure we continue to extend fully
    if( currentAction != Action.MOVING ||
        nextPosition > extensionArmMotor.getTargetPosition() )
    {
      Action cachedAction = currentAction;
      setTargetPositionAndPower( nextPosition, Speed.MANUAL.value );
      return currentAction != cachedAction;
    }
    else
    {
      return false;
    }
  }

  public boolean manuallyRetract()
  {
    int nextPosition = extensionArmMotor.getCurrentPosition() - MANUAL_POSITION_ADJUST;

    // Ensure we continue to retract fully
    if( currentAction != Action.MOVING ||
        nextPosition < extensionArmMotor.getTargetPosition() )
    {
      Action cachedAction = currentAction;
      setTargetPositionAndPower( nextPosition, Speed.MANUAL.value );
      return currentAction != cachedAction;
    }
    else
    {
      return false;
    }
  }

  private void setTargetPositionAndPower( int position, double power )
  {
    if( power <= 0 )
    {
      stop();
      return;
    }

    //Prevent moving too far
    if( position > Position.FULLY_EXTENDED.value )
    {
      position = Position.FULLY_EXTENDED.value;
    }
    else if( position < Position.FULLY_RETRACTED.value )
    {
      position = Position.FULLY_RETRACTED.value;
    }

    final int currentPosition = extensionArmMotor.getCurrentPosition();

    if( position != currentPosition )
    {
      telemetry.log().add( String.format("Arm traveling to %s", position ) );
      extensionArmMotor.setTargetPosition( position );
      extensionArmMotor.setPower( power );
      currentAction = Action.MOVING;

      if( position != Position.RETRACT_TO_CLIMB.value )
      {
        autoDetectStall = true;
        stallTimer.reset();
        telemetry.log().add( "Starting auto stall timer" );
      }
      else
      {
        autoDetectStall = false;
      }
    }

//    if( position == Position.FULLY_RETRACTED.value )
//    {
//      autoDetectStall = true;
//      stallTimer.reset();
//      telemetry.log().add( "Starting auto reset timer" );
//    }
//    else if( autoDetectStall )
//    {
//      telemetry.log().add( "Canceling auto reset timer" );
//      autoDetectStall = false;
//    }
  }

  public void stop()
  {
    //never drop motor power to 0 when climbing
    //or extension arm will extend
    if( currentAction == Action.CLIMBING )
    {
      extensionArmMotor.setTargetPosition( extensionArmMotor.getCurrentPosition() );
      extensionArmMotor.setPower( 1 );
    }
    else if( currentAction == Action.MOVING )
    {
      super.stop();
    }

    telemetry.log().add( "Arm stopped" );
    currentAction = Action.STOPPED;

    if( autoDetectStall )
    {
      telemetry.log().add( "Canceling stall detection" );
      autoDetectStall = false;
    }
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Extension Arm Action: %s", currentAction ) );
    telemetry.addLine( String.format( "Extension Arm Position: %s", getMotorPosition() ) );
  }

  public int getMotorPosition()
  {
    return extensionArmMotor.getCurrentPosition();
  }

  private void initObjects()
  {
    extensionArmMotor = createMotor( "extensionArmMotor" );
    stallTimer = new ElapsedTime();
  }

  private void initState()
  {
    initMotor( extensionArmMotor, DcMotor.RunMode.RUN_TO_POSITION, DcMotorSimple.Direction.REVERSE );
    extensionArmMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
  }
}