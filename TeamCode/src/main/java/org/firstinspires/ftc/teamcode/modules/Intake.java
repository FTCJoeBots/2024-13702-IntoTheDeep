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

  private static final double SPIT_OUT_SPEED = 1;
  private static final double PULL_IN_SPEED = 0.2;
  private static final double STOP_SPEED = 0;
  private static final double DELAY = 100;

  public enum Direction
  {
    PULL,
    PUSH
  }

  public enum CurrentAction
  {
    PULL_IN_SAMPLE_FROM_IN_FRONT,
    PULL_IN_SAMPLE_FROM_BEHIND,
    TURN_OFF_AFTER_DELAY,
    SPIT_OUT_SAMPLE_IN_FRONT,
    SPIT_OUT_SAMPLE_BEHIND,
    DOING_NOTHING
  }

  private CurrentAction currentAction = CurrentAction.DOING_NOTHING;

  public enum ObservedObject
  {
    RED_SAMPLE,
    BLUE_SAMPLE,
    YELLOW_SAMPLE,
    NOTHING
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

     if( hue < 15 || hue > 325 )
    { return ObservedObject.RED_SAMPLE; }
    else if( hue > 30 && hue < 75 )
    { return ObservedObject.YELLOW_SAMPLE; }
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

  public void pullSampleBack()
  {
    turnOnServos( Direction.PULL );
  }

  public void pushSampleForward()
  {
    turnOnServos( Direction.PUSH );
  }

  private void turnOnServos( Direction direction )
  {
    if( direction == Direction.PULL &&
        ( currentAction == CurrentAction.PULL_IN_SAMPLE_FROM_IN_FRONT ||
          currentAction == CurrentAction.SPIT_OUT_SAMPLE_BEHIND ) )
    { return; }

    if( direction == Direction.PUSH &&
      ( currentAction == CurrentAction.SPIT_OUT_SAMPLE_IN_FRONT ||
        currentAction == CurrentAction.PULL_IN_SAMPLE_FROM_BEHIND ) )
    { return; }

    boolean sampleDetected = getObservedObject() != ObservedObject.NOTHING;

    if( direction == Direction.PULL )
    {
      currentAction = sampleDetected ?
                      CurrentAction.SPIT_OUT_SAMPLE_BEHIND :
                      CurrentAction.PULL_IN_SAMPLE_FROM_IN_FRONT;
    }
    else
    {
      currentAction = sampleDetected ?
                      CurrentAction.SPIT_OUT_SAMPLE_IN_FRONT :
                      CurrentAction.PULL_IN_SAMPLE_FROM_BEHIND;
    }

    int    multiplier = direction == Direction.PULL ? -1 : 1;
    double speed      = sampleDetected ? SPIT_OUT_SPEED : PULL_IN_SPEED;

    setServoSpeed( multiplier * speed );
  }

  //every time we clip in auto shift over a bit
  //reset position by overhsooting and tap wall when returning to basekt or human player pickup
  //auto rasie and then lower the extension arm to avoid bashing it into the middle. this would trigger
  //when extension arm is at 0
  @Override
  public void stop()
  {
    currentAction = CurrentAction.DOING_NOTHING;
    super.stop();
  }

  public Boolean actUponColor()
  {
    if( currentAction == CurrentAction.DOING_NOTHING )
    { return false; }

    boolean sampleDetected = getObservedObject() != ObservedObject.NOTHING;

    switch( currentAction )
    {
      case PULL_IN_SAMPLE_FROM_IN_FRONT:
        if( sampleDetected )
        {
          stop();
          return true;
        }
        break;

      //delay turning off servos until after the sample has a chance to be pulled in
      case PULL_IN_SAMPLE_FROM_BEHIND:
        if( sampleDetected )
        {
          currentAction = CurrentAction.TURN_OFF_AFTER_DELAY;
          time.reset();
        }
        break;

      case TURN_OFF_AFTER_DELAY:
        if( time.milliseconds() >= DELAY )
        {
          stop();
          return true;
        }

      case SPIT_OUT_SAMPLE_IN_FRONT:
      case SPIT_OUT_SAMPLE_BEHIND:
        if( !sampleDetected )
        {
          stop();
          return true;
        }
        break;

      case DOING_NOTHING:
        break;
    }

    return false;
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

    // Update the hsvValues array by passing it to Color.colorToHSV()
    Color.colorToHSV( colors.toColor(), hsvValues );

    //    telemetry.addLine().addData( "Red", "%.3f", colors.red ).addData( "Green", "%.3f", colors.green ).addData( "Blue", "%.3f", colors.blue );
    telemetry.addLine().addData( "Hue", "%.3f", hsvValues[ 0 ] ).addData( "Saturation", "%.3f", hsvValues[ 1 ] );

    /* If this color sensor also has a distance sensor, display the measured distance.
     * Note that the reported distance is only useful at very close range, and is impacted by
     * ambient light and surface reflectivity. */
    if( colorSensor != null && colorSensor instanceof DistanceSensor )
    {
      telemetry.addData( "Distance (cm)", "%.3f", distance );
    }

    switch( getObservedObject() )
    {
      case RED_SAMPLE:
        telemetry.addLine( "Red sample");
        break;
      case BLUE_SAMPLE:
        telemetry.addLine( "Blue sample");
        break;
      case YELLOW_SAMPLE:
        telemetry.addLine( "Yellow sample");
        break;
      case NOTHING:
        telemetry.addLine( "No sample");
        break;
    }

    telemetry.addData( "Current Action:", "%s", currentAction );
  }

}