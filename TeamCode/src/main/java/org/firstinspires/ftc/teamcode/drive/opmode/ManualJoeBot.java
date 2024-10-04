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

  private boolean retracted = true;

  private final Gamepad previousButtons = new Gamepad();

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
    previousButtons.copy( gamepad2 );
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
    //if "A" is pressed, and it wasn't pressed the last time through the loop
    if( gamepad2.a && !previousButtons.a )
    {
      if( retracted )
      { robot.extensionArm.fullyExtend(); }
      else
      { robot.extensionArm.fullyRetract(); }

      retracted = !retracted;
    }

    if( gamepad2.right_stick_button && !previousButtons.right_stick_button )
    {
      Module[] modules = Module.values();

      if( currentModule == modules[ modules.length - 1 ] )
      { currentModule = modules[ 0 ]; }
      else
      { currentModule = Module.values()[ currentModule.ordinal() + 1 ]; }
    }

    if( gamepad2.left_bumper && !gamepad2.right_bumper )
    { robot.extensionArm.manuallyRetract(); }

    else if( gamepad2.right_bumper && !gamepad2.left_bumper )
    { robot.extensionArm.manuallyExtend(); }

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
    previousButtons.copy( gamepad2 );
  }

  //Called when the OpMode terminates
  @Override
  public void stop()
  {
    robot.stop();
  }

}