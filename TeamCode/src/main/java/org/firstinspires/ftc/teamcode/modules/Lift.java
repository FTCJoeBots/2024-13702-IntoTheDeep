package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Lift extends AbstractModule
{
  private static final double FAST_SPEED_UP = 1.0;
  private static final double FAST_SPEED_DOWN = 1.0;

  private static final double COAST = 0;

  private static final int ADJUST_UP   = 100;
  private static final int ADJUST_DOWN = 100;

  //coast down until we are close to our target
  //IMPORTANT: this value should be less than ADJUST_DOWN or else we never will coast
  //when manually moving the lift down
  private static final int FAR_AWAY = 50;

  //only coast down above a minimum height since
  //gravity does not seem to cause the lift as we get close
  //to the bottom and it is necessary to use the motors to pull the lift
  //the rest of the way down.
  private static final int MINIMUM_COAST_HEIGHT = 700;

  //Preset positions we can extend the arm to
  public enum Position
  {
    FLOOR( 0 ),
    MAX_LIFT( 5200 ),

    SAMPLE_FLOOR( 90 ),
    SPECIMEN_FLOOR( 116 ),

    //high enough that we don't hit the submersible bar when retracting
    TRAVEL_WITH_SPECIMEN( 480 ),

    //putting samples in baskets
    HIGH_BASKET( 5150 ),
    LOW_BASKET( 3302 ),

    //hanging specimens
    ABOVE_HIGH_SPECIMEN_BAR( 3384 ),
    ABOVE_LOW_SPECIMEN_BAR( 1912 ),
    SPECIMEN_CLIPPED_ONTO_HIGH_BAR( 3135 ),
    SPECIMEN_CLIPPED_ONTO_LOW_BAR( 1590 ),

    //level 1 ascent
    AT_LOW_HANG_BAR( 2116 ),

    //level 2 ascent
    ABOVE_ABOVE_HANG_BAR( 4470 ),
    TOUCHING_HIGH_HANG_BAR( 3685 ),

    //height above which we should limit extending the extension arm to avoid tipping over
    HIGH_UP( 3472 );

    Position( int value )
    {
      this.value = value;
    }

    public final int value;
  }

  DcMotorEx leftMotor = null;
  DcMotorEx rightMotor = null;

  private enum Action
  {
    MOVING,
    CLIMBING,
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
    return currentAction != Action.STOPPED;
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

  public boolean fastDrop()
  {
    return turnMotors( DcMotorSimple.Direction.REVERSE, FAST_SPEED_DOWN );
  }

  public boolean climb()
  {
    double power = 1.0;
    int position = Position.TOUCHING_HIGH_HANG_BAR.value;

    setMotorPosition( leftMotor, position, power );
    setMotorPosition( rightMotor, position, power );
    currentAction = Action.CLIMBING;
    return true;
  }

  private double adjustPower( int targetPosition, double power )
  {
    targetPower = power;

    //it is smoother if we coast downwards until we get
    //close to our target position
    int liftCurPosition = liftPosition();
    double liftVelocity = leftMotor.getVelocity();

    if( targetPosition < liftCurPosition &&
      liftCurPosition > MINIMUM_COAST_HEIGHT &&
      Math.abs( targetPosition - liftCurPosition ) > FAR_AWAY &&
      //wait until the lift starts moving before switching to coast most
      //to avoid getting stuck
      liftVelocity > 10 )
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
    travelTo( position.value );
  }

  public void travelTo( int value )
  {
    double power = liftPosition() < value ?
                   FAST_SPEED_UP :
                   FAST_SPEED_DOWN;

    power = adjustPower( value, power );

    setMotorPosition( leftMotor, value, power );
    setMotorPosition( rightMotor, value, power );
    currentAction = Action.MOVING;
  }

  public void floatMotors()
  {
    leftMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
    rightMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
    leftMotor.setPower( 0 );
    rightMotor.setPower( 0 );
    currentAction = Action.STOPPED;
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
    if( currentAction == Action.STOPPED )
    { return; }

    int leftDiff = Math.abs( leftMotor.getCurrentPosition() - leftMotor.getTargetPosition() );
    int rightDiff = Math.abs( rightMotor.getCurrentPosition() - rightMotor.getTargetPosition() );

    int diff = Math.min( leftDiff, rightDiff );

    //stop once we get close to our target position
    if( diff <= 2 )
    {
      telemetry.log().add( String.format( "Lift.updateState stopping, diff: %s", diff ) );
      stop();
    }

    //switch from floating to using power once we get close to our target position
    else if( leftMotor.getZeroPowerBehavior() == DcMotor.ZeroPowerBehavior.FLOAT &&
             ( liftPosition() <= MINIMUM_COAST_HEIGHT ||
               Math.min( leftDiff, rightDiff ) < FAR_AWAY ) )
    {
      leftMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      rightMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
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
    {
      telemetry.addLine( String.format( "Left Lift Motor: %s", leftMotor.getCurrentPosition() ) );
      telemetry.addLine( String.format( "Left Lift Velocity: %f", leftMotor.getVelocity() ) );
    }

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
    if( currentAction != Action.STOPPED )
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

  private void setMotorPosition( DcMotorEx motor, int position, double power )
  {
    if( motor == null )
    { return; }

    motor.setTargetPosition( position );
    motor.setPower( power );
  }

}
