package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ExtensionArm extends AbstractModule
{
  private DcMotor extensionArmMotor = null;

  //Relative position for manually extending and contracting the arm
  private static final int MANUAL_POSITION_ADJUST = 100;

  //Preset positions we can extend the arm to
  public enum Position
  {
    FULLY_RETRACTED( 0 ),
    RETRACTED_WITH_SAMPLE( 47 ),
    FULLY_EXTENDED( 2876 ),
    EXTEND_TO_DUMP_IN_BASKET( 100 ),
    EXTEND_TO_HANG( 834 ),
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
    STOPPED
  }

  private Action currentAction = Action.STOPPED;

  public ExtensionArm( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  public boolean isMoving()
  {
    return currentAction == Action.MOVING;
  }

  public void updateState()
  {
    if( currentAction == Action.MOVING &&
      Math.abs( extensionArmMotor.getCurrentPosition() - extensionArmMotor.getTargetPosition() ) <= 1 )
    {
      stop();
      telemetry.log().add( "Extension Arm Stopped" );
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

  public void travelTo( Position position )
  {
    setTargetPositionAndPower( position.value, Speed.FAST.value );
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
      extensionArmMotor.setTargetPosition( position );
      extensionArmMotor.setPower( power );
      currentAction = Action.MOVING;
    }
  }

  public void stop()
  {
    super.stop();
    currentAction = Action.STOPPED;
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
  }

  private void initState()
  {
    initMotor( extensionArmMotor, DcMotor.RunMode.RUN_TO_POSITION, DcMotorSimple.Direction.REVERSE );
    extensionArmMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
  }
}