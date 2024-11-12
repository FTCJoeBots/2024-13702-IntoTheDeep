package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.actions.ActionTools;
import org.firstinspires.ftc.teamcode.actions.MoveExtensionArm;
import org.firstinspires.ftc.teamcode.actions.MoveLift;
import org.firstinspires.ftc.teamcode.actions.OperateIntake;
import org.firstinspires.ftc.teamcode.enums.Bar;
import org.firstinspires.ftc.teamcode.enums.Basket;
import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.enums.Team;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractAutonomousOpMode extends OpMode
{
  private final Team team;
  private final GameStrategy gameStrategy;
  private AutonomousState state = AutonomousState.HAVE_SPECIMEN;
  private int neutralSamplesLeft = 6;
  private int teamSamplesLeft = 3;
  private int numberHung = 0;
  ElapsedTime time = null;
  List<LynxModule> hubs;
  JoeBot robot = null;

  protected AbstractAutonomousOpMode( Team team, GameStrategy gameStrategy )
  {
    this.team = team;
    this.gameStrategy = gameStrategy;
  }

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
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

    //always reset the position and heading at the beginning of Autonomous
    telemetry.addLine( "Resetting Position" );
    robot.resetPos( defaultPos() );

    telemetry.addLine( "Initialized Auto" );
    telemetry.update();
  }

  @Override
  public void init_loop()
  {
    //Allow robot to be pushed around before the start button is pressed
    robot.coast();
  }

  @Override
  public void start()
  {
    //Prevent robot from being pushed around
    robot.brake();

    //raise lift so that the speicen does not drag and slow down the robot
    ActionTools.runBlocking( robot,
      new SequentialAction(
        new MoveLift( robot, Lift.Position.TRAVEL_WITH_SPECIMEN, 500 )
      )
    );
  }

  @Override
  public void loop()
  {
    switch( gameStrategy )
    {
      case PARK:
        if( state != AutonomousState.PARKED )
        {
          park();
        }
        break;
      case LEVEL_1_ASCENT:
        if( state != AutonomousState.PARKED )
        {
          level1Ascent();
        }
        break;
      case PLACE_SAMPLES_IN_BASKETS:
         basketStrategy();
         break;
      case HANG_SPECIMENS_ON_BARS:
        specimenStrategy();
        break;
    }
  }

  @Override
  public void stop()
  {
    //store position so it can be restored when we start TeleOp
    robot.cachePos();

    //prevent resetting encoders when switching to TeleOp
    AbstractModule.encodersReset = true;
  }

  private void level1Ascent()
  {
    driveTo( Arrays.asList( new Pose2d( Location.NEAR_ASCENT_ZONE.value, Math.toRadians( -90 ) ),
                            new Pose2d( Location.ASCENT_ZONE.value, Math.toRadians( -90 ) ) ) );
    robot.extensionArm().travelTo( ExtensionArm.Position.EXTEND_TO_TOUCH_BAR.value );
    state  = AutonomousState.PARKED;
  }

  private void park()
  {
    driveTo( new Pose2d( Location.OBSERVATION_ZONE.value, Math.toRadians( 0 ) ) );
    state  = AutonomousState.PARKED;
  }

  private void basketStrategy()
  {
    if( state == AutonomousState.HAVE_SPECIMEN )
    {
      driveTo( new Pose2d( Location.SPECIMEN_BAR_LEFT.value, 0 ) );
      robot.hangSpecimen( Bar.HIGH_BAR );
      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_SAMPLE )
    {
      driveTo( new Pose2d( Location.SAMPLE_BASKETS.value, 135 ) );
      robot.placeSampleInBasket( Basket.HIGH_BASKET );
      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_NOTHING )
    {
      if( neutralSamplesLeft < 4 ||
          timeRunningOut() )
      {
        level1Ascent();
      }
      else
      {
        if( neutralSamplesLeft == 6 )
        {
          driveTo( new Pose2d( Location.YELLOW_SAMPLE_1.value, 0 ) );
        }
        else if( neutralSamplesLeft == 5 )
        {
          driveTo( Arrays.asList( new Pose2d( Location.NEAR_YELLOW_SAMPLES.value, Math.toRadians( 180 ) ),
                                  new Pose2d( Location.YELLOW_SAMPLE_2.value, Math.toRadians( 180 ) ) ) );
        }
        else if( neutralSamplesLeft == 4 )
        {
          driveTo( Arrays.asList( new Pose2d( Location.NEAR_YELLOW_SAMPLES.value, Math.toRadians( 135 ) ),
                                  new Pose2d( Location.YELLOW_SAMPLE_2.value, Math.toRadians( 135 ) ) ) );
        }

        robot.grabSample( false );
        neutralSamplesLeft--;

        if( robot.intake().hasSample() )
        {
          state = AutonomousState.HAVE_SAMPLE;
        }
      }
    }
  }

  private void specimenStrategy()
  {
    if( state == AutonomousState.HAVE_SPECIMEN )
    {
      Vector2d location = new Vector2d( Location.SPECIMEN_BAR_RIGHT.value.x,
                                        Location.SPECIMEN_BAR_RIGHT.value.y + 5 * numberHung );
      driveTo( new Pose2d( location, 0 ) );
      robot.hangSpecimen( Bar.HIGH_BAR );
      numberHung++;
      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_SAMPLE )
    {
      driveTo( new Pose2d( Location.OBSERVATION_ZONE.value, 180 ) );
      robot.giveUpSample();
      state = retrieveSpecimen() ?
              AutonomousState.HAVE_SPECIMEN :
              AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_NOTHING )
    {
      if( teamSamplesLeft <= 0 ||
        timeRunningOut() )
      {
        park();
      }

      if( teamSamplesLeft == 3 )
      {
        driveTo( Arrays.asList( new Pose2d( Location.NEAR_TEAM_SAMPLES.value, Math.toRadians( -90 ) ),
                                new Pose2d( Location.TEAM_SAMPLE_1.value, Math.toRadians( -90 ) ),
                                new Pose2d( Location.OBSERVATION_ZONE.value, Math.toRadians( -90 ) ) ) );
      }
      else if( neutralSamplesLeft == 2 )
      {
        driveTo( Arrays.asList( new Pose2d( Location.NEAR_TEAM_SAMPLES.value, Math.toRadians( -90 ) ),
                                new Pose2d( Location.TEAM_SAMPLE_2.value, Math.toRadians( -90 ) ),
                                new Pose2d( Location.OBSERVATION_ZONE.value, Math.toRadians( -90 ) ) ) );
      }
      else
      {
        driveTo( Arrays.asList( new Pose2d( Location.NEAR_TEAM_SAMPLES.value, Math.toRadians( -90 ) ),
                                new Pose2d( Location.TEAM_SAMPLE_1.value, Math.toRadians( -90 ) ),
                                new Pose2d( Location.OBSERVATION_ZONE.value, Math.toRadians( -90 ) ) ) );
      }

      teamSamplesLeft--;
      state = retrieveSpecimen() ?
              AutonomousState.HAVE_SPECIMEN :
              AutonomousState.HAVE_NOTHING;
    }
  }

  private void driveTo( Pose2d pose )
  {
    driveTo( Collections.singletonList( pose ) );
  }

  private void driveTo( List<Pose2d> poses )
  {
    MecanumDrive drive = robot.mecanumDrive();

    TrajectoryActionBuilder trajectory = drive.actionBuilder( drive.pose );
    trajectory.setTangent( 0 );
    for( Pose2d pose : poses )
    {
      trajectory = trajectory.strafeToLinearHeading( pose.position, pose.heading.toDouble() );
    }

    ActionTools.runBlocking( robot, trajectory.build() );
  }

  private boolean retrieveSpecimen()
  {
    driveTo( new Pose2d( Location.NEAR_THE_OBSERVATION_ZONE.value, 180 ) );
    robot.wait( 1000 );

    for( int i = 0; i < 3; i++ )
    {
      robot.grabSample( true );

      if( robot.intake().hasSample() )
      { return true; }
    }

    return false;
  }

  private boolean timeRunningOut()
  {
    int timeInMatch = 30;
    double timeEllapsed = time.seconds();
    double timeLeft = timeInMatch - timeEllapsed;
    return timeLeft <= 3;
  }

  private Vector2d defaultPos()
  {
    switch( gameStrategy )
    {
      case LEVEL_1_ASCENT:
      case PLACE_SAMPLES_IN_BASKETS:
        return Location.STARTING_POSITION_BASKETS.value;

      case PARK:
      case HANG_SPECIMENS_ON_BARS:
        return Location.STARTING_POSITION_SPECIMENS.value;

      default:
        return new Vector2d( 0, 0 );
    }
  }

}
