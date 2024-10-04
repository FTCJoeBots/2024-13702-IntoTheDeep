package org.firstinspires.ftc.teamcode.drive.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Lift extends AbstractModule
{
  public static final int LIFTLOWPOINT = 10;
  public static final int LIFTHIGHPOINT = 200;
  public static final double LIFTSPEED = 0.2;
  public static final int LIFTMANUALINC = 30;

  DcMotor liftMotor = null;

  public Lift( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( telemetry );
    initObjects( hardwareMap );
    initState();
  } 

  private void initObjects( HardwareMap hardwareMap )
  { liftMotor = hardwareMap.get( DcMotor.class, "liftMotor" ); }

  private void initState()
  {
    liftMotor.setMode( DcMotor.RunMode.RUN_USING_ENCODER );
    liftMotor.setDirection( DcMotorSimple.Direction.FORWARD );
    liftMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    liftMotor.setPower( LIFTSPEED );
    liftautodown();
  }

  //manual up
  public void liftmanualup()
  {
    int liftCurPosition = liftMotor.getCurrentPosition();
    int liftNewPosition = liftCurPosition + LIFTMANUALINC;
    if( liftNewPosition < LIFTHIGHPOINT )
    {
      liftMotor.setTargetPosition( liftNewPosition );
      liftMotor.setPower( LIFTSPEED );
    }
  }

  //manual down
  public void liftmanualdown()
  {
    int liftCurPosition = liftMotor.getCurrentPosition();
    int liftNewPosition = liftCurPosition - LIFTMANUALINC;

    if( liftNewPosition > LIFTLOWPOINT )
    {
      liftMotor.setTargetPosition( liftNewPosition );
      liftMotor.setPower( LIFTSPEED );
    }
  }

  //auto down
  public void liftautodown()
  {
    liftMotor.setTargetPosition( LIFTLOWPOINT );
    liftMotor.setPower( LIFTSPEED );
  }

  //auto up
  public void liftautoup()
  {
    liftMotor.setTargetPosition( LIFTHIGHPOINT );
    liftMotor.setPower( LIFTSPEED );
  }

  //Stops the extension arm motor
  public void stop()
  {
    liftMotor.setPower( 0 );
  }

  //Prints out the extension arm motor position
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Lift - %s", liftMotor.getCurrentPosition() ) );
    telemetry.update();
  }

}
