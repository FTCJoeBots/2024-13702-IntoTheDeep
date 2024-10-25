package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Lift extends AbstractModule
{
  public static final double SLOW_SPEED = 0.1;
  public static final double FAST_SPEED = 1;

  public static final int LIFTMANUALINC = 30;

  //Preset positions we can extend the arm to
  public enum Position
  {
    HIGHEST( 360 ), HIGH_BASKET( 200 ), LOW_BASKET( 100 ), FLOOR( 0 );

    Position( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  DcMotor leftMotor = null;
  DcMotor rightMotor = null;

  public Lift( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  private void initObjects()
  {
    leftMotor = createMotor( "leftLiftMotor" );
    rightMotor = createMotor( "rightLiftMotor" );
  }

  private void initState()
  {
    final DcMotor.RunMode runMode = DcMotor.RunMode.RUN_TO_POSITION;
    initMotor( leftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( rightMotor, runMode, DcMotorSimple.Direction.REVERSE );
  }

  private void turnMotor( DcMotor motor, DcMotorSimple.Direction direction, double speed )
  {
    int liftCurPosition = motor.getCurrentPosition();
    int liftNewPosition = liftCurPosition + ( direction == DcMotorSimple.Direction.FORWARD ? 1 : -1 ) * LIFTMANUALINC;

    if( liftNewPosition > Position.HIGHEST.value )
    {
      liftNewPosition = Position.HIGHEST.value;
    }

    if( liftNewPosition < Position.FLOOR.value )
    {
      liftNewPosition = Position.FLOOR.value;
    }

    setMotorPosition( motor, liftNewPosition, speed );
  }

  private void setMotorPosition( DcMotor motor, int position, double power )
  {
    if( motor == null )
    { return; }

    motor.setTargetPosition( position );
    motor.setPower( power );
  }

  public void fastLift()
  {
    turnMotor( leftMotor, DcMotorSimple.Direction.FORWARD, FAST_SPEED );
    turnMotor( rightMotor, DcMotorSimple.Direction.FORWARD, FAST_SPEED );
  }

  public void fastDrop()
  {
    turnMotor( leftMotor, DcMotorSimple.Direction.REVERSE, FAST_SPEED );
    turnMotor( rightMotor, DcMotorSimple.Direction.REVERSE, FAST_SPEED );
  }

  public void slowLift()
  {
    turnMotor( leftMotor, DcMotorSimple.Direction.FORWARD, SLOW_SPEED );
    turnMotor( rightMotor, DcMotorSimple.Direction.FORWARD, SLOW_SPEED );
  }

  //manual down
  public void slowDrop()
  {
    turnMotor( leftMotor, DcMotorSimple.Direction.REVERSE, SLOW_SPEED );
    turnMotor( rightMotor, DcMotorSimple.Direction.REVERSE, SLOW_SPEED );
  }

  public void travelTo( Position position )
  {
    setMotorPosition( leftMotor, position.value, FAST_SPEED );
    setMotorPosition( rightMotor, position.value, FAST_SPEED );
  }

  public void climb()
  {
    setMotorPosition( leftMotor, Position.HIGHEST.value, SLOW_SPEED );
    setMotorPosition( rightMotor, Position.HIGHEST.value, SLOW_SPEED );
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    if( leftMotor != null )
    { telemetry.addLine( String.format( "Left Lift Motor: %s", leftMotor.getCurrentPosition() ) ); }

    if( rightMotor != null )
    { telemetry.addLine( String.format( "Right Lift Motor: %s", rightMotor.getCurrentPosition() ) ); }
  }
}
