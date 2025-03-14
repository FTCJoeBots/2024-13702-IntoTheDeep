package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.actions.ActionTools;
import org.firstinspires.ftc.teamcode.actions.GiveUpSample;
import org.firstinspires.ftc.teamcode.actions.GrabSample;
import org.firstinspires.ftc.teamcode.actions.MoveExtensionArm;
import org.firstinspires.ftc.teamcode.actions.MoveLift;
import org.firstinspires.ftc.teamcode.actions.MoveLiftToClimb;
import org.firstinspires.ftc.teamcode.actions.OperateClimbArm;
import org.firstinspires.ftc.teamcode.actions.OperateIntake;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;
import org.firstinspires.ftc.teamcode.modules.ClimbArm;
import org.firstinspires.ftc.teamcode.modules.drive.AngleTools;
import org.firstinspires.ftc.teamcode.modules.drive.Drive;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.enums.Bar;
import org.firstinspires.ftc.teamcode.enums.Basket;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

import java.util.List;

public class JoeBot
{
  private Telemetry telemetry = null;
  private ExtensionArm extensionArm = null;
  private Lift lift = null;
  private Intake intake = null;
  private ClimbArm climbArm = null;

  public volatile Gamepads gamepads = null;

  private List<LynxModule> hubs;
  private MecanumDrive mecanumDrive = null;
  private Drive drive = null;
  private IMU imu = null;

  private static Pose2d pose = new Pose2d( 0, 0, 0 );

  public static boolean debugging = true;

  public JoeBot( boolean forAutonomous,
                 HardwareMap hardwareMap,
                 Telemetry telemetry )
  {
    if( !AbstractModule.encodersReset )
    { telemetry.addLine( "Resetting Encoders" ); }

    this.telemetry = telemetry;
    extensionArm = new ExtensionArm( hardwareMap, telemetry );
    lift = new Lift( hardwareMap, telemetry );
    intake = new Intake( hardwareMap, telemetry );
    climbArm = new ClimbArm( hardwareMap, telemetry );

    if( forAutonomous )
    {
      mecanumDrive = new MecanumDrive( hardwareMap, pose );
    }
    else
    {
      drive = new Drive( hardwareMap, telemetry, pose );
    }

    imu = hardwareMap.get( IMU.class, "imu" );

    RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot( RevHubOrientationOnRobot.LogoFacingDirection.RIGHT, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD );
    imu.initialize( new IMU.Parameters( orientationOnRobot ) );

    //setup bulk caching AFTER we create the MecanumDrive since when tuning
    //RoadRunner they prefer to run using Auto instead of Manual mode
    hubs = hardwareMap.getAll( LynxModule.class );
    setupBulkCaching();
  }

  public void debug( String message )
  {
    if( debugging )
    {
      telemetry.log().add( message );
      telemetry.update();
    }
  }

  private void setupBulkCaching()
  {
    for( LynxModule module : hubs )
    {
      module.setBulkCachingMode( LynxModule.BulkCachingMode.MANUAL );
    }
  }

  public void clearBulkCache()
  {
    for( LynxModule module : hubs )
    {
      module.clearBulkCache();
    }
  }

  public Telemetry telemetry()
  { return telemetry; }

  public ExtensionArm extensionArm()
  { return extensionArm; }

  public Lift lift()
  { return lift; }

  public Intake intake()
  { return intake; }

  public Drive drive()
  { return drive; }

  public MecanumDrive mecanumDrive()
  { return mecanumDrive; }

  public ClimbArm climbArm()
  { return climbArm; }

  public IMU imu()
  { return imu; }

  public void coast()
  {
    if( drive != null )
    {
      drive.coast();
    }

    if( mecanumDrive != null )
    {
      mecanumDrive.leftFront.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
      mecanumDrive.leftBack.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
      mecanumDrive.rightBack.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
      mecanumDrive.rightFront.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
    }
  }

  public void brake()
  {
    if( drive != null )
    {
      drive.brake();
    }

    if( mecanumDrive != null )
    {
      mecanumDrive.leftFront.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      mecanumDrive.leftBack.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      mecanumDrive.rightBack.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      mecanumDrive.rightFront.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    }
  }

  public void stop()
  {
    extensionArm.stop();
    lift.stop();
    intake.stop();
    climbArm.stop();

    stopDrive();
  }

  public void resetPos( Vector2d position )
  {
    JoeBot.pose = new Pose2d( position, 0 );

    if( drive != null )
    { drive.resetPose( pose ); }

    if( mecanumDrive != null )
    { mecanumDrive.pose = pose; }

    if( imu != null )
    { imu.resetYaw(); }
  }

  public void automaticallyResetHeadingUsingIMU()
  {
    Pose2d pose = mecanumDrive != null ?
                  mecanumDrive.pose :
                  drive.getPos();

    final double deadWheelHeading = Math.toDegrees( pose.heading.toDouble() );

    final YawPitchRollAngles imuAngles = imu.getRobotYawPitchRollAngles();

    //acquisition time will be 0 if IMU is unresponsive
    if( imuAngles.getAcquisitionTime() == 0 )
    { return; }

    final double imuHeading = imuAngles.getYaw( AngleUnit.DEGREES );
    final double angleDifference = AngleTools.angleDifference( deadWheelHeading, imuHeading );

    if( angleDifference > 0.5 )
    {
      telemetry.log().add( "angleDifference: %f", angleDifference );
      telemetry.log().add( "Resetting heading from %f to %f", deadWheelHeading, imuHeading );

      Pose2d updatedPose = new Pose2d( pose.position, Math.toRadians( imuHeading ) );

      if( mecanumDrive != null )
      { mecanumDrive.pose = updatedPose; }
      else
      { drive.resetPose( updatedPose ); }
    }
  }

  public void cachePos()
  {
    if( drive != null )
    {
      pose = drive.getPos();
    }
    else if( mecanumDrive != null )
    {
      pose = mecanumDrive.pose;
    }
  }

  public void updateState()
  {
    updateState( false );
  }

  public void updateState( boolean force )
  {
    clearBulkCache();

    if( drive != null )
    {
      drive.updateState();
    }

    lift.updateState();
    extensionArm.updateState();
    climbArm.updateState();
    intake.updateState( force );

    //a specimen could jam and prevent it going all the way down
    lift.allowReset = !intake.hasSample();
  }

  public void grabSample( boolean isSpecimen )
  {
    debug( String.format( "JoeBot::grabSample isSpecimen=%s", isSpecimen ) );

    ActionTools.runBlocking( this,
      new SequentialAction(
        new MoveLift( this,
          isSpecimen ?
            Lift.Position.SPECIMEN_FLOOR :
            Lift.Position.SAMPLE_FLOOR ),
        new GrabSample( this, isSpecimen ),
        new ParallelAction(
          new MoveLift( this, Lift.Position.TRAVEL_WITH_SPECIMEN ),
          new MoveExtensionArm( this, ExtensionArm.Position.RETRACTED_WITH_SAMPLE.value, ExtensionArm.Speed.FAST.value, 500 )
        )
      )
    );

    automaticallyResetHeadingUsingIMU();
  }

  public void retrieveSample()
  {
    debug( "JoeBot::retrieveSample" );

    ActionTools.runBlocking( this,
      new SequentialAction(
        new MoveLift( this, Lift.Position.TRAVEL_WITH_SPECIMEN ),
        new MoveExtensionArm( this, ExtensionArm.Position.RETRACTED_WITH_SAMPLE.value )
      )
    );

    automaticallyResetHeadingUsingIMU();
  }

  public void giveUpSample()
  {
    debug( "JoeBot::giveUpSample" );

    //Prevent robot from moving while the motion if being performed
    stopDrive();

    ActionTools.runBlocking( this, new GiveUpSample( this ) );
  }

  public void placeSampleInBasket( Basket basket )
  {
    debug( String.format( "JoeBot::placeSampleInBasket %s", basket ) );

    //Prevent robot from moving while the motion if being performed
    stopDrive();

    clearBulkCache();
    final int currentPosition = extensionArm.getMotorPosition();
    final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_DUMP_IN_BASKET.value;

    Lift.Position raisePosition = basket == Basket.HIGH_BASKET ?
      Lift.Position.HIGH_BASKET :
      Lift.Position.LOW_BASKET;

    MoveLift moveUp = new MoveLift( this, raisePosition, 8000 );

    //avoid pause, start moving in while lift is still going up the last bit
    moveUp.minimumHeight = raisePosition.value - 30;

    ActionTools.runBlocking( this,
      new SequentialAction(
        moveUp,
        new MoveExtensionArm( this, extendedPosition ),
        new OperateIntake( this, Intake.Direction.PUSH, 2000 ),
        new MoveExtensionArm( this, ExtensionArm.Position.FULLY_RETRACTED.value, ExtensionArm.Speed.FAST.value, 500 ),
        new MoveLift( this, Lift.Position.SAMPLE_FLOOR, 0 )
      )
    );

    automaticallyResetHeadingUsingIMU();
    updateState( true );
  }

  public void hangSpecimen( Bar bar )
  {
    debug( String.format( "JoeBot::hangSpecimen %s", bar ) );

    //Prevent robot from moving while the motion if being performed
    stopDrive();

    clearBulkCache();
    final int currentPosition = extensionArm.getMotorPosition();
    final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_HANG_SAMPLE.value;

    final Lift.Position abovePosition =
      bar == Bar.HIGH_BAR ?
        Lift.Position.ABOVE_HIGH_SPECIMEN_BAR :
        Lift.Position.ABOVE_LOW_SPECIMEN_BAR;

    final Lift.Position clippedPosition =
      bar == Bar.HIGH_BAR ?
        Lift.Position.SPECIMEN_CLIPPED_ONTO_HIGH_BAR :
        Lift.Position.SPECIMEN_CLIPPED_ONTO_LOW_BAR;

    MoveLift moveUp = new MoveLift( this, abovePosition, 6000 );

    //avoid pause, start moving in while lift is still going up the last bit
    moveUp.minimumHeight = abovePosition.value - 30;

    ActionTools.runBlocking( this,
      new SequentialAction(
        //raise lift above bar
        moveUp,
        //extend past bar
        new MoveExtensionArm( this, extendedPosition, ExtensionArm.Speed.FAST.value, 500  ),
        //drop down so when we pull back the specimen will be clipped to the bar
        new MoveLift( this, clippedPosition, 1000 ),
        new MoveExtensionArm( this, ExtensionArm.Position.FULLY_RETRACTED.value, ExtensionArm.Speed.HANG_SPECIMEN.value, 500 ),
        new MoveLift( this, Lift.Position.SPECIMEN_FLOOR, 0 )
      )
    );

    automaticallyResetHeadingUsingIMU();
    updateState( true );
  }

  public void levelOneAscent()
  {
    debug( "JotBot::levelOneAscent()" );

    //prevent slipping gears on the extension arm by letting the robot move backwards as the
    //extension arm comes in contact with the submersible frame
    coast();

    clearBulkCache();

    ActionTools.runBlocking( this,
      new SequentialAction(
        new MoveLift( this, Lift.Position.AT_LOW_HANG_BAR, 1000 ),
        new MoveExtensionArm( this, ExtensionArm.Position.EXTEND_TO_TOUCH_BAR.value )
      )
    );

    //restore normal drive system behavior
    brake();
  }

  public void levelTwoAscent()
  {
    debug( "JotBot::levelTwoAscent()" );

    //Prevent robot from moving while the motion if being performed
    stopDrive();

    //save power by making drive wheels coast
    coast();

    clearBulkCache();

    ActionTools.runBlocking( this,
      new SequentialAction(
        new MoveLift( this, Lift.Position.ABOVE_ABOVE_HANG_BAR, 6000 ),
        new MoveExtensionArm( this, ExtensionArm.Position.EXTEND_TO_CLIMB.value, ExtensionArm.Speed.FAST.value, 500 ),
        new MoveLiftToClimb( this ),
        new MoveExtensionArm( this, ExtensionArm.Position.RETRACT_TO_CLIMB.value, ExtensionArm.Speed.FAST.value, 500 ),
        new OperateClimbArm( this )
      )
    );

  }

  private void stopDrive()
  {
    if( drive != null )
    { drive.stop(); }
  }

}
