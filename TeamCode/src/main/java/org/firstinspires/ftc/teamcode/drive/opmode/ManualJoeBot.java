package org.firstinspires.ftc.teamcode.drive.opmode;

//package org.firstinspires.ftc.teamcode;
//
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.drive.JoeBot;

//This is an annotation.  It puts metadata in an object.
//This is telling the framework that this is a TeleOp mode, and giving it a name and group
@TeleOp(name="Manual Joe Bot", group="Iterative Opmode")

public class ManualJoeBot extends OpMode {

  //NULL?
  HardwareMap hardwareMap = null;
  JoeBot robot = null;

  private boolean retracted = true;
  private boolean prevA = false;

  //We run this when the user hits "INIT" on the app
  @Override
  public void init () {
    robot = new JoeBot( hardwareMap )
    telemetry.addLine("Initialization complete");
    telemetry.update();
  }


  //This runs in a loop from the time the user hits init until they press start
  @Override
  public void init_loop(){

  }

  //This gets called one time when the user hits the start button
  @Override
  public void start() {

  }


  //This gets run repeatedly...many times per second while we're running our robot
  @Override
  public void loop() {
    //if "A" is pressed, and it wasn't pressed the last time through the loop
    if (gamepad2.a && !prevA)
    {
      if( retracted )
      {
        robot.extensionArm.fullyExtend();
        retracted = false;
      }
      else
      { robot.extensionArm.fullyRetract();
        retracted = true;
      }
    }

    //Set this every time through the loop
    prevA=gamepad2.a;
  }

  //This runs one time on stop
  @Override
  public void stop() {
  }

}