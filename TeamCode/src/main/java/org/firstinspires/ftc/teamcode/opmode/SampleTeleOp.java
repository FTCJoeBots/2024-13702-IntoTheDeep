package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.Lift;

import java.util.EnumSet;
import java.util.List;

//Tell framework that this is a TeleOp mode
@TeleOp( name = "Sample Tele Op", group = "Iterative Opmode" )
public class SampleTeleOp extends OpMode
{
  CRServo leftServo = null;
  CRServo rightServo = null;

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    leftServo = hardwareMap.get( CRServo.class, "leftIntakeServo" );
    leftServo.setDirection( DcMotorSimple.Direction.FORWARD );
    leftServo.setPower( 1.0 );
    rightServo = hardwareMap.get( CRServo.class, "rightIntakeServo" );
    rightServo.setDirection( DcMotorSimple.Direction.REVERSE );
    rightServo.setPower( 1.0 );
  }

  @Override
  public void init_loop()
  {
  }

  @Override
  public void start()
  {
  }

  @Override
  public void loop()
  {
    telemetry.addData( "left servo power", "%f", leftServo.getPower() );
    telemetry.addData( "right servo power", "%f", rightServo.getPower() );
    telemetry.update();
  }

  //Called when the OpMode terminates
  @Override
  public void stop()
  {
  }

}
