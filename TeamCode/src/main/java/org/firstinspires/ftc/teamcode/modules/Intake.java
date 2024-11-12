package org.firstinspires.ftc.teamcode.modules;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.JoeBot;

public class Intake extends AbstractModule
{
  private CRServo leftServo = null;
  private CRServo rightServo = null;
  private NormalizedColorSensor colorSensor = null;

  private boolean colorKnown = false;
  private NormalizedRGBA colors = new NormalizedRGBA();

  // Once per loop, we will update this hsvValues array. The first element (0) will contain the
  // hue, the second element (1) will contain the saturation, and the third element (2) will
  // contain the value. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
  // for an explanation of HSV color.
  final float[] hsvValues = new float[ 3 ];

  private double distance = Double.NaN;

  private ElapsedTime time = null;
  private int delay = 0;

  private static final double SPIT_OUT_SPEED = 1;
  private static final double PULL_IN_SPEED = 0.25; //0.2;
  private static final double STOP_SPEED = 0;

  //continue running the servos briefly after we see the sample
  //to ensure it is *centered* within the intake
  private static final int CENTER_DELAY = 100;
  private static final int EJECT_DELAY = 400;

  public enum Direction
  {
    PULL,
    PUSH
  }

  public enum Action
  {
    PULL_IN_SAMPLE_FROM_IN_FRONT,
    PULL_IN_SAMPLE_FROM_BEHIND,
    TURN_OFF_AFTER_DELAY,
    SPIT_OUT_SAMPLE_IN_FRONT,
    SPIT_OUT_SAMPLE_BEHIND,
    DOING_NOTHING
  }

  private Action currentAction = Action.DOING_NOTHING;

  public enum ObservedObject
  {
    RED_SAMPLE,
    BLUE_SAMPLE,
    YELLOW_SAMPLE,
    NOTHING
  }

  public boolean isMoving()
  {
    return currentAction != Action.DOING_NOTHING;
  }

  public boolean hasSample()
  {
    return getObservedObject() != Intake.ObservedObject.NOTHING;
  }

  public void pullSampleBack()
  {
    turnOnServos( Direction.PULL );
  }

  public void pushSampleForward()
  {
    turnOnServos( Direction.PUSH );
  }

  public void resetColor()
  {
    colorKnown = false;
  }

  public void updateColorAndDistance()
  {
    if( colorSensor == null )
    { return; }

    // Get the normalized colors from the sensor
    colors = colorSensor.getNormalizedColors();

    // Update the hsvValues array by passing it to Color.colorToHSV()
    Color.colorToHSV( colors.toColor(), hsvValues );

    if( colorSensor instanceof DistanceSensor )
    {
      distance = ( ( DistanceSensor ) colorSensor ).getDistance( DistanceUnit.CM );
    }

    colorKnown = true;
  }

  public ObservedObject getObservedObject()
  {
    if( colorSensor == null )
    { return ObservedObject.NOTHING; }

    if( !colorKnown )
    { updateColorAndDistance(); }

    //tape on ground is 8.8
    //samples are 5.5
    if( Double.valueOf( distance ).isNaN() ||
        distance > 7 )
    {
      return ObservedObject.NOTHING;
    }

    //samples have a saturation of .7
    //the ground has a saturation of .2
    float saturation = hsvValues[ 1 ];
    if( saturation < 0.4 )
    {
      return ObservedObject.NOTHING;
    }

    float hue = hsvValues[ 0 ];

    //~24
     if( hue < 40 || hue > 325 )
    { return ObservedObject.RED_SAMPLE; }
     //~90
    else if( hue > 70 && hue < 110 )
    { return ObservedObject.YELLOW_SAMPLE; }
    //~210
    else if( hue > 195 && hue < 265 )
    { return ObservedObject.BLUE_SAMPLE; }

    return ObservedObject.NOTHING;
  }

  public Intake( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  private void initObjects()
  {
    time = new ElapsedTime();

    leftServo = createCRServo( "leftIntakeServo" );
    rightServo = createCRServo( "rightIntakeServo" );

    // Get a reference to our sensor object. It's recommended to use NormalizedColorSensor over
    // ColorSensor, because NormalizedColorSensor consistently gives values between 0 and 1, while
    // the values you get from ColorSensor are dependent on the specific sensor you're using.
    colorSensor = hardwareMap.get( NormalizedColorSensor.class, "colorSensor" );
  }

  private void initState()
  {
    initServo( leftServo, DcMotorSimple.Direction.FORWARD );
    initServo( rightServo, DcMotorSimple.Direction.REVERSE );

    if( colorSensor != null )
    {
      if( colorSensor instanceof SwitchableLight )
      {
        ( ( SwitchableLight ) colorSensor ).enableLight( true );
      }

      // Tell the sensor our desired gain value)
      //
      // You can give the sensor a gain value, will be multiplied by the sensor's raw value before the
      // normalized color values are calculated. Color sensors (especially the REV Color Sensor V3)
      // can give very low values (depending on the lighting conditions), which only use a small part
      // of the 0-1 range that is available for the red, green, and blue values. In brighter conditions,
      // you should use a smaller gain than in dark conditions. If your gain is too high, all of the
      // colors will report at or near 1, and you won't be able to determine what color you are
      // actually looking at. For this reason, it's better to err on the side of a lower gain
      // (but always greater than  or equal to 1).
      colorSensor.setGain( 2 );

      updateColorAndDistance();
    }
  }

  private void initServo( CRServo servo, DcMotorSimple.Direction direction )
  {
    if( servo == null )
    { return; }

    servo.setDirection( direction );
    servo.setPower( STOP_SPEED );
  }

  private void setServoSpeed( double speed )
  {
    if( leftServo != null )
    { leftServo.setPower( speed ); }

    if( rightServo != null )
    { rightServo.setPower( speed ); }
  }

  private void turnOnServos( Direction direction )
  {
    if( direction == Direction.PULL &&
        ( currentAction == Action.PULL_IN_SAMPLE_FROM_IN_FRONT ||
          currentAction == Action.SPIT_OUT_SAMPLE_BEHIND ) )
    { return; }

    if( direction == Direction.PUSH &&
      ( currentAction == Action.SPIT_OUT_SAMPLE_IN_FRONT ||
        currentAction == Action.PULL_IN_SAMPLE_FROM_BEHIND ) )
    { return; }

    boolean sampleDetected = hasSample();

    if( direction == Direction.PULL )
    {
      currentAction = sampleDetected ?
                      Action.SPIT_OUT_SAMPLE_BEHIND :
                      Action.PULL_IN_SAMPLE_FROM_IN_FRONT;
    }
    else
    {
      currentAction = sampleDetected ?
                      Action.SPIT_OUT_SAMPLE_IN_FRONT :
                      Action.PULL_IN_SAMPLE_FROM_BEHIND;
    }

    if( JoeBot.debugging )
    { telemetry.log().add( "Intake.turnOnServos - turning on" ); }

    int    multiplier = direction == Direction.PULL ? -1 : 1;
    double speed      = sampleDetected ? SPIT_OUT_SPEED : PULL_IN_SPEED;

    setServoSpeed( multiplier * speed );
  }

  @Override
  public void stop()
  {
    if( JoeBot.debugging )
    { telemetry.log().add( "Intake.stop" ); }
    currentAction = Action.DOING_NOTHING;
    super.stop();
  }

  public void updateState()
  {
    if( currentAction == Action.DOING_NOTHING )
    { return; }

    resetColor();
    boolean sampleDetected = hasSample();

    switch( currentAction )
    {
      case PULL_IN_SAMPLE_FROM_IN_FRONT:
      case PULL_IN_SAMPLE_FROM_BEHIND:
        //ensure sample is centered before turning off servos
        if( sampleDetected )
        {
          if( JoeBot.debugging )
          { telemetry.log().add( "Intake sampleDetected, scheduling turning off" ); }
          currentAction = Action.TURN_OFF_AFTER_DELAY;
          time.reset();
          delay = CENTER_DELAY;
        }
        break;

      case SPIT_OUT_SAMPLE_IN_FRONT:
      case SPIT_OUT_SAMPLE_BEHIND:
        //ensure sample is full ejected before turning off servos
        if( !sampleDetected )
        {
          if( JoeBot.debugging )
          { telemetry.log().add( "Intake sampleLost, scheduling turning off" ); }
          currentAction = Action.TURN_OFF_AFTER_DELAY;
          time.reset();
          delay = EJECT_DELAY;
        }
        break;

      case TURN_OFF_AFTER_DELAY:
        if( time.milliseconds() >= delay )
        {
          if( JoeBot.debugging )
          { telemetry.log().add( "Intake delay met" ); }
          stop();
        }

      case DOING_NOTHING:
        break;
    }
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    printServo( "Left Intake Servo", leftServo );
    printServo( "Right Intake Servo", rightServo );
    printColor();
  }

  private void printServo( String name, CRServo servo )
  {
    if( servo == null )
    { return; }

    telemetry.addLine( name + ": " + String.format( "%s", servo.getPower() ) );
  }

  private void printColor()
  {
    if( colorSensor == null )
    { return; }

    if ( !colorKnown )
    { updateColorAndDistance(); }

    telemetry.addLine().addData( "Hue", "%.3f", hsvValues[ 0 ] ).addData( "Saturation", "%.3f", hsvValues[ 1 ] );
    telemetry.addData( "Distance (cm)", "%.3f", distance );
    telemetry.addData( "Observed:", "%s", getObservedObject() );
    telemetry.addData( "Current Action:", "%s", currentAction );
  }
}