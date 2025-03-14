package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

public abstract class AbstractModule
{
  protected HardwareMap hardwareMap = null;
  protected Telemetry telemetry = null;
  private ArrayList<DcMotorSimple> motors = null;

  public static boolean encodersReset = false;

  protected boolean autoDetectStall = false;
  protected ElapsedTime stallTimer = null;

  public AbstractModule( HardwareMap hardwareMap, Telemetry telemetry )
  {
    this.hardwareMap = hardwareMap;
    this.telemetry = telemetry;
    motors = new ArrayList<>();
    stallTimer = new ElapsedTime();
  }

  protected DcMotorEx createMotor( String name )
  {
    DcMotorEx motor = hardwareMap.get( DcMotorEx.class, name );
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

    // Reset the motor encoder so that it reads zero ticks
    if( !encodersReset )
    {
      motor.setMode( DcMotor.RunMode.STOP_AND_RESET_ENCODER );
      motor.setTargetPosition( 0 );
    }

    motor.setDirection( direction );
    motor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    motor.setPower( 0 );

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
