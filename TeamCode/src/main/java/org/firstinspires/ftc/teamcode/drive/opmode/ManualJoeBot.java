package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.drive.Button;
import org.firstinspires.ftc.teamcode.drive.Gamepads;
import org.firstinspires.ftc.teamcode.drive.JoeBot;
import org.firstinspires.ftc.teamcode.drive.Participant;

import java.util.EnumSet;

//Tell framework that this is a TeleOp mode
@TeleOp( name = "Manual Joe Bot", group = "Iterative Opmode" )

public class ManualJoeBot extends OpMode
{
  JoeBot robot = null;
  Gamepads gamepads = null;

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

  private void addMessege( String message)
  {
    telemetry.addLine( message);
    telemetry.update();
  }

  //Main OpMode loop
  @Override
  public void loop()
  {
    //Fully extend - B + Y
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.Y ) ) )
    {
      robot.extensionArm.fullyExtend();
      addMessege( "Fully extend arm" );
    }
    //Manually extend - Y
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.Y ) )
    {
      robot.extensionArm.manuallyExtend();
      addMessege( "Manually extend arm" );
    }

    //Full retract - B + A
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.A ) ) )
    {
      robot.extensionArm.fullyRetract();
      addMessege( "Fully retract arm" );
    }
    //Manually retract - A
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.A ) )
    {
      robot.extensionArm.manuallyRetract();
      addMessege( "Manually retract arm" );
    }

    //Raise lift slow (high torque) - dpad_up + b
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_UP, Button.B ) ) )
    {
      //TODO - use slow
      robot.lift.liftmanualup();
      addMessege( "Raise lift slow" );
    }
    //Raise lift fast - dpad_up
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.DPAD_UP ) )
    {
      //TODO - use fast
      robot.lift.liftmanualup();
      addMessege( "Raise lift fast" );
    }
    //Lower lift slow (high torque) - dpad_down + b
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_DOWN, Button.B ) ) )
    {
      //TODO - use slow
      robot.lift.liftmanualdown();
      addMessege( "Lower lift slow" );
    }
    //Lower lift fast- dpad_down
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.DPAD_DOWN ) )
    {
      //TODO - use fast
      robot.lift.liftmanualdown();
      addMessege( "Lower lift fast" );
    }

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
        robot.extensionArm.printTelemetry();
        break;
      case LIFT:
        robot.lift.printTelemetry();
        break;
      case INTAKE:
        //        robot.intake.printTelemetry();
        break;
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
