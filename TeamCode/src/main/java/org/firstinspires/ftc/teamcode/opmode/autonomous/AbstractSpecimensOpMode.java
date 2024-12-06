package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.actions.ActionTools;
import org.firstinspires.ftc.teamcode.enums.Bar;
import org.firstinspires.ftc.teamcode.enums.Button;
import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.enums.Participant;
import org.firstinspires.ftc.teamcode.enums.Team;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

public abstract class AbstractSpecimensOpMode extends AbstractAutonomousOpMode
{
  private enum Setting
  {
    STATE,
    SPLINE_PATHS,
    STRAFE_TWO,
  }

  private Setting currentSetting = Setting.STATE;
  private boolean useSplinePaths = false;
  private boolean strafeTwo = false;

  protected AbstractSpecimensOpMode( Team team )
  {
    super( team, AutonomousState.HAVE_SPECIMEN );
  }

  @Override
  protected Vector2d defaultPos()
  {
    return Location.STARTING_POSITION_SPECIMENS;
  }

  @Override
  protected double minimumTime()
  {
    //never park, try to hang as many specimens as possible
    return 0;
  }

  @Override
  public void init()
  {
    super.init();

    currentSetting = Setting.STATE;
    useSplinePaths = false;
    strafeTwo = false;
  }

  @Override
  public void init_loop()
  {
    if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_UP ) ||
        gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_DOWN ) )
    {
      final Setting[] settings = Setting.values();
      int index = currentSetting.ordinal();

      if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_UP ) &&
          index > 0 )
      {
        currentSetting = settings[ index - 1 ];
      }
      else if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_DOWN ) &&
               index + 1 < settings.length )
      {
        currentSetting = settings[ index + 1 ];
      }
    }
    else if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_DOWN ) )
    {
      final Setting[] settings = Setting.values();
      int index = currentSetting.ordinal();

      if( index > 0 )
      {
        currentSetting = settings[ index - 1 ];
      }
    }
    else if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_LEFT ) ||
             gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_RIGHT ) ||
             gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.GUIDE ) )
    {
      if( currentSetting == Setting.STATE )
      {
        final AutonomousState[] states = AutonomousState.values();
        int index = state.ordinal();
        if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_LEFT ) )
        {
          state = states[ index > 0 ?
                  index - 1 :
                  states.length - 1 ];
        }
        else
        {
          state = states[ index + 1 < states.length ?
                  index + 1 :
                  0 ];
        }
      }
      else if( currentSetting == Setting.SPLINE_PATHS )
      {
        useSplinePaths = !useSplinePaths;
      }
      else if( currentSetting == Setting.STRAFE_TWO )
      {
        strafeTwo = !strafeTwo;
      }
    }

    telemetry.addLine( formatSetting( Setting.STATE,        String.format( "Initial State: %s", state ) ) );
    telemetry.addLine( formatSetting( Setting.SPLINE_PATHS, String.format( "Spline Paths:  %b", useSplinePaths ) ) );
    telemetry.addLine( formatSetting( Setting.STRAFE_TWO,   String.format( "Strafe Two:    %b", strafeTwo ) ) );
    telemetry.update();
    gamepads.storeLastButtons();
  }

  private String formatSetting( Setting setting, String message )
  {
    return String.format( "%c ", setting == currentSetting ? '>' : ' ' ) + message;
  }

  @Override
  public void loop()
  {
    if( state == AutonomousState.PARKED )
    { return; }

    if( timeRunningOut() ||
        specimensHung >= 3 )
    {
      robot.debug( "SpecimenAuto:parking!" );
      park();
    }
    else if( state == AutonomousState.HAVE_SPECIMEN )
    {
      robot.debug( "SpecimenAuto:HAVE_SPECIMEN -> hangSpecimen" );
      Vector2d location = new Vector2d( Location.SPECIMEN_BAR_RIGHT.x + specimensHung,
                                        Location.SPECIMEN_BAR_RIGHT.y + 6 * specimensHung );
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
      driveTo( new Pose2d( Location.RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE, Math.PI ) );
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

        final double faceLeft = Math.toRadians( 90 );

        //start moving the lift so it is ready by the time we go to grab a specimen
        robot.lift().travelTo( Lift.Position.SPECIMEN_FLOOR );

        MecanumDrive drive = robot.mecanumDrive();

        //strafe in the first team sample
        if( teamSamples == 3 )
        {
          Vector2d strafePos = new Vector2d( Location.STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, Location.TEAM_SAMPLE_1.y );

          if( useSplinePaths )
          {
            final double faceUp = Math.toRadians( 0 );
            final double faceRight = Math.toRadians( -90 );

            final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 21, -28 );
            final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 47, -36 );
            final Vector2d TEAM_SAMPLE_1 = new Vector2d( 54, -44 );

            ActionTools.runBlocking( robot, drive.actionBuilder( drive.pose )
              .strafeToLinearHeading( NEAR_TEAM_SAMPLES_1, faceUp )
              .splineToConstantHeading( NEAR_TEAM_SAMPLES_2, faceUp )
              .splineToSplineHeading( new Pose2d( TEAM_SAMPLE_1, faceRight ), 0 )
              .strafeToLinearHeading( strafePos, faceRight )
              .build() );
          }
          else
          {
            ActionTools.runBlocking( robot, drive.actionBuilder( drive.pose )
              .strafeToLinearHeading( Location.NEAR_TEAM_SAMPLES_1, faceLeft )
              .strafeToLinearHeading( Location.NEAR_TEAM_SAMPLES_2, faceLeft)
              .strafeToLinearHeading( Location.TEAM_SAMPLE_1, faceLeft )
              .strafeToLinearHeading( strafePos, faceLeft)
                .build() );
          }

          teamSamples--;
        }
        else if( teamSamples == 2 &&
                 strafeTwo )
        {
          if( useSplinePaths )
          {
            final double faceRight = Math.toRadians( -90 );

            final Vector2d TEAM_SAMPLE_1 = new Vector2d( 54, -44 );
            final Vector2d TEAM_SAMPLE_2 = new Vector2d( 54, -54 );

            Vector2d strafePos1 = new Vector2d( Location.STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, TEAM_SAMPLE_1.y );
            Vector2d strafePos2 = new Vector2d( Location.STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, TEAM_SAMPLE_2.y );

            ActionTools.runBlocking( robot, drive.actionBuilder( drive.pose )
              .strafeToLinearHeading( new Vector2d( strafePos1.x + 20,
                                      strafePos1.y + 2 ), faceRight )
              .splineToConstantHeading( TEAM_SAMPLE_2, faceRight )
              .strafeToLinearHeading( strafePos2, faceRight )
              .build() );
          }
          else
          {
            Vector2d strafePos = new Vector2d( Location.STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, Location.TEAM_SAMPLE_2.y );

            ActionTools.runBlocking( robot, drive.actionBuilder( drive.pose )
              .strafeToLinearHeading( Location.TEAM_SAMPLE_1, faceLeft )
              .strafeToLinearHeading( Location.TEAM_SAMPLE_2, faceLeft )
              .strafeToLinearHeading( strafePos, faceLeft)
              .build() );
          }

          teamSamples--;
        }
        else
        {
          driveTo( new Pose2d( Location.NEAR_THE_OBSERVATION_ZONE, Math.PI ) );

          state = retrieveSpecimen() ?
            AutonomousState.HAVE_SPECIMEN :
            AutonomousState.HAVE_NOTHING;
        }
      }
    }
  }

}
