package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractModule
{
  protected HardwareMap hardwareMap = null;
  protected Telemetry telemetry = null;
  private Map<String, DcMotor> motors = null;
  private Map<String, CRServo> servos = null;

  AbstractModule( HardwareMap hardwareMap, Telemetry telemetry )
  {
    this.hardwareMap = hardwareMap;
    this.telemetry = telemetry;
    motors = new HashMap<>();
    servos = new HashMap<>();
  }

  protected DcMotor createMotor( String name )
  {
    DcMotor motor = hardwareMap.get( DcMotor.class, name );
    motors.put( name, motor );
    return motor;
  }

  protected CRServo createServo( String name )
  {
    CRServo servo = hardwareMap.get( CRServo.class, name );
    servos.put( name, servo );
    return servo;
  }

  protected void initMotor( DcMotor motor, DcMotor.RunMode runMode, DcMotorSimple.Direction direction )
  {
    motor.setMode( DcMotor.RunMode.STOP_AND_RESET_ENCODER );
    motor.setMode( runMode );
    motor.setDirection( direction );
    motor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    motor.setPower( 0 );
  }

  //You must override this function in derived classes to implement a
  //shut down procedure, e.g. stopping all motors and servos
  public void stop()
  {
    for( DcMotor motor : motors.values() )
    {
      motor.setPower( 0 );
    }

    for( CRServo servo : servos.values() )
    {
      servo.setPower( 0 );
    }
  }

  //You must override this function to derived classes to print out motor position
  //and other debugging information.
  public abstract void printTelemetry();
}
