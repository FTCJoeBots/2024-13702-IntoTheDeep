package org.firstinspires.ftc.teamcode.modules;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.roadrunner.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

public class DriveSystem extends AbstractModule
{
  private DcMotor frontLeftMotor = null;
  private DcMotor frontRightMotor = null;
  private DcMotor backLeftMotor = null;
  private DcMotor backRightMotor = null;

  private ThreeDeadWheelLocalizer localizer = null;
  private static Pose2d pose = new Pose2d( 0, 0, 0 );

  private IMU inertialMeasurementUnit = null;
  private static YawPitchRollAngles orientation = new YawPitchRollAngles( AngleUnit.DEGREES, 0, 0, 0, 0 );
  private static ElapsedTime orientationTime = new ElapsedTime();

  public enum CurrentAction
  {
    ROTATE,
    DOING_NOTHING
  }

  public enum RotateDirection
  {
    RIGHT,
    LEFT
  }

  public enum PresetDirection
  {
    FOREWARD,
    BACKWARD,
    LEFT,
    RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT,
    UP_LEFT,
    UP_RIGHT
  }

  private double ANGLE_THRESHOLD = 2.0;
  private double BREAKING_DISTANCE = 30;

  private CurrentAction currentAction = CurrentAction.DOING_NOTHING;
  private RotateDirection targetDirection = RotateDirection.RIGHT;
  private double targetAngle = 0;

  public void turnAround( RotateDirection direction )
  {
    double currAngle = angleForHeading( pose.heading.toDouble() );
    double nextAngle = ( currAngle + 180 ) % 360;
    turnToAngle( direction, nextAngle );
  }

  public void faceDirection( PresetDirection direction )
  {
    double currentAngle = angleForHeading( pose.heading.toDouble() );

    double nextHeading = headingForDirection( direction );
    double nextAngle = angleForHeading( nextHeading );

    RotateDirection rotateDirection = quickestDirection( currentAngle, nextAngle );
    turnToAngle( rotateDirection, nextAngle );
  }

  private RotateDirection quickestDirection( double currentAngle, double nextAngle )
  {
    double right = nextAngle - currentAngle;
    if( right < 0 )
    {
      right += 360;
    }

    double left = 360 - right;

    if( right < left )
    { return RotateDirection.RIGHT; }
    else
    { return RotateDirection.LEFT; }
  }

  //converts [-180, 180] to [0, 360]
  private double angleForHeading( double heading )
  {
    return heading + 180;
  }

  //returns a value between [ -180, 180 ]
  private double headingForDirection( PresetDirection direction )
  {
    switch( direction )
    {
      case FOREWARD:
        return 0;
      case BACKWARD:
        return 180;
      case LEFT:
        return -90;
      case RIGHT:
        return 90;
      case DOWN_LEFT:
        return -135;
      case DOWN_RIGHT:
        return 135;
      case UP_LEFT:
        return -45;
      case UP_RIGHT:
        return 45;
      default:
        return 0;
    }
  }

  private void turnToAngle( RotateDirection direction, double angle )
  {
    currentAction = CurrentAction.ROTATE;
    targetDirection = direction;
    targetAngle = angle;
  }

  public DriveSystem( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  public void coast()
  {
    setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
  }

  public void brake()
  {
    setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
  }

  private void initObjects()
  {
    frontLeftMotor = createMotor( "frontLeftMotor" );
    frontRightMotor = createMotor( "frontRightMotor" );
    backLeftMotor = createMotor( "backLeftMotor" );
    backRightMotor = createMotor( "backRightMotor" );
    localizer = new ThreeDeadWheelLocalizer( hardwareMap, MecanumDrive.PARAMS.inPerTick );
//    inertialMeasurementUnit = hardwareMap.get( IMU.class, "imu" );
  }

  private void initState()
  {
    //do not use the motor encoder for built-in velocity control
    final DcMotor.RunMode runMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
    initMotor( frontLeftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( frontRightMotor, runMode, DcMotorSimple.Direction.REVERSE );
    initMotor( backLeftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( backRightMotor, runMode, DcMotorSimple.Direction.REVERSE );

    if( inertialMeasurementUnit != null )
    {
      RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot( RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD );
      inertialMeasurementUnit.initialize( new IMU.Parameters( orientationOnRobot ) );
      inertialMeasurementUnit.resetYaw();
    }
  }

  private class MotorValues
  {
    public double frontLeft = 0;
    public double frontRight = 0;
    public double backLeft = 0;
    public double backRight = 0;
  }

  private double computeRotateSpeed( double angleDifference )
  {
    if( angleDifference > BREAKING_DISTANCE )
    { return  1; }
    else
    { return angleDifference / BREAKING_DISTANCE; }
  }

  public void move( double forward, double strafe, double rotate )
  {
    if( rotate != 0 )
    {
      currentAction = CurrentAction.DOING_NOTHING;
    }
    else if( currentAction == CurrentAction.ROTATE )
    {
      double currAngle = angleForHeading( pose.heading.toDouble() );
      double angleDifference = targetAngle - currAngle;

      if( angleDifference < 0 )
      {
        angleDifference += 360;
      }

      if( angleDifference > 180 )
      {
        angleDifference = 360 - angleDifference;
      }

      if( angleDifference < ANGLE_THRESHOLD )
      {
        currentAction = CurrentAction.DOING_NOTHING;
      }
      else if( targetDirection == RotateDirection.RIGHT )
      {
        rotate = computeRotateSpeed( angleDifference );
      }
      else
      {
        rotate = -computeRotateSpeed( angleDifference );
      }
    }

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

  public void updateLocation()
  {
    updatePose();
    updateHeading();
  }

  private void updateHeading()
  {
    if( inertialMeasurementUnit == null ||
        orientationTime.seconds() < 10 )
    { return; }

    YawPitchRollAngles angles = inertialMeasurementUnit.getRobotYawPitchRollAngles();

    if( angles != null &&
        !Double.valueOf( angles.getYaw( AngleUnit.DEGREES ) ).isNaN() )
    { orientation = angles; }

    orientationTime.reset();
  }

  private void updatePose()
  {
    if( localizer == null )
    { return; }

    Twist2dDual<Time> twist = localizer.update();
    pose = pose.plus( twist.value() );
  }

  @Override
  public void printTelemetry()
  {
//    telemetry.addLine( String.format( "Front Left Power: %s", frontLeftMotor.getPower() ) );
//    telemetry.addLine( String.format( "Front Right Power: %s", frontRightMotor.getPower() ) );
//    telemetry.addLine( String.format( "Back Left Power: %s", backLeftMotor.getPower() ) );
//    telemetry.addLine( String.format( "Back Right Power: %s", backRightMotor.getPower() ) );

//    telemetry.addLine( String.format( "Front Left Pos: %s", frontLeftMotor.getCurrentPosition() ) );
//    telemetry.addLine( String.format( "Front Right Pos: %s", frontRightMotor.getCurrentPosition() ) );
//    telemetry.addLine( String.format( "Back Left Pos: %s", backLeftMotor.getCurrentPosition() ) );
//    telemetry.addLine( String.format( "Back Right Pos: %s", backRightMotor.getCurrentPosition() ) );

    if( localizer != null )
    {
      telemetry.addLine().addData( "XL: ", "%s", localizer.par0.getPositionAndVelocity().position );
      telemetry.addLine().addData( "XR: ", "%s", localizer.par1.getPositionAndVelocity().position );
      telemetry.addLine().addData( "XS: ", "%s", localizer.perp.getPositionAndVelocity().position );

      telemetry.addLine().addData( "Heading: ", "%.1f", Math.toDegrees( pose.heading.toDouble() ) );
      telemetry.addLine().addData( "X: ", "%.1f", pose.position.x );
      telemetry.addLine().addData( "Y: ", "%.1f", pose.position.y );
    }

    if( inertialMeasurementUnit != null )
    {
      telemetry.addLine().addData( "IMU Heading: ", "%.1f", orientation.getYaw( AngleUnit.DEGREES ) );
      telemetry.addLine().addData( "Pitch: ", "%.1f", orientation.getPitch( AngleUnit.DEGREES ) );
      telemetry.addLine().addData( "Roll: ", "%.1f", orientation.getRoll( AngleUnit.DEGREES ) );
    }
  }
}
