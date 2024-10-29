package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Lift extends AbstractModule
{
  public static final double SLOW_SPEED = 0.1;
  public static final double FAST_SPEED = 0.5;

  public static final int MANUAL_POSITION_ADJUST = 100;

  //Preset positions we can extend the arm to
  public enum Position
  {
    FLOOR( 0 ),
    SAMPLE_FLOOR( 172 ),
    SPECIMEN_FLOOR( 347 ),
    HIGH_BASKET( 9545 ),
    LOW_BASKET( 5846 ),
    ABOVE_HIGH_SPECIMEN_BAR( 6324 ),
    ABOVE_LOW_SPECIMEN_BAR( 3548 ),
    SPECIMEN_CLIPPED_ONTO_HIGH_BAR( 5691 ),
    SPECIMEN_CLIPPED_ONTO_LOW_BAR( 2996 ),
    ABOVE_HIGH_HANG_BAR( 8060 ),
    ABOVE_LOW_HANG_BAR( 4663 ),
    HANG_FROM_HIGH_HANG_BAR ( 7216 ),
    HANG_FROM_LOW_HANG_BAR( 3889 );

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
    final DcMotor.RunMode runMode = DcMotor.RunMode.RUN_USING_ENCODER;
    initMotor( leftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( rightMotor, runMode, DcMotorSimple.Direction.REVERSE );
  }

  private void turnMotor( DcMotor motor, DcMotorSimple.Direction direction, double speed )
  {
    int liftCurPosition = motor.getCurrentPosition();
    int liftNewPosition = liftCurPosition + ( direction == DcMotorSimple.Direction.FORWARD ? 1 : -1 ) * MANUAL_POSITION_ADJUST;

    if( liftNewPosition > Position.HIGH_BASKET.value )
    {
      liftNewPosition = Position.HIGH_BASKET.value;
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
    setMotorPosition( leftMotor, Position.ABOVE_LOW_HANG_BAR.value, SLOW_SPEED );
    setMotorPosition( rightMotor, Position.ABOVE_LOW_HANG_BAR.value, SLOW_SPEED );
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
