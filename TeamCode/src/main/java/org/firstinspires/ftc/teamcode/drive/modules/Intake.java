package org.firstinspires.ftc.teamcode.drive.modules;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Intake extends AbstractModule
{
  public static final double SLOW_SPEED = 0.1;
  public static final double FAST_SPEED = 1;
  public static final double STOP_SPEED = 0;

  private CRServo leftServo = null;
  private CRServo rightServo = null;

  public Intake( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( telemetry );
    initObjects( hardwareMap );
    initState();
  }

  private void initObjects( HardwareMap hardwareMap )
  {
    leftServo = hardwareMap.get( CRServo.class, "leftIntakeServo" );
    rightServo = hardwareMap.get( CRServo.class, "rightIntakeServo" );
  }

  private void initState()
  {
    initServo( leftServo, DcMotorSimple.Direction.FORWARD );
    initServo( rightServo, DcMotorSimple.Direction.REVERSE );
  }

  private void initServo( CRServo servo, DcMotorSimple.Direction direction )
  {
    servo.setDirection( direction );
    servo.setPower( STOP_SPEED );
  }

  private void setServoSpeed( double speed )
  {
    leftServo.setPower( speed );
    rightServo.setPower( speed );
  }

  public void pullInSample()
  {
    setServoSpeed( FAST_SPEED );
  }

  public void spitOutSample()
  {
    setServoSpeed( -SLOW_SPEED );
  }

  public void stop()
  {
    setServoSpeed( STOP_SPEED );
  }

  //Prints out the extension arm motor position
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Left Intake Servo -  %s", leftServo.getPower() ) );
    telemetry.addLine( String.format( "Right Intake Servo -  %s", rightServo.getPower() ) );
    telemetry.update();
  }
}