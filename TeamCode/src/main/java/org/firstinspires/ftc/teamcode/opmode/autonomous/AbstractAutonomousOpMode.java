package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.actions.ActionTools;
import org.firstinspires.ftc.teamcode.actions.MoveLift;
import org.firstinspires.ftc.teamcode.enums.Bar;
import org.firstinspires.ftc.teamcode.enums.Basket;
import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;
import org.firstinspires.ftc.teamcode.enums.Team;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Config
public abstract class AbstractAutonomousOpMode extends OpMode
{
  private final Team team;
  private final GameStrategy gameStrategy;

  private AutonomousState state = AutonomousState.HAVE_NOTHING;
  private int neutralSamples = 3;
  private int teamSamples = 3;
  private int specimensHung = 0;
  ElapsedTime time = null;
  List<LynxModule> hubs;
  JoeBot robot = null;

  //set to false to speed up debugging by ejecting samples
  //without operating the lift
  public static boolean enableLiftMotions = false;

  protected AbstractAutonomousOpMode( Team team, GameStrategy gameStrategy, AutonomousState startState )
  {
    this.team = team;
    this.gameStrategy = gameStrategy;
    this.state = startState;
  }

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    //print telemetry to Dashboard
    if( JoeBot.debugging )
    {
      telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
      telemetry.setAutoClear( false );
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
    //reset the timer when the game starts
    time.reset();

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

      case GRAB_SAMPLE:
        robot.clearBulkCache();
        robot.intake().updateState();
        if( !robot.intake().hasSample() )
        {
          robot.grabSample( false );
        }
        break;

      case GIVE_UP_SAMPLE:
        robot.clearBulkCache();
        robot.intake().updateState();
        if( robot.intake().hasSample() )
        {
          robot.giveUpSample();
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
    robot.debug( "Autonomous:level1Ascent" );
    final double faceRight = Math.toRadians( -90 );
    driveTo( Arrays.asList( new Pose2d( Location.NEAR_ASCENT_ZONE, faceRight ),
                            new Pose2d( Location.ASCENT_ZONE, faceRight ) ) );

    robot.levelOneAscent();
    state  = AutonomousState.PARKED;
  }

  private void park()
  {
    robot.debug( "Autonomous:park" );
    driveTo( new Pose2d( Location.OBSERVATION_ZONE, 0 ) );
    state = AutonomousState.PARKED;
  }

  private void basketStrategy()
  {
    if( state == AutonomousState.PARKED )
    { return; }

    if( timeRunningOut() )
    {
      robot.debug( "BasketAuto:timeRunningOut!" );
      level1Ascent();
    }
    else if( state == AutonomousState.HAVE_SPECIMEN )
    {
      robot.debug( "BasketAuto:HAVE_SPECIMEN -> hangSpecimen" );
      final double faceForward = 0;
      driveTo( new Pose2d( Location.SPECIMEN_BAR_LEFT, faceForward ) );

      if( enableLiftMotions )
      {
        robot.hangSpecimen( Bar.HIGH_BAR );
      }
      else
      {
        robot.giveUpSample();
      }

      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_SAMPLE )
    {
      robot.debug( "BasketAuto:HAVE_SAMPLE -> placeSampleInBasket" );
      final double faceBasket = Math.toRadians( 135 );
      driveTo( new Pose2d( Location.SAMPLE_BASKETS, faceBasket ) );

      if( enableLiftMotions )
      {
        robot.placeSampleInBasket( Basket.HIGH_BASKET );
      }
      else
      {
        robot.giveUpSample();
      }

      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_NOTHING )
    {
      if( neutralSamples <= 0 )
      {
        robot.debug( String.format( "BasketAuto:HAVE_NOTHING -> neutralSamplesLeft %s", neutralSamples ) );
        level1Ascent();
      }
      else
      {
        if( neutralSamples == 3 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo1" );
          final double faceForward = 0;
          driveTo( new Pose2d( Location.YELLOW_SAMPLE_1, faceForward ) );
        }
        else if( neutralSamples == 2 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo2" );
          final double faceBackwards = Math.PI;
          driveTo( Arrays.asList( new Pose2d( Location.NEAR_YELLOW_SAMPLES, faceBackwards ),
                                  new Pose2d( Location.YELLOW_SAMPLE_2, faceBackwards ) ) );
        }
        else if( neutralSamples == 1 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo3" );
          final double faceSample = Math.toRadians( 135 );
          driveTo( Arrays.asList( new Pose2d( Location.NEAR_YELLOW_SAMPLES, faceSample ),
                                  new Pose2d( Location.YELLOW_SAMPLE_2, faceSample ) ) );
        }

        robot.debug( "BasketAuto:HAVE_NOTHING -> grabSample" );
        robot.grabSample( false );
        neutralSamples--;

        robot.clearBulkCache();
        robot.intake().updateState();
        if( robot.intake().hasSample() )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> HAVE_SAMPLE" );
          state = AutonomousState.HAVE_SAMPLE;
        }
      }
    }
  }

  private void specimenStrategy()
  {
    if( state == AutonomousState.PARKED )
    { return; }

    if( timeRunningOut() )
    {
      robot.debug( "SpecimenAuto:timeRunningOut!" );
      park();
    }
    else if( state == AutonomousState.HAVE_SPECIMEN )
    {
      robot.debug( "SpecimenAuto:HAVE_SPECIMEN -> hangSpecimen" );
      Vector2d location = new Vector2d( Location.SPECIMEN_BAR_RIGHT.x,
                                        Location.SPECIMEN_BAR_RIGHT.y + 3 * specimensHung );
      driveTo( new Pose2d( location, 0 ) );
      if( enableLiftMotions )
      {
        robot.hangSpecimen( Bar.HIGH_BAR );
      }
      else
      {
        robot.giveUpSample();
      }
      specimensHung++;
      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_SAMPLE )
    {
      robot.debug( "SpecimenAuto:HAVE_SAMPLE -> giveUp/retrieve" );
      driveTo( new Pose2d( Location.OBSERVATION_ZONE, Math.PI ) );
      robot.giveUpSample();
      state = retrieveSpecimen() ?
              AutonomousState.HAVE_SPECIMEN :
              AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_NOTHING )
    {
      if( teamSamples <= 0 )
      {
        robot.debug( "SpecimenAuto:HAVE_NOTHING -> no team samples left" );
        park();
      }
      else
      {
        robot.debug( "SpecimenAuto:HAVE_NOTHING -> strafe and retrieve" );

        while( teamSamples > 0 )
        {
          Vector2d samplePos;
          if( teamSamples == 3 )
          {
            samplePos = Location.TEAM_SAMPLE_1;
          }
          else
          {
            samplePos = teamSamples == 2 ?
                        Location.TEAM_SAMPLE_2 :
                        Location.TEAM_SAMPLE_3;
          }

          final double faceLeft = Math.toRadians( 90 );
          driveTo( Arrays.asList( new Pose2d( Location.NEAR_TEAM_SAMPLES, faceLeft ),
                                  new Pose2d( samplePos, faceLeft ),
                                  new Pose2d( Location.OBSERVATION_ZONE, faceLeft ) ) );
          teamSamples--;
        }

        state = retrieveSpecimen() ?
                AutonomousState.HAVE_SPECIMEN :
                AutonomousState.HAVE_NOTHING;
      }
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
    for( Pose2d pose : poses )
    {
      trajectory = trajectory.strafeToLinearHeading( pose.position, pose.heading.toDouble() );
    }

    ActionTools.runBlocking( robot, trajectory.build() );
  }

  private boolean retrieveSpecimen()
  {
    driveTo( new Pose2d( Location.NEAR_THE_OBSERVATION_ZONE, Math.PI ) );
//    robot.wait( 1000 );

    while( !timeRunningOut() )
    {
      robot.grabSample( true );
      robot.updateState();
      if( robot.intake().hasSample() )
      { return true; }
    }

    return false;
  }

  private boolean timeRunningOut()
  {
    final int timeInMatch = 30;
    double timeElapsed = time.seconds();
    double timeLeft = timeInMatch - timeElapsed;

    if( timeLeft <= 4 )
    {
      robot.debug( String.format( "Autonomous::timeLeft %s", timeLeft ) );
      return true;
    }
    else
    {
      return false;
    }
  }

  private Vector2d defaultPos()
  {
    switch( gameStrategy )
    {
      case LEVEL_1_ASCENT:
      case PLACE_SAMPLES_IN_BASKETS:
        return Location.STARTING_POSITION_BASKETS;

      case PARK:
      case HANG_SPECIMENS_ON_BARS:
        return Location.STARTING_POSITION_SPECIMENS;

      case GRAB_SAMPLE:
      case GIVE_UP_SAMPLE:
      default:
        return new Vector2d( 0, 0 );
    }
  }

}
