package org.firstinspires.ftc.teamcode.opmode;

import com.acmerobotics.roadrunner.TrajectoryActionBuilder;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Gamepads;
import org.firstinspires.ftc.teamcode.JoeBot;
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
  private Location target = Location.values()[ 0 ];

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    robot = new JoeBot( false, hardwareMap, telemetry );
    robot.resetPos();

    //Allow robot to be pushed around before the start button is pressed
    robot.drive().coast();

    gamepads = new Gamepads( gamepad1, gamepad2 );
  }

  @Override
  public void init_loop()
  {
    robot.drive().printTelemetry();
  }

  @Override
  public void start()
  {
    robot = new JoeBot( true, hardwareMap, telemetry );
  }

  @Override
  public void loop()
  {
    if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_LEFT ) ||
        gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_UP ) )
    {
      final Location[] locations = Location.values();

      target = locations[ target.ordinal() > 0 ?
                          target.ordinal() - 1 :
                          locations.length - 1 ];
    }
    else if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_RIGHT ) ||
             gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_DOWN ) )
    {
      final Location[] locations = Location.values();

      target = locations[ target.ordinal() < locations.length - 1 ?
                          target.ordinal() + 1 :
                          0 ];
    }
    else if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.A ) ||
             gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.B ) ||
             gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.X ) ||
             gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.Y ) )
    {
      telemetry.addLine( String.format( "Driving to: %s", target ) );
      telemetry.update();

      MecanumDrive drive = robot.mecanumDrive();

      TrajectoryActionBuilder trajectory = drive.actionBuilder( drive.pose );
      trajectory = trajectory.splineTo( target.value, 0 );

      Actions.runBlocking( trajectory.build() );
    }
    else
    {
      telemetry.addLine( String.format( "Next Target: %s", target ) );
      telemetry.addLine( String.format( "Current Pose: %s", robot.mecanumDrive().pose ) );
      telemetry.update();
    }
  }
}
