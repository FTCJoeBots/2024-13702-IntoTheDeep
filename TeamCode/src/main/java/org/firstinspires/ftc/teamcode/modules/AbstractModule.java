package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractModule
{
  protected HardwareMap hardwareMap = null;
  protected Telemetry telemetry = null;
  private List<DcMotorSimple> motors = null;

  public AbstractModule( HardwareMap hardwareMap, Telemetry telemetry )
  {
    this.hardwareMap = hardwareMap;
    this.telemetry = telemetry;
    motors = new ArrayList<>();
  }

  protected DcMotor createMotor( String name )
  {
    DcMotor motor = hardwareMap.dcMotor.get( name );
    motors.add( motor );
    return motor;
  }

  protected CRServo createCRServo( String name )
  {
    CRServo servo = hardwareMap.crservo.get( name );
    motors.add( servo );
    return servo;
  }

  protected void initMotor( DcMotor motor, DcMotor.RunMode runMode, DcMotorSimple.Direction direction )
  {
    if( motor == null )
    { return; }

    motor.setPower( 0 );
    motor.setDirection( direction );
    motor.setTargetPosition( 0 );

    motor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );

    // Reset the motor encoder so that it reads zero ticks
    motor.setMode( DcMotor.RunMode.STOP_AND_RESET_ENCODER );

    // Turn the motor back on
    motor.setMode( runMode );
  }

  public void setZeroPowerBehavior( DcMotor.ZeroPowerBehavior val )
  {
    for( DcMotorSimple motor : motors )
    {
      if( motor instanceof DcMotor )
      {
        ( ( DcMotor ) motor ).setZeroPowerBehavior( val );
      }
    }
  }

  //You must override this function in derived classes to implement a
  //shut down procedure, e.g. stopping all motors and servos
  public void stop()
  {
    for( DcMotorSimple motor : motors )
    {
      motor.setPower( 0 );
    }
  }

  //You must override this function to derived classes to print out motor position
  //and other debugging information.
  public abstract void printTelemetry();
}
