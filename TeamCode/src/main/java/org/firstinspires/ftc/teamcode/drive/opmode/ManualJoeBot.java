package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.drive.JoeBot;

//Tell framework that this is a TeleOp mode
@TeleOp( name = "Manual Joe Bot", group = "Iterative Opmode" )

public class ManualJoeBot extends OpMode
{
  JoeBot robot = null;

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
    if( gamepad2.b && gamepad2.y &&
        !( previousButtons2.b && previousButtons2.y ) )
    {
      robot.extensionArm.fullyExtend();
    }
    //Manually extend - Y
    else if( gamepad2.y && !previousButtons2.y )
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
    if( ( gamepad1.right_stick_button && !previousButtons1.right_stick_button ) ||
        ( gamepad2.right_stick_button && !previousButtons2.right_stick_button ) )
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
  }

  //Called when the OpMode terminates
  @Override
  public void stop()
  {
    robot.stop();
  }

}
