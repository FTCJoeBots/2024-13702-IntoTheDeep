package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Lift extends AbstractModule
{
  private static final double FAST_SPEED_UP = 1.0;
  private static final double FAST_SPEED_DOWN = 1.0;

  private static final double SLOW_SPEED_UP = 0.4;
  private static final double SLOW_SPEED_DOWN = 0.15;

  private static final double COAST = 0;

  private static final int ADJUST_UP   = 200;
  private static final int ADJUST_DOWN = 200;

  //coast down until we are close to our target
  //IMPORTANT: this value should be less than ADJUST_DOWN or else we never will coast
  //when manually moving the lift down
  private static final int FAR_AWAY = 100;

  //only coast down above a minimum height since
  //gravity does not seem to cause the lift as we get close
  //to the bottom and it is necessary to use the motors to pull the lift
  //the rest of the way down.
  private static final int MINIMUM_COAST_HEIGHT = 1500;

  //Preset positions we can extend the arm to
  public enum Position
  {
    FLOOR( 0 ),
    MAX_LIFT( 10000 ),

    SAMPLE_FLOOR( 172 ),
    SPECIMEN_FLOOR( 347 ),

    //putting samples in baskets
    HIGH_BASKET( 10000 ),
    LOW_BASKET( 6088 ),

    //hanging specimens
    ABOVE_HIGH_SPECIMEN_BAR( 6324 ),
    ABOVE_LOW_SPECIMEN_BAR( 3548 ),
    SPECIMEN_CLIPPED_ONTO_HIGH_BAR( 5691 ),
    SPECIMEN_CLIPPED_ONTO_LOW_BAR( 2996 ),

    //climbing
    ABOVE_LOW_HANG_BAR( 4663 ),
    HANG_FROM_LOW_HANG_BAR( 3889 ),

    //height above which we should limit extending the extension arm to avoid tipping over
    HIGH_UP( 2000 );

    Position( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  DcMotor leftMotor = null;
  DcMotor rightMotor = null;

  private enum Action
  {
    MOVING,
    STOPPED
  }

  private Action currentAction = Action.STOPPED;
  private double targetPower = 0;

  public Lift( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  private void initObjects()
  {
    leftMotor = createMotor( "leftLiftMotor" );
    rightMotor = createMotor( "rightLiftMotor" );
  }

  private void initState()
  {
    final DcMotor.RunMode runMode = DcMotor.RunMode.RUN_TO_POSITION;
    initMotor( leftMotor, runMode, DcMotorSimple.Direction.FORWARD );
    initMotor( rightMotor, runMode, DcMotorSimple.Direction.REVERSE );
  }

  public boolean isHigh()
  {
    return liftPosition() > Position.HIGH_UP.value;
  }

  public boolean isMoving()
  {
    telemetry.log().add( String.format( "currentAction: %s", currentAction ) );
    return currentAction == Action.MOVING;
  }

  public int liftPosition()
  {
    int leftPosition = leftMotor.getCurrentPosition();
    return leftPosition;

//    int rightPosition = rightMotor.getCurrentPosition();
//
//    return Math.round( ( leftPosition + rightPosition ) / 2.0f );
  }

  public boolean fastLift()
  {
    return turnMotors( DcMotorSimple.Direction.FORWARD, FAST_SPEED_UP );
  }

  public boolean slowLift()
  {
    return turnMotors( DcMotorSimple.Direction.FORWARD, SLOW_SPEED_UP );
  }

  public boolean fastDrop()
  {
    return turnMotors( DcMotorSimple.Direction.REVERSE, FAST_SPEED_DOWN );
  }

  public boolean slowDrop()
  {
    return turnMotors( DcMotorSimple.Direction.REVERSE, SLOW_SPEED_DOWN );
  }

  private double adjustPower( int targetPosition, double power )
  {
    targetPower = power;

    //it is smoother if we coast downwards until we get
    //close to our target position
    int liftCurPosition = liftPosition();

    if( targetPosition < liftCurPosition &&
      liftCurPosition > MINIMUM_COAST_HEIGHT &&
      Math.abs( targetPosition - liftCurPosition ) > FAR_AWAY )
    {
      leftMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
      rightMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
      return COAST;
    }
    else
    {
      leftMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      rightMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      return power;
    }
  }

  public void travelTo( Position position )
  {
    double power = liftPosition() < position.value ?
                   FAST_SPEED_UP :
                   FAST_SPEED_DOWN;

    //always use power to climb
    if( position != Position.HANG_FROM_LOW_HANG_BAR )
    { power = adjustPower( position.value, power ); }

    setMotorPosition( leftMotor, position.value, power );
    setMotorPosition( rightMotor, position.value, power );
    currentAction = Action.MOVING;
  }

  public void stop()
  {
    //set motors to hold the current position with full power to avoid slipping
    leftMotor.setTargetPosition( leftMotor.getCurrentPosition() );
    leftMotor.setPower( 1 );
    rightMotor.setTargetPosition( rightMotor.getCurrentPosition() );
    rightMotor.setPower( 1 );
    currentAction = Action.STOPPED;
  }

  public void updateState()
  {
    if( currentAction != Action.MOVING )
    { return; }

    int leftDiff = Math.abs( leftMotor.getCurrentPosition() - leftMotor.getTargetPosition() );
    int rightDiff = Math.abs( rightMotor.getCurrentPosition() - rightMotor.getTargetPosition() );

    int diff = Math.min( leftDiff, rightDiff );

    //stop once we get close to our target position
    telemetry.log().add( String.format( "lift diff: %s", diff ) );

    if( diff <= 20 )
    {
      telemetry.log().add( "close enough, auto stop" );
      stop();
    }

    //switch from floating to using power once we get close to our target position
    else if( targetPower == 0 &&
             ( liftPosition() <= MINIMUM_COAST_HEIGHT ||
               Math.min( leftDiff, rightDiff ) < FAR_AWAY ) )
    {
      leftMotor.setPower( targetPower );
      rightMotor.setPower( targetPower );
    }
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    telemetry.addLine( String.format( "Lift Action: %s", currentAction ) );

    if( leftMotor != null )
    { telemetry.addLine( String.format( "Left Lift Motor: %s", leftMotor.getCurrentPosition() ) ); }

    if( rightMotor != null )
    { telemetry.addLine( String.format( "Right Lift Motor: %s", rightMotor.getCurrentPosition() ) ); }
  }

  private boolean turnMotors( DcMotorSimple.Direction direction, double power )
  {
    if( power <= 0 )
    {
      stop();
      return false;
    }

    int liftCurPosition = liftPosition();
    int liftNewPosition = direction == DcMotorSimple.Direction.FORWARD ?
                          liftCurPosition + ADJUST_UP :
                          liftCurPosition - ADJUST_DOWN;

    //Prevent moving too far
    if( liftNewPosition > Position.MAX_LIFT.value )
    {
      liftNewPosition = Position.MAX_LIFT.value;
    }
    else if( liftNewPosition < Position.FLOOR.value )
    {
      liftNewPosition = Position.FLOOR.value;
    }

    // Ensure we continue to lift to preset position as we release buttons
    if( currentAction == Action.MOVING )
    {
      if( direction == DcMotorSimple.Direction.FORWARD && liftNewPosition <= leftMotor.getTargetPosition() )
      { return false; }

      if( direction == DcMotorSimple.Direction.REVERSE && liftNewPosition >= leftMotor.getTargetPosition() )
      { return false; }
    }

    if( liftNewPosition != leftMotor.getCurrentPosition() ||
        liftNewPosition != rightMotor.getCurrentPosition() )
    {
      power = adjustPower( liftNewPosition, power );

      setMotorPosition( leftMotor, liftNewPosition, power );
      setMotorPosition( rightMotor, liftNewPosition, power );
      Action cachedAction = currentAction;
      currentAction = Action.MOVING;
      return currentAction != cachedAction;
    }
    else
    { return false; }
  }

  private void setMotorPosition( DcMotor motor, int position, double power )
  {
    if( motor == null )
    { return; }

    motor.setTargetPosition( position );
    motor.setPower( power );
  }

}
