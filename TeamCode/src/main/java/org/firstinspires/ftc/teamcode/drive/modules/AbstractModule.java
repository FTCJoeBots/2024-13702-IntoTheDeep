package org.firstinspires.ftc.teamcode.drive.modules;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class AbstractModule
{
  protected Telemetry telemetry;

  AbstractModule( Telemetry telemetry )
  {
    this.telemetry = telemetry;
  }

  //You must override this function in derived classes to implement a
  //shut down procedure, e.g. stopping all motors and servos
  public abstract void stop();

  //You must override this function to derived classes to print out motor position
  //and other debugging information.
  public abstract void printTelemetry();
}
