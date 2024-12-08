package org.firstinspires.ftc.teamcode.modules.drive;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.enums.PresetDirection;
import org.firstinspires.ftc.teamcode.enums.RotateDirection;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;
import org.firstinspires.ftc.teamcode.roadrunner.ThreeDeadWheelLocalizer;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

public class Drive extends AbstractModule
{
  private DcMotor frontLeftMotor = null;
  private DcMotor frontRightMotor = null;
  private DcMotor backLeftMotor = null;
  private DcMotor backRightMotor = null;

  private ThreeDeadWheelLocalizer localizer = null;
  private Pose2d pose;

  public enum Perspective
  {
    ROBOT,
    DRIVER
  }

  public enum CurrentAction
  {
    ROTATE,
    DOING_NOTHING
  }

  private double ANGLE_THRESHOLD = 1;
  private double BREAKING_DISTANCE = 20;

  private CurrentAction currentAction = CurrentAction.DOING_NOTHING;
  private Perspective perspective = Perspective.ROBOT;

  public void togglePerspective()
  {
    perspective = perspective == Perspective.ROBOT ?
      Perspective.DRIVER :
      Perspective.ROBOT;
  }

  private RotateDirection targetDirection = RotateDirection.RIGHT;
  private double targetAngle = 0;

  public Drive( HardwareMap hardwareMap, Telemetry telemetry, Pose2d pose )
  {
    super( hardwareMap, telemetry );
    this.pose = pose;
    initObjects();
    initState();
  }

  private void initObjects()
  {
    frontLeftMotor = createMotor( "frontLeftMotor" );
    frontRightMotor = createMotor( "frontRightMotor" );
    backLeftMotor = createMotor( "backLeftMotor" );
    backRightMotor = createMotor( "backRightMotor" );
    localizer = new ThreeDeadWheelLocalizer( hardwareMap, MecanumDrive.PARAMS.inPerTick );
  }

  private void initState()
  {
    //do not use the motor encoder for built-in velocity control
    final DcMotor.RunMode runMode = DcMotor.RunMode.RUN_WITHOUT_ENCODER;
    initMotor( frontLeftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( frontRightMotor, runMode, DcMotorSimple.Direction.REVERSE );
    initMotor( backLeftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( backRightMotor, runMode, DcMotorSimple.Direction.REVERSE );
  }

  public Pose2d getPos()
  {
    return pose;
  }

  public void resetPose( Pose2d pose )
  {
    this.pose = pose;
    telemetry.log().add( "Reset Position and Heading" );
  }

  public void turnAround( RotateDirection direction )
  {
    double currAngle = AngleTools.angleForHeading( Math.toDegrees( pose.heading.toDouble() ) );
    double nextAngle = ( currAngle + 180 ) % 360;
    turnToAngle( direction, nextAngle );
  }

  public void faceDirection( PresetDirection direction )
  {
    double currentAngle = AngleTools.angleForHeading( Math.toDegrees( pose.heading.toDouble() ) );
    telemetry.log().add( String.format( "currentAngle: %f", currentAngle ) );

    double nextHeading = AngleTools.headingForDirection( direction );
    telemetry.log().add( String.format( "nextHeading: %f", nextHeading ) );

    double nextAngle = AngleTools.angleForHeading( nextHeading );
    telemetry.log().add( String.format( "nextAngle: %f", nextAngle ) );

    RotateDirection rotateDirection = AngleTools.quickestDirection( currentAngle, nextAngle );
    telemetry.log().add( String.format( "rotateDirection: %s", rotateDirection ) );

    turnToAngle( rotateDirection, nextAngle );
  }

  private void turnToAngle( RotateDirection direction, double angle )
  {
    currentAction = CurrentAction.ROTATE;
    targetDirection = direction;
    targetAngle = angle;
  }

  public void coast()
  {
    setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
  }

  public void brake()
  {
    setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
  }

  private double computeRotateSpeed( double angleDifference )
  {
    double speed = 0.6;

    if( angleDifference < BREAKING_DISTANCE )
    {
      speed = 0.2;
    }

    return speed;
  }

  public void move( double forward, double strafe, double rotate )
  {
    if( perspective == Perspective.DRIVER )
    {
      double heading = pose.heading.toDouble();

      double rotX = strafe * Math.cos(-heading) - forward * Math.sin(-heading);
      double rotY = strafe * Math.sin(-heading) + forward * Math.cos(-heading);

      forward = rotY;
      strafe = rotX;
    }

    if( rotate != 0 )
    {
      currentAction = CurrentAction.DOING_NOTHING;
    }
    else if( currentAction == CurrentAction.ROTATE )
    {
      final double currAngle = AngleTools.angleForHeading( Math.toDegrees( pose.heading.toDouble() ) );
      final double angleDifference = AngleTools.angleDifference( currAngle, targetAngle );

      if( angleDifference < ANGLE_THRESHOLD )
      {
        currentAction = CurrentAction.DOING_NOTHING;
      }
      else
      {
        rotate = computeRotateSpeed( angleDifference );
        targetDirection = AngleTools.quickestDirection( currAngle, targetAngle );

        if( targetDirection == RotateDirection.RIGHT )
        {
          rotate *= -1;
        }
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

  public void updateState()
  {
    updatePose();
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
    telemetry.addLine( String.format( "Perspective: %s", perspective ) );

    if( localizer != null )
    {
//      telemetry.addLine().addData( "XL: ", "%s", localizer.par0.getPositionAndVelocity().position );
//      telemetry.addLine().addData( "XR: ", "%s", localizer.par1.getPositionAndVelocity().position );
//      telemetry.addLine().addData( "XS: ", "%s", localizer.perp.getPositionAndVelocity().position );

      telemetry.addLine().addData( "X: ", "%.1f", pose.position.x );
      telemetry.addLine().addData( "Y: ", "%.1f", pose.position.y );
      telemetry.addLine().addData( "Heading: ", "%.1f", Math.toDegrees( pose.heading.toDouble() ) );
    }
  }
}
