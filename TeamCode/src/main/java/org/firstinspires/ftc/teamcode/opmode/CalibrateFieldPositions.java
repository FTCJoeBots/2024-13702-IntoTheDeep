package org.firstinspires.ftc.teamcode.opmode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

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
    robot.resetPos( new Vector2d( 0, 0 ) );

    //Allow robot to be pushed around before the start button is pressed
    robot.coast();

    gamepads = new Gamepads( gamepad1, gamepad2 );
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
    Pose2d pose = robot.mecanumDrive().pose;
    telemetry.addLine().addData( "X: ", "%.1f", pose.position.x );
    telemetry.addLine().addData( "Y: ", "%.1f", pose.position.y );
    telemetry.addLine().addData( "Heading: ", "%.1f", Math.toDegrees( pose.heading.toDouble() ) );
  }

  @Override
  public void start()
  {
    robot.brake();
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

      TrajectoryActionBuilder trajectory = drive.actionBuilder( drive.pose );
      trajectory = trajectory.strafeToLinearHeading( Location.position( target ), 0 );

      ActionTools.runBlocking( robot, trajectory.build() );
    }
    else
    {
      Location.NamedLocation target = Location.NamedLocation.values()[ targetIndex ];
      Vector2d position = Location.position( target );
      telemetry.addLine( String.format( "Target: %s ( %f, %f )", target, position.x, position.y ) );
      printPose();
      telemetry.update();
    }

    gamepads.storeLastButtons();
  }
}
