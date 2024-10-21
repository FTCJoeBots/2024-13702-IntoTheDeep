package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DriveSystem extends AbstractModule
{
  private DcMotor frontLeftMotor = null;
  private DcMotor frontRightMotor = null;
  private DcMotor backLeftMotor = null;
  private DcMotor backRightMotor = null;
  private IMU inertialMeasurementUnit = null;

  public DriveSystem( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  private void initObjects()
  {
    frontLeftMotor = createMotor( "frontLeftMotor" );
    frontRightMotor = createMotor( "frontRightMotor" );
    backLeftMotor = createMotor( "backLeftMotor" );
    backRightMotor = createMotor( "backRightMotor" );
    inertialMeasurementUnit = hardwareMap.get( IMU.class, "imu" );
  }

  private void initState()
  {
    final DcMotor.RunMode runMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
    initMotor( frontLeftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( frontRightMotor, runMode, DcMotorSimple.Direction.REVERSE );
    initMotor( backLeftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( backRightMotor, runMode, DcMotorSimple.Direction.REVERSE );

    RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot( RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD );
    inertialMeasurementUnit.initialize( new IMU.Parameters( orientationOnRobot ) );
    inertialMeasurementUnit.resetYaw();
  }

  private class MotorValues
  {
    public double frontLeft = 0;
    public double frontRight = 0;
    public double backLeft = 0;
    public double backRight = 0;
  }

  public void move( double forward, double strafe, double rotate )
  {
    MotorValues speeds = new MotorValues();
    speeds.frontLeft = forward + strafe + rotate;
    speeds.frontRight = forward - strafe - rotate;
    speeds.backLeft = forward - strafe + rotate;
    speeds.backRight = forward + strafe - rotate;

    setMotorSpeeds( speeds );
  }

  private void setMotorSpeeds( MotorValues speeds )
  {
    MotorValues powers = new MotorValues();
    powers.frontLeft = speeds.frontLeft;
    powers.frontRight = speeds.frontRight;
    powers.backLeft = speeds.backLeft;
    powers.backRight = speeds.backRight;

    double largestSpeed = Math.abs( speeds.frontLeft );
    largestSpeed = Math.max( largestSpeed, Math.abs( speeds.frontRight ) );
    largestSpeed = Math.max( largestSpeed, Math.abs( speeds.backLeft ) );
    largestSpeed = Math.max( largestSpeed, Math.abs( speeds.backRight ) );

    if( largestSpeed > 1 )
    {
      powers.frontLeft /= largestSpeed;
      powers.frontRight /= largestSpeed;
      powers.backLeft /= largestSpeed;
      powers.backRight /= largestSpeed;
    }

    setMotorPowers( powers );
  }

  private void setMotorPowers( MotorValues powers )
  {
    frontLeftMotor.setPower( powers.frontLeft );
    frontRightMotor.setPower( powers.frontRight );
    backLeftMotor.setPower( powers.backLeft );
    backRightMotor.setPower( powers.backRight );
  }

  private double getHeading()
  {
    YawPitchRollAngles orientation = inertialMeasurementUnit.getRobotYawPitchRollAngles();
    return orientation.getYaw( AngleUnit.DEGREES );
  }

  @Override
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Drive Front Left Motor - %s", frontLeftMotor.getPower() ) );
    telemetry.addLine( String.format( "Drive Front Right Motor - %s", frontRightMotor.getPower() ) );
    telemetry.addLine( String.format( "Drive Back Left Motor - %s", backLeftMotor.getPower() ) );
    telemetry.addLine( String.format( "Drive Back Right Motor - %s", backRightMotor.getPower() ) );
    telemetry.addLine().addData( "IMU Heading - ", "%.1f", getHeading() );
  }
}
