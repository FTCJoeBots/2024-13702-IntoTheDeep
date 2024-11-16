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
  private static final int MANUAL_POSITION_ADJUST = 100;

  //Preset positions we can extend the arm to
  public enum Position
  {
    FULLY_RETRACTED( 0 ),
    RETRACTED_WITH_SAMPLE( 90 ),
    FULLY_EXTENDED( 2876 ),
    EXTEND_TO_GRAB_SAMPLE( 1600 ),
    EXTEND_TO_DUMP_IN_BASKET( 800 ),
    EXTEND_TO_HANG_SAMPLE( 1100 ),
    EXTEND_TO_TOUCH_BAR( 455 ),
    EXTEND_TO_CLIMB( 834 ),
    MAX_EXTENSION_WHILE_HIGH( 1000 );

    Position( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  //Various speeds for extending and retracting the arm
  private enum Speed
  {
    FAST( 1.0 ),
    MANUAL( 0.5 );

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

  private boolean autoResetMotorPosition = false;
  ElapsedTime autoResetTimer = null;

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

    if( diff <= 1 )
    {
      telemetry.log().add( String.format( "Arm stopping, cp %s tp %s", current, target ) );
      stop();
    }
    //####
    else if( currentAction == Action.MOVING &&
             extensionArmMotor.getCurrentPosition() > 0 &&
             extensionArmMotor.getTargetPosition() < extensionArmMotor.getCurrentPosition() &&
             Math.abs( extensionArmMotor.getVelocity() ) <= 0.2 &&
             autoResetMotorPosition &&
             autoResetTimer.milliseconds() > 3000 )
    {
      telemetry.log().add( "Stall when retracting detected, resetting motor position" );
      telemetry.log().add( String.format( "currentPosition: %s", extensionArmMotor.getCurrentPosition() ) );
      telemetry.log().add( String.format( "targetPosition: %s", extensionArmMotor.getTargetPosition() ) );
      telemetry.log().add( String.format( "currentVelocity: %f", extensionArmMotor.getVelocity() ) );
      telemetry.log().add( String.format( "ellapsed: %s", autoResetTimer.milliseconds() ) );
      super.stop();
      extensionArmMotor.setMode( DcMotor.RunMode.STOP_AND_RESET_ENCODER );
      extensionArmMotor.setMode( DcMotor.RunMode.RUN_TO_POSITION );
      currentAction = Action.STOPPED;
      autoResetMotorPosition = false;
    }
    //####
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

    if( position != extensionArmMotor.getCurrentPosition() )
    {
      telemetry.log().add( String.format("Arm traveling to %s", position ) );
      extensionArmMotor.setTargetPosition( position );
      extensionArmMotor.setPower( power );
      currentAction = Action.MOVING;
    }

    if( position == Position.FULLY_RETRACTED.value )
    {
      autoResetMotorPosition = true;
      autoResetTimer.reset();
      telemetry.log().add( "Starting auto reset timer" );
    }
    else if( autoResetMotorPosition )
    {
      telemetry.log().add( "Canceling auto reset timer" );
      autoResetMotorPosition = false;
    }
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

    if( autoResetMotorPosition )
    {
      telemetry.log().add( "Since arm was stopped canceling auto reset" );
      autoResetMotorPosition = false;
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
    autoResetTimer = new ElapsedTime();
  }

  private void initState()
  {
    initMotor( extensionArmMotor, DcMotor.RunMode.RUN_TO_POSITION, DcMotorSimple.Direction.REVERSE );
    extensionArmMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
  }
}