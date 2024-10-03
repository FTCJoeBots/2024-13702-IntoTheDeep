package org.firstinspires.ftc.teamcode.drive.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.drive.JoeBot;

//Tell framework that this is a TeleOp mode
@TeleOp(name="Manual Joe Bot", group="Iterative Opmode")

public class ManualJoeBot extends OpMode {
  HardwareMap hardwareMap = null;
  JoeBot robot = null;

  private boolean retracted = true;

  private final Gamepad previousButtons = new Gamepad();


  //We run this when the user hits "INIT" on the app
  @Override
  public void init () {
    robot = new JoeBot( hardwareMap );
    previousButtons.copy( gamepad2 );
    telemetry.addLine("Initialization complete");
    telemetry.update();
  }


  //This loop runs before the start button is pressed
  @Override
  public void init_loop() {}

  //Called when the user hits the start button
  @Override
  public void start() {}

  //Main OpMode loop
  @Override
  public void loop() {
    //if "A" is pressed, and it wasn't pressed the last time through the loop
    if( gamepad2.a &&
        !previousButtons.a )
    {
      if( retracted )
      { robot.extensionArm.fullyExtend(); }
      else
      { robot.extensionArm.fullyRetract(); }

      retracted = !retracted;
    }

    //Set this every time through the loop
    previousButtons.copy( gamepad2 );
  }

  //Called when the OpMode terminates
  @Override
  public void stop() {
    robot.stop();
  }

}