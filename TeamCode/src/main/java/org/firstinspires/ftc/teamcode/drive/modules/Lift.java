package org.firstinspires.ftc.teamcode.drive.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Lift extends AbstractModule
{
  public static final int LIFTLOWPOINT = 10;
  public static final int LIFTHIGHPOINT = 200;

  public static final double SLOW_SPEED = 0.1;
  public static final double FAST_SPEED= 1;

  public static final int LIFTMANUALINC = 30;

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
    leftMotor = hardwareMap.get( DcMotor.class, "leftMotor" );
    rightMotor = hardwareMap.get( DcMotor.class, "rightMotor" );
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
    initMotor(rightMotor, DcMotorSimple.Direction.REVERSE  );
    liftautodown();
  }

  private void turnMotor( DcMotor motor,
                          DcMotorSimple.Direction direction,
                          double speed)
  {
    int liftCurPosition = motor.getCurrentPosition();
    int liftNewPosition = liftCurPosition +
      ( direction == DcMotorSimple.Direction.FORWARD ?
        1 : -1 ) * LIFTMANUALINC;

    if( liftNewPosition > LIFTHIGHPOINT )
    {
      liftNewPosition = LIFTHIGHPOINT;
    }

    if( liftNewPosition < LIFTLOWPOINT )
    {
      liftNewPosition = LIFTLOWPOINT;
    }

    motor.setTargetPosition( liftNewPosition );
    motor.setPower( speed );
  }

  private void setMotorPostion( DcMotor motor,
                                int position,
                                double power )
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

  //auto down
  public void liftautodown()
  {
    setMotorPostion( leftMotor, LIFTLOWPOINT, FAST_SPEED );
    setMotorPostion( rightMotor, LIFTLOWPOINT, FAST_SPEED );
  }

  //auto up
  public void liftautoup()
  {
    setMotorPostion( leftMotor, LIFTHIGHPOINT, FAST_SPEED );
    setMotorPostion( rightMotor, LIFTHIGHPOINT, FAST_SPEED );
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
