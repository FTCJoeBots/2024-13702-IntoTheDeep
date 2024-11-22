package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ClimbArm extends AbstractModule
{
  private DcMotorEx climbMotor = null;

  public ClimbArm( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  public void setPower( double power )
  {
    climbMotor.setPower( power );
  }

  public void stop()
  {
    climbMotor.setPower( 0 );
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Climb Arm Position: %s", climbMotor.getCurrentPosition() ) );
    telemetry.addLine( String.format( "Climb Arm Power: %s", climbMotor.getPower() ) );
  }

  private void initObjects()
  {
    climbMotor = createMotor( "climbMotor" );
  }

  private void initState()
  {
    initMotor( climbMotor, DcMotor.RunMode.RUN_WITHOUT_ENCODER, DcMotorSimple.Direction.FORWARD );
    climbMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
  }
}