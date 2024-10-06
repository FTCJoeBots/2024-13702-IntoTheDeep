package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.drive.Button;
import org.firstinspires.ftc.teamcode.drive.Gamepads;
import org.firstinspires.ftc.teamcode.drive.JoeBot;
import org.firstinspires.ftc.teamcode.drive.Participant;

import java.util.EnumSet;
import java.util.Set;

//Tell framework that this is a TeleOp mode
@TeleOp( name = "Manual Joe Bot", group = "Iterative Opmode" )

public class ManualJoeBot extends OpMode
{
  JoeBot robot = null;
  Gamepads gamepads = null;

  private final Gamepad previousButtons1 = new Gamepad();
  private final Gamepad previousButtons2 = new Gamepad();

  private enum Module
  {
    NONE, EXTENSION_ARM, LIFT, INTAKE, DRIVE_MOTORS, DRIVE_ODOMETERS
  }

  private Module currentModule = Module.values()[ 0 ];

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    robot = new JoeBot( hardwareMap, telemetry );
    gamepads = new Gamepads( gamepad1, gamepad2 );
    previousButtons1.copy( gamepad1 );
    previousButtons2.copy( gamepad2 );
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

  //Main OpMode loop
  @Override
  public void loop()
  {
    //Fully extend - B + Y
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.Y ) ) )
    {
      robot.extensionArm.fullyExtend();
    }
    //Manually extend - Y
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.Y ) )
    {
      robot.extensionArm.manuallyExtend();
    }

    //Full retract - B + A
    if( gamepad2.b && gamepad2.a &&
      !( previousButtons2.b && previousButtons2.a ) )
    {
      robot.extensionArm.fullyRetract();
    }
    //Manually retract - A
    else if( gamepad2.a && !previousButtons2.a )
    {
      robot.extensionArm.manuallyRetract();
    }

    //Raise lift slow (high torque) - dpad_up + b
    if( gamepad2.b && gamepad2.dpad_up &&
      !( previousButtons2.b && previousButtons2.dpad_up ) )
    {
      //TODO - use slow
      robot.lift.liftmanualup();
    }
    //Raise lift fast - dpad_up
    else if( gamepad2.dpad_up && !previousButtons2.dpad_up)
    {
      //TODO - use fast
      robot.lift.liftmanualup();
    }
    //Lower lift slow (high torque) - dpad_down + b
    else if( gamepad2.b && gamepad2.dpad_down &&
      !( previousButtons2.b && previousButtons2.dpad_down ) )
    {
      //TODO - use slow
      robot.lift.liftmanualdown();
    }
    //Lower lift fast- dpad_down
    else if( gamepad2.dpad_down && !previousButtons2.dpad_down )
    {
      //TODO - use fast
      robot.lift.liftmanualdown();
    }

    //Cycle through telemetry
    if( gamepads.buttonPressed( Participant.ANY, Button.RIGHT_STICK ) )
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
        robot.extensionArm.printTelemetry();
        break;
      case LIFT:
        robot.lift.printTelemetry();
        break;
      case INTAKE:
        //        robot.intake.printTelemetry();
        break;
    }

    //Set this every time through the loop
    previousButtons1.copy( gamepad1 );
    previousButtons2.copy( gamepad2 );
    gamepads.storeLastButtons();
  }

  //Called when the OpMode terminates
  @Override
  public void stop()
  {
    robot.stop();
  }

}
