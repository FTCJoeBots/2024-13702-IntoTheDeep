package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.drive.Button;
import org.firstinspires.ftc.teamcode.drive.Gamepads;
import org.firstinspires.ftc.teamcode.drive.JoeBot;
import org.firstinspires.ftc.teamcode.drive.Participant;
import org.firstinspires.ftc.teamcode.drive.modules.Lift;

import java.util.EnumSet;

//Tell framework that this is a TeleOp mode
@TeleOp( name = "Manual Joe Bot", group = "Iterative Opmode" )
public class ManualJoeBot extends OpMode
{
  JoeBot robot = null;
  Gamepads gamepads = null;

  private enum Module
  {
    NONE, DRIVE, INTAKE, LIFT, EXTENSION_ARM
  }

  private Module currentModule = Module.values()[ 0 ];

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    robot = new JoeBot( hardwareMap, telemetry );
    gamepads = new Gamepads( gamepad1, gamepad2 );
    telemetry.addLine( "ManualJoeBot OpMode Initialized" );
    telemetry.update();
  }

  //This loop runs before the start button is pressed
  @Override
  public void init_loop()
  {
  }

  //Called when the user hits the start button
  @Override
  public void start()
  {
  }

  private void addMessage( String message)
  {
    telemetry.addLine( message);
    telemetry.update();
  }

  //Main OpMode loop
  @Override
  public void loop()
  {
    //==================
    //Extension Arm
    //==================
    //Fully extend - B + Y
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.Y ) ) )
    {
      robot.extensionArm().fullyExtend();
      addMessage( "Fully extend arm" );
    }
    //Manually extend - Y
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.Y ) )
    {
      robot.extensionArm().manuallyExtend();
      addMessage( "Manually extend arm" );
    }

    //Full retract - B + A
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.A ) ) )
    {
      robot.extensionArm().fullyRetract();
      addMessage( "Fully retract arm" );
    }
    //Manually retract - A
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.A ) )
    {
      robot.extensionArm().manuallyRetract();
      addMessage( "Manually retract arm" );
    }

    //==================
    //Lift
    //==================
    //High basket - x + dpad_up
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_UP, Button.X ) ) )
    {
      robot.lift().travelTo( Lift.Position.HIGH_BASKET );
    }
    //Low basket - x + dpad_down
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_DOWN, Button.X ) ) )
    {
      robot.lift().travelTo( Lift.Position.LOW_BASKET );
    }
    //Move lift to bottom - x + a
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.A, Button.X ) ) )
    {
      robot.lift().travelTo( Lift.Position.FLOOR );
    }
    //Climbing - x + dpad_right
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_RIGHT, Button.X ) ) )
    {
      robot.lift().climb();
    }
    //Raise lift slow (high torque) - dpad_up + b
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_UP, Button.B ) ) )
    {
      robot.lift().slowLift();
      addMessage( "Raise lift slow" );
    }
    //Raise lift fast - dpad_up
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.DPAD_UP ) )
    {
      robot.lift().fastLift();
      addMessage( "Raise lift fast" );
    }
    //Lower lift slow (high torque) - dpad_down + b
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_DOWN, Button.B ) ) )
    {
      robot.lift().slowDrop();
      addMessage( "Lower lift slow" );
    }
    //Lower lift fast- dpad_down
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.DPAD_DOWN ) )
    {
      robot.lift().fastDrop();
      addMessage( "Lower lift fast" );
    }

    //==================
    //Intake
    //==================
    if( robot.intake().actUponColor() )
    {
      gamepad1.rumbleBlips( 3 );
      gamepad2.rumbleBlips( 3 );
    }

    //Pull in sample
    if( gamepad2.left_stick_y < 0 )
    {
      robot.intake().pullInSample();
    }
    //Spit out sample
    else if ( gamepad2.left_stick_y > 0 )
    {
      robot.intake().spitOutSample();
    }
    //Stop
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.LEFT_STICK ) )
    {
      robot.intake().stop();
    }

    //==================
    //Drive
    //==================
    final double forward = gamepad1.left_stick_y;
    final double strafe = -( gamepad1.left_stick_x + gamepad1.right_stick_x );
    final double rotate = gamepad1.left_trigger - gamepad1.right_trigger;
    robot.drive().move( forward, strafe, rotate );

    //Cycle through telemetry
    if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.RIGHT_STICK ) )
    {
      Module[] modules = Module.values();

      if( currentModule == modules[ modules.length - 1 ] )
      { currentModule = modules[ 0 ]; }
      else
      { currentModule = Module.values()[ currentModule.ordinal() + 1 ]; }
    }

    switch( currentModule )
    {
      case EXTENSION_ARM:
        robot.extensionArm().printTelemetry();
        break;
      case LIFT:
        robot.lift().printTelemetry();
        break;
      case INTAKE:
        robot.intake().printTelemetry();
        break;
      case DRIVE:
        robot.drive().printTelemetry();
    }

    gamepads.storeLastButtons();
  }

  //Called when the OpMode terminates
  @Override
  public void stop()
  {
    robot.stop();
  }

}
