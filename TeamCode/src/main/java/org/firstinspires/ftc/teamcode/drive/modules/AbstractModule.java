package org.firstinspires.ftc.teamcode.drive.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class AbstractModule
{
  protected Telemetry telemetry;

  AbstractModule( Telemetry telemetry )
  {
    this.telemetry = telemetry;
  }

  protected void initMotor( DcMotor motor, DcMotorSimple.Direction direction )
  {
    motor.setMode( DcMotor.RunMode.RUN_USING_ENCODER );
    motor.setDirection( direction );
    motor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    motor.setPower( 0 );
  }

  //You must override this function in derived classes to implement a
  //shut down procedure, e.g. stopping all motors and servos
  public abstract void stop();

  //You must override this function to derived classes to print out motor position
  //and other debugging information.
  public abstract void printTelemetry();
}
