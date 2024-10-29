package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ExtensionArm extends AbstractModule
{
  private DcMotor extensionArmMotor = null;

  //Relative position for manually extending and contracting the arm
  private static final int MANUAL_POSITION_ADJUST = 100;

  //Preset positions we can extend the arm to
  private enum Position
  {
    FULLY_RETRACTED( 0 ),
    RETRACTED_WITH_SAMPLE( 47 ),
    FULLY_EXTENDED( 2876 ),
    EXTEND_TO_HANG( 834 );

    Position( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  //Various speeds for extending and retracting the arm
  private enum Speed
  {
    EXTEND( 0.5 ),
    RETRACT( 0.5 ),
    MANUAL_EXTEND( 0.1 ),
    MANUAL_RETRACT( 0.1 );

    Speed( double value )
    {
      this.value = value;
    }

    public final double value;
  }

  public ExtensionArm( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  public void fullyExtend()
  {
    setTargetPositionAndPower( Position.FULLY_EXTENDED.value, Speed.EXTEND.value );
  }

  public void fullyRetract()
  {
    setTargetPositionAndPower( Position.FULLY_RETRACTED.value, Speed.RETRACT.value );
  }

  //Extends the arm slightly
  public void manuallyExtend()
  {
    if( extensionArmMotor == null )
    { return; }

    final int currPosition = extensionArmMotor.getCurrentPosition();
    int nextPosition = currPosition + MANUAL_POSITION_ADJUST;

    //Prevent the extension arm from extending too far
    if( nextPosition > Position.FULLY_EXTENDED.value )
    { nextPosition = Position.FULLY_EXTENDED.value; }

    setTargetPositionAndPower( nextPosition, Speed.MANUAL_EXTEND.value );
  }

  private void setTargetPositionAndPower( int position, double power )
  {
    if( extensionArmMotor == null )
    { return; }

    extensionArmMotor.setTargetPosition( position );
    extensionArmMotor.setPower( power );
  }

  //Retracts the arm slightly
  public void manuallyRetract()
  {
    if( extensionArmMotor == null )
    { return; }

    final int currPosition = extensionArmMotor.getCurrentPosition();
    int nextPosition = currPosition - MANUAL_POSITION_ADJUST;

    //Prevent the extension arm from retracting too far
    if( nextPosition < Position.FULLY_RETRACTED.value )
    { nextPosition = Position.FULLY_RETRACTED.value; }

    setTargetPositionAndPower( nextPosition, Speed.MANUAL_RETRACT.value );
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    if( extensionArmMotor == null )
    { return; }

    telemetry.addLine( String.format( "Extension Arm: %s", getMotorPosition() ) );
  }

  public int getMotorPosition()
  {
    if( extensionArmMotor == null )
    { return 0; }

    return extensionArmMotor.getCurrentPosition();
  }

  private void initObjects()
  {
    extensionArmMotor = createMotor( "extensionArmMotor" );
  }

  private void initState()
  {
    if( extensionArmMotor == null )
    { return; }

    initMotor( extensionArmMotor, DcMotor.RunMode.RUN_USING_ENCODER, DcMotorSimple.Direction.REVERSE );
    extensionArmMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
  }
}