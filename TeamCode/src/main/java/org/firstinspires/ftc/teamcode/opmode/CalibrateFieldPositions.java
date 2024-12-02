package org.firstinspires.ftc.teamcode.opmode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Gamepads;
import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.actions.ActionTools;
import org.firstinspires.ftc.teamcode.enums.Button;
import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.enums.Participant;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

//Tell framework that this is a TeleOp mode
@TeleOp( name = "Calibrate Field Positions", group = "Iterative Opmode" )
public class CalibrateFieldPositions extends OpMode
{
  private JoeBot robot = null;

  private Gamepads gamepads = null;
  private int targetIndex = 0;
  private final int numTargets = Location.NamedLocation.values().length;

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    robot = new JoeBot( true, hardwareMap, telemetry );

    //Allow robot to be pushed around before the start button is pressed
    robot.coast();

    gamepads = new Gamepads( gamepad1, gamepad2 );

    robot.enableSoundEffects = true;
    robot.playSound( JoeBot.Sound.CALIBRATE_INIT, true );
  }

  @Override
  public void init_loop()
  {
    robot.clearBulkCache();
    robot.mecanumDrive().updatePoseEstimate();
    printPose();
    telemetry.update();
  }

  private void printPose()
  {
    final Pose2d pose = robot.mecanumDrive().pose;
    telemetry.addLine( String.format( "X,Y = %.1f, %.1f  Heading = %.1f", pose.position.x, pose.position.y, Math.toDegrees( pose.heading.toDouble() ) ) );

    final double yaw = robot.imu().getRobotYawPitchRollAngles().getYaw( AngleUnit.DEGREES );
    telemetry.addLine().addData( "IMU Heading: ", "%.1f", yaw );
  }

  @Override
  public void start()
  {
    robot.brake();
    robot.resetPos( new Vector2d( 0, 0 ) );
    robot.imu().resetYaw();
    robot.playSound( JoeBot.Sound.CALIBRATE_RUN, true );
  }

  @Override
  public void loop()
  {
    robot.updateState();

    if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.X ) )
    {
      targetIndex = targetIndex > 0 ?
                    targetIndex - 1 :
                    numTargets - 1;
    }
    else if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.B ) )
    {
      targetIndex = targetIndex + 1 < numTargets ?
                    targetIndex + 1 :
                    0;
    }
    else if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.A ) )
    {
      Location.NamedLocation target = Location.NamedLocation.values()[ targetIndex ];

      telemetry.addLine( String.format( "Driving to: %s", target ) );
      telemetry.update();

      MecanumDrive drive = robot.mecanumDrive();

      double angle = 0;
      switch( target )
      {
        case _YELLOW_SAMPLE_3:
        case _SAMPLE_BASKETS:
        {
          angle = 135;
          break;
        }
        case _NEAR_YELLOW_SAMPLES_1:
        case _NEAR_YELLOW_SAMPLES_2:
        case _YELLOW_SAMPLE_2:
        case _NEAR_THE_OBSERVATION_ZONE:
        case _IN_THE_OBSERVATION_ZONE:
        case _OBSERVATION_ZONE:
        {
          angle = 180;
          break;
        }

        case _TEAM_SAMPLE_1:
        case _TEAM_SAMPLE_2:
        case _TEAM_SAMPLE_3:
        case _NEAR_TEAM_SAMPLES_1:
        case _NEAR_TEAM_SAMPLES_2:
        {
          angle = 90;
          break;
        }

        case _NEAR_ASCENT_ZONE:
        case _ASCENT_ZONE:
        {
          angle = -90;
          break;
        }


        default:
          break;
      }
      double heading = Math.toRadians( angle );

      robot.automaticallyResetHeadingUsingIMU();

      TrajectoryActionBuilder trajectory = drive.actionBuilder( drive.pose );
      trajectory = trajectory.strafeToLinearHeading( Location.position( target ), heading );
      ActionTools.runBlocking( robot, trajectory.build() );
    }
    else
    {
      Location.NamedLocation target = Location.NamedLocation.values()[ targetIndex ];
      Vector2d position = Location.position( target );
      telemetry.addLine( String.format( "Target: %s", target ) );
      printPose();
      telemetry.update();
    }

    gamepads.storeLastButtons();
  }

  public void stop()
  {
    robot.stopPlayingLoopingSounds();
  }

}
