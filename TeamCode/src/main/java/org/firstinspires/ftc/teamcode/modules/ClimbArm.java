package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ClimbArm extends AbstractModule
{
  private DcMotorEx climbMotor = null;

  //Preset positions we can extend the arm to
  public enum Position
  {
    HOOKS_RETRACTED( 0 ),
    HALF_CLIMB( 6900 ),
    HOOKS_RAISED( 10000 );

    Position( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  private enum Action
  {
    MOVING,
    STOPPED
  }

  private Action currentAction = Action.STOPPED;

  public ClimbArm( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  public void updateState()
  {
    if( currentAction == Action.STOPPED )
    { return; }

    final int current = climbMotor.getCurrentPosition();
    final int target = climbMotor.getTargetPosition();
    final int diff = Math.abs( current - target );

    if( diff <= 1 )
    {
      telemetry.log().add( String.format( "Climb Arm stopping, current %s target %s", current, target ) );
      stop();
    }
  }

  public boolean armsRaised()
  {
    return Math.abs( climbMotor.getCurrentPosition() - Position.HOOKS_RAISED.value ) <= 2000;
  }

  public void raiseHooks()
  {
    climbMotor.setTargetPosition( Position.HOOKS_RAISED.value );
    climbMotor.setPower( 1 );
    currentAction = Action.MOVING;
  }

  public void retractHooks()
  {
    climbMotor.setTargetPosition( Position.HOOKS_RETRACTED.value );
    climbMotor.setPower( 1 );
    currentAction = Action.MOVING;
  }

  public void climb()
  {
    climbMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    climbMotor.setTargetPosition( Position.HALF_CLIMB.value );
    climbMotor.setPower( 1 );
    currentAction = Action.MOVING;
  }

  public void moveHooks( boolean raise )
  {
    int delta = 100;
    int oldPosition = climbMotor.getCurrentPosition();
    int newPosition = oldPosition + ( raise ? 1 : -1 ) * delta;

    climbMotor.setTargetPosition( newPosition );
    climbMotor.setPower( 1 );
    currentAction = Action.MOVING;
  }

  public void stop()
  {
    if( currentAction == Action.MOVING )
    {
      super.stop();
    }

    telemetry.log().add( "Climb Arm stopped" );
    currentAction = Action.STOPPED;
  }

  public boolean isMoving()
  {
    return currentAction != Action.STOPPED;
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
    initMotor( climbMotor, DcMotor.RunMode.RUN_TO_POSITION, DcMotorSimple.Direction.REVERSE );
    climbMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
  }
}