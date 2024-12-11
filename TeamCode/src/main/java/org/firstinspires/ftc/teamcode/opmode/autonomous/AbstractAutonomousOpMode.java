package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
//import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Gamepads;
import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.actions.ActionTools;
import org.firstinspires.ftc.teamcode.actions.MoveLift;
import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;
import org.firstinspires.ftc.teamcode.enums.Team;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

import java.util.List;

public abstract class AbstractAutonomousOpMode extends OpMode
{
  protected final Team team;

  protected AutonomousState state = AutonomousState.HAVE_NOTHING;
  protected int neutralSamples = 3;
  protected int teamSamples = 3;
  protected int specimensHung = 0;
  protected ElapsedTime time = null;
  protected List<LynxModule> hubs;
  protected JoeBot robot = null;
  protected Gamepads gamepads = null;

  //set to false to speed up debugging by ejecting samples
  //without operating the lift
  public static boolean enableLiftMotions = true;

  protected AbstractAutonomousOpMode( Team team, AutonomousState startState )
  {
    this.team = team;
    this.state = startState;
  }

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    Intake.team = team;

    //print telemetry to Dashboard
    if( JoeBot.debugging )
    {
      telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    time = new ElapsedTime();

    //setup bulk reads
    hubs = hardwareMap.getAll( LynxModule.class );
    for( LynxModule module : hubs )
    {
      module.setBulkCachingMode( LynxModule.BulkCachingMode.MANUAL );
    }

    //force encoders to be reset at the beginning of autonomous
    AbstractModule.encodersReset = false;

    robot = new JoeBot( true, hardwareMap, telemetry );

    gamepads = new Gamepads( gamepad1, gamepad2 );

    //prevent resetting encoders again
    AbstractModule.encodersReset = true;

    telemetry.log().add( "Initialized Auto" );
    telemetry.update();

    //Allow robot to be pushed around before the start button is pressed
    robot.coast();
  }

  @Override
  public void start()
  {
    //clear screen
    telemetry.update();

    //Prevent robot from being pushed around
    robot.brake();

    //always reset the position and heading at the beginning of Autonomous
    telemetry.log().add( "Resetting Position and Heading" );
    robot.resetPos( defaultPos() );

    //update robot state including the color sensor
    robot.updateState( true );

    //raise list before driving to avoid dragging sample on the ground
    //if lift motions are enabled we'll immediately raise the lift before driving so
    //this step is not necessary
    if( !enableLiftMotions )
    {
      ActionTools.runBlocking( robot,
        new MoveLift( robot, Lift.Position.TRAVEL_WITH_SPECIMEN, 500 ) );
    }

    //reset the timer when the game starts
    time.reset();
  }

  @Override
  public void stop()
  {
    //store position so it can be restored when we start TeleOp
    robot.cachePos();
  }

  protected void level1Ascent()
  {
    robot.debug( "Autonomous:level1Ascent" );
    final double faceRight = Math.toRadians( -90 );
    driveTo( new Pose2d( Location.ASCENT_ZONE, faceRight ) ) ;
    robot.levelOneAscent();
    state = AutonomousState.PARKED;
  }

  protected void park()
  {
    robot.debug( "Autonomous:park" );
    driveTo( new Pose2d( Location.PARK_IN_OBSERVATION_ZONE, 0 ) );
    state = AutonomousState.PARKED;
  }

  protected void driveTo( Pose2d pose )
  {
    MecanumDrive drive = robot.mecanumDrive();
    ActionTools.runBlocking( robot, drive.actionBuilder( drive.pose )
      .strafeToLinearHeading( pose.position, pose.heading.toDouble() )
      .build() );
  }

  protected boolean retrieveSpecimen()
  {
    //give human player a change to position the specimen
//    ActionTools.runBlocking( robot, new SleepAction( 0.5 ) );

    driveTo( new Pose2d( Location.RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE, Math.PI ) );

    while( !timeRunningOut() )
    {
      robot.grabSample( true );
      robot.intake().updateState( true );
      if( robot.intake().hasSample() )
      { return true; }
      else
      {
        driveTo( new Pose2d( Location.NEAR_THE_OBSERVATION_ZONE, Math.PI ) );

        //give human player a change to position the specimen
//        ActionTools.runBlocking( robot, new SleepAction( 0.5 ) );

        driveTo( new Pose2d( Location.RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE, Math.PI ) );
      }
    }

    return false;
  }

  protected boolean timeRunningOut()
  {
    final int timeInMatch = 30;
    double timeElapsed = time.seconds();
    double timeLeft = timeInMatch - timeElapsed;

    if( timeLeft < minimumTime() )
    {
      robot.debug( String.format( "Autonomous::timeLeft %s", timeLeft ) );
      return true;
    }
    else
    {
      return false;
    }
  }

  //reimplement to indicate how much time in seconds
  //must remain before switch to parking
  protected abstract double minimumTime();

  //reimplement to indicate where the robot is starting
  //so that the position can be initialized
  protected abstract Vector2d defaultPos();
}
