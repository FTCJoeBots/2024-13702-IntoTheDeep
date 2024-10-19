package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ExtensionArm extends AbstractModule
{
  //Preset positions we can extend the arm to
  private enum Position
  {
    RETRACTED( 0 ), EXTENDED( 100 );

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
    EXTEND( 0.5 ), RETRACT( 0.5 ), MANUAL_EXTEND( 0.1 ), MANUAL_RETRACT( 0.1 ), STOP( 0 );

    Speed( double value )
    {
      this.value = value;
    }

    public final double value;
  }

  private DcMotor extensionArmMotor = null;

  public ExtensionArm( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  public void fullyExtend()
  {
    setTargetPositionAndPower( Position.EXTENDED.value, Speed.EXTEND.value );
  }

  public void fullyRetract()
  {
    setTargetPositionAndPower( Position.RETRACTED.value, Speed.RETRACT.value );
  }

  //Extends the arm slightly
  public void manuallyExtend()
  {
    final int currPosition = extensionArmMotor.getCurrentPosition();
    int nextPosition = currPosition + MANUAL_POSITION_ADJUST;

    //Prevent the extension arm from extending too far
    if( nextPosition > Position.EXTENDED.value )
    { nextPosition = Position.EXTENDED.value; }

    setTargetPositionAndPower( nextPosition, Speed.MANUAL_EXTEND.value );
  }

  private void setTargetPositionAndPower( int position, double power )
  {
//    extensionArmMotor.setTargetPosition( position );
//    extensionArmMotor.setPower( power );
  }

  //Retracts the arm slightly
  public void manuallyRetract()
  {
    final int currPosition = extensionArmMotor.getCurrentPosition();
    int nextPosition = currPosition - MANUAL_POSITION_ADJUST;

    //Prevent the extension arm from retracting too far
    if( nextPosition < Position.RETRACTED.value )
    { nextPosition = Position.RETRACTED.value; }

    setTargetPositionAndPower( nextPosition, Speed.MANUAL_RETRACT.value );
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Extension Arm - %s", getMotorPosition() ) );
  }

  public int getMotorPosition()
  {
    return extensionArmMotor.getCurrentPosition();
  }

  private void initObjects()
  {
    extensionArmMotor = createMotor( "extensionArmMotor" );
  }

  private void initState()
  {
    initMotor( extensionArmMotor, DcMotor.RunMode.RUN_USING_ENCODER, DcMotorSimple.Direction.FORWARD );
    extensionArmMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
    fullyRetract();
  }
}