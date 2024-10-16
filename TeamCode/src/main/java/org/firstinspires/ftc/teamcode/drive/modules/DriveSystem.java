package org.firstinspires.ftc.teamcode.drive.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class DriveSystem extends AbstractModule
{
  private DcMotor frontLeftMotor = null;
  private DcMotor frontRightMotor = null;
  private DcMotor backLeftMotor = null;
  private DcMotor backRightMotor = null;

  public DriveSystem( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( telemetry );
    initObjects( hardwareMap );
    initState();
  }

  private void initObjects( HardwareMap hardwareMap )
  {
    frontLeftMotor = hardwareMap.get( DcMotor.class, "frontLeftMotor" );
    frontRightMotor = hardwareMap.get( DcMotor.class, "frontRightMotor" );
    backLeftMotor = hardwareMap.get( DcMotor.class, "backLeftMotor" );
    backRightMotor = hardwareMap.get( DcMotor.class, "backRightMotor" );
  }

  private void initState()
  {
    initMotor( frontLeftMotor, DcMotorSimple.Direction.FORWARD );
    initMotor( frontRightMotor, DcMotorSimple.Direction.REVERSE );
    initMotor( backLeftMotor, DcMotorSimple.Direction.FORWARD );
    initMotor( backRightMotor, DcMotorSimple.Direction.REVERSE );
  }

  private class MotorValues
  {
    public double frontLeft = 0;
    public double frontRight = 0;
    public double backLeft = 0;
    public double backRight = 0;
  };

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
    double maxSpeed = 0.5;

    double largestSpeed = maxSpeed;
    largestSpeed = Math.max( largestSpeed, Math.abs( speeds.frontLeft ) );
    largestSpeed = Math.max( largestSpeed, Math.abs( speeds.frontRight ) );
    largestSpeed = Math.max( largestSpeed, Math.abs( speeds.backLeft ) );
    largestSpeed = Math.max( largestSpeed, Math.abs( speeds.backRight ) );

    MotorValues powers = new MotorValues();
    powers.frontLeft = speeds.frontLeft / largestSpeed;
    powers.frontRight = speeds.frontRight / largestSpeed;
    powers.backLeft = speeds.backLeft / largestSpeed;
    powers.backRight = speeds.backRight / largestSpeed;

    setMotorPowers( powers );
  }

  private void setMotorPowers( MotorValues powers )
  {
    frontLeftMotor.setPower( powers.frontLeft );
    frontRightMotor.setPower( powers.frontRight );
    backLeftMotor.setPower( powers.backLeft );
    backRightMotor.setPower( powers.backRight );
  }

  @Override
  public void stop()
  {
    MotorValues powers = new MotorValues();
    setMotorPowers( powers );
  }

  @Override
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Drive Front Left Motor - %s", frontLeftMotor.getPower() ) );
    telemetry.addLine( String.format( "Drive Front Right Motor - %s", frontRightMotor.getPower() ) );
    telemetry.addLine( String.format( "Drive Back Left Motor - %s", backLeftMotor.getPower() ) );
    telemetry.addLine( String.format( "Drive Back Right Motor - %s", backRightMotor.getPower() ) );
    telemetry.update();
  }
}
