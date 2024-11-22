package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Gamepads;
import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.actions.ActionTools;
import org.firstinspires.ftc.teamcode.actions.MoveLift;
import org.firstinspires.ftc.teamcode.enums.Bar;
import org.firstinspires.ftc.teamcode.enums.Basket;
import org.firstinspires.ftc.teamcode.enums.Button;
import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.enums.Participant;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;
import org.firstinspires.ftc.teamcode.enums.Team;
import org.firstinspires.ftc.teamcode.modules.Intake;
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
  private ElapsedTime time = null;
  private List<LynxModule> hubs;
  private JoeBot robot = null;
  private Gamepads gamepads = null;

  //set to false to speed up debugging by ejecting samples
  //without operating the lift
  public static boolean enableLiftMotions = true;

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

    //always reset the position and heading at the beginning of Autonomous
    telemetry.log().add( "Resetting Position" );
    robot.resetPos( defaultPos() );

    telemetry.log().add( "Initialized Auto" );
    telemetry.update();

    //Allow robot to be pushed around before the start button is pressed
    robot.coast();
  }

  @Override
  public void init_loop()
  {
    if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.GUIDE ) )
    {
      final AutonomousState[] states = AutonomousState.values();
      state = states[ state.ordinal() < states.length - 1 ?
                      state.ordinal() + 1 :
                      0 ];
    }

    final Pose2d pose = robot.mecanumDrive().pose;
    telemetry.addLine( String.format( "%s", state ) );
    telemetry.addLine( String.format( "X: %.1f", pose.position.x ) );
    telemetry.addLine( String.format( "Y: %.1f", pose.position.y ) );
    telemetry.addLine( String.format( "Heading: %.1f", Math.toDegrees( pose.heading.toDouble() ) ) );
    telemetry.update();

    gamepads.storeLastButtons();
  }

  @Override
  public void start()
  {
    //reset the timer when the game starts
    time.reset();

    JoeBot.competition = true;

    //clear screen
    telemetry.update();

    //Prevent robot from being pushed around
    robot.brake();

    robot.updateState();

    //raise list before driving to avoid dragging sample on the ground
    //if lift motions are enabled we'll immediately raise the lift before driving so
    //this step is not necessary
    if( !enableLiftMotions )
    {
      ActionTools.runBlocking( robot,
        new MoveLift( robot, Lift.Position.TRAVEL_WITH_SPECIMEN, 500 ) );
    }
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
        robot.intake().updateState( true );
        if( !robot.intake().hasSample() )
        {
          robot.grabSample( false );
        }
        break;

      case GIVE_UP_SAMPLE:
        robot.intake().updateState( true );
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
  }

  private void level1Ascent()
  {
    robot.debug( "Autonomous:level1Ascent" );
    final double faceRight = Math.toRadians( -90 );
    driveTo( new Pose2d( Location.ASCENT_ZONE, faceRight ) ) ;
    robot.levelOneAscent();
    state = AutonomousState.PARKED;
  }

  private void park()
  {
    robot.debug( "Autonomous:park" );
    driveTo( new Pose2d( Location.PARK_IN_OBSERVATION_ZONE, 0 ) );
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
      robot.lift().travelTo( Lift.Position.ABOVE_HIGH_SPECIMEN_BAR );
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
      robot.lift().travelTo( Lift.Position.HIGH_BASKET );
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
      //we're not fast enough to get all three yellow samples so once we have picked up to
      //perform a level 1 ascent if there is still time
      if( neutralSamples <= 1 )
      {
        robot.debug( String.format( "BasketAuto:HAVE_NOTHING -> neutralSamplesLeft %s", neutralSamples ) );
        level1Ascent();
      }
      else
      {
        //start moving lift
        robot.lift().travelTo( Lift.Position.SAMPLE_FLOOR );

        if( neutralSamples == 3 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo1" );
          final double faceForward = 0;
          driveTo( new Pose2d( Location.YELLOW_SAMPLE_1, faceForward ) );
        }
        else if( neutralSamples == 2 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo2" );
          final double faceForward = 0;
          driveTo( new Pose2d( Location.YELLOW_SAMPLE_2, faceForward ) );
        }
        else if( neutralSamples == 1 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo3" );
          final double faceSample = Math.toRadians( 45 );
          driveTo( new Pose2d( Location.YELLOW_SAMPLE_3, faceSample ) );
        }

        if( timeRunningOut() )
        {
          robot.debug( "BasketAuto:timeRunningOut!" );
          level1Ascent();
          return;
        }

        robot.debug( "BasketAuto:HAVE_NOTHING -> grabSample" );
        robot.grabSample( false );
        neutralSamples--;

        robot.intake().updateState( true );
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
      Vector2d location = new Vector2d( Location.SPECIMEN_BAR_RIGHT.x + 2 * specimensHung,
                                        Location.SPECIMEN_BAR_RIGHT.y + 3 * specimensHung );
      if( enableLiftMotions )
      {
        robot.lift().travelTo( Lift.Position.ABOVE_HIGH_SPECIMEN_BAR );
      }

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

        Vector2d strafePos = new Vector2d( Location.OBSERVATION_ZONE.x, samplePos.y );

        driveTo( Arrays.asList( new Pose2d( Location.NEAR_TEAM_SAMPLES_1, faceLeft ),
                                new Pose2d( Location.NEAR_TEAM_SAMPLES_2, faceLeft ) ) );

        driveTo( new Pose2d( samplePos, faceLeft ) );
        driveTo( new Pose2d( strafePos, faceLeft ) );

        teamSamples--;

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
    driveTo( new Pose2d( Location.IN_THE_OBSERVATION_ZONE, Math.PI ) );

    while( !timeRunningOut() )
    {
      robot.grabSample( true );
      robot.intake().updateState( true );
      if( robot.intake().hasSample() )
      { return true; }
      else
      {
        driveTo( new Pose2d( Location.NEAR_THE_OBSERVATION_ZONE, Math.PI ) );
      }
    }

    return false;
  }

  private boolean timeRunningOut()
  {
    final int timeInMatch = 30;
    double timeElapsed = time.seconds();
    double timeLeft = timeInMatch - timeElapsed;

    int minimumTime = 6;
    switch( gameStrategy )
    {
      case PLACE_SAMPLES_IN_BASKETS:
        minimumTime = 4;
        break;

      case HANG_SPECIMENS_ON_BARS:
        minimumTime = 4;
        break;

      default:
        minimumTime = 6;
        break;
    }

    if( timeLeft < minimumTime )
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
