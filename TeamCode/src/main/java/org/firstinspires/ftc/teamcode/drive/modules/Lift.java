package org.firstinspires.ftc.teamcode.drive.modules;

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
    super( telemetry );
    initObjects( hardwareMap );
    initState();
  }

  private void initObjects( HardwareMap hardwareMap )
  {
    leftMotor = hardwareMap.get( DcMotor.class, "leftLiftMotor" );
    rightMotor = hardwareMap.get( DcMotor.class, "rightLiftMotor" );
  }

  private void initMotor( DcMotor motor, DcMotorSimple.Direction direction )
  {
    motor.setMode( DcMotor.RunMode.RUN_USING_ENCODER );
    motor.setDirection( direction );
    motor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    motor.setPower( 0 );
  }

  private void initState()
  {
    initMotor( leftMotor, DcMotorSimple.Direction.FORWARD );
    initMotor( rightMotor, DcMotorSimple.Direction.REVERSE );
    travelTo( Position.FLOOR );
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

    motor.setTargetPosition( liftNewPosition );
    motor.setPower( speed );
  }

  private void setMotorPosition( DcMotor motor, int position, double power )
  {
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

  //Stops the extension arm motor
  public void stop()
  {
    leftMotor.setPower( 0 );
    rightMotor.setPower( 0 );
  }

  //Prints out the extension arm motor position
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Left Lift Motor -  %s", leftMotor.getCurrentPosition() ) );
    telemetry.addLine( String.format( "Right Lift Motor -  %s", rightMotor.getCurrentPosition() ) );
    telemetry.update();
  }
}
