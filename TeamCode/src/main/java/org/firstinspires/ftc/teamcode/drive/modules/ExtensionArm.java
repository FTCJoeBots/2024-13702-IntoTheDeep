package org.firstinspires.ftc.teamcode.drive.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ExtensionArm extends AbstractModule
{
  //Preset positions we can extend the arm to
  private enum Position
  {
    RETRACTED( 0 ),
    EXTENDED( 100 );

    Position( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  //Relative position for manually extending and contracting the arm
  private static final int MANUAL_POSITION_ADJUST = 10;

  //Various speeds for extending and retracting the arm
  private enum Speed
  {
    EXTEND( 20 ),
    RETRACT( -20 ),
    MANUAL_EXTEND( 40 ),
    MANUAL_RETRACT( -40 ),
    STOP( 0 );

    Speed( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  private DcMotor extensionArmMotor = null;

  public ExtensionArm( HardwareMap hardwareMap )
  {
    initObjects( hardwareMap );
    initState();
  }

  public void fullyExtend()
  {
    extensionArmMotor.setTargetPosition( Position.EXTENDED.value );
    extensionArmMotor.setPower( Speed.EXTEND.value );
  }

  public void fullyRetract()
  {
    extensionArmMotor.setTargetPosition( Position.RETRACTED.value );
    extensionArmMotor.setPower( Speed.RETRACT.value );
  }

  //Extends the arm slightly
  public void manuallyExtend()
  {
    final int currPosition = extensionArmMotor.getCurrentPosition();
    final int nextPosition = currPosition + MANUAL_POSITION_ADJUST;
    extensionArmMotor.setTargetPosition( nextPosition );
    extensionArmMotor.setPower( Speed.MANUAL_EXTEND.value );
  }

  //Retracts the arm slightly
  public void manuallyRetract()
  {
    final int currPosition = extensionArmMotor.getCurrentPosition();
    final int nextPosition = currPosition - MANUAL_POSITION_ADJUST;
    extensionArmMotor.setTargetPosition( nextPosition );
    extensionArmMotor.setPower( Speed.MANUAL_RETRACT.value );
  }

  //Stops the extension arm motor
  public void stop()
  {
    extensionArmMotor.setPower( Speed.STOP.value );
  }

  private void initObjects( HardwareMap hardwareMap )
  {
    extensionArmMotor = hardwareMap.get( DcMotor.class, "extensionArmMotor" );
  }

  private void initState()
  {
    extensionArmMotor.setMode( DcMotor.RunMode.RUN_USING_ENCODER );
    extensionArmMotor.setDirection( DcMotorSimple.Direction.FORWARD );
    extensionArmMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
    fullyRetract();
  }
}
