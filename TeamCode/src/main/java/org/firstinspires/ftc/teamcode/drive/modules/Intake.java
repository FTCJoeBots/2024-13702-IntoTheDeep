package org.firstinspires.ftc.teamcode.drive.modules;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Intake extends AbstractModule
{
  public static final double SLOW_SPEED = 0.1;
  public static final double FAST_SPEED = 1;
  public static final double STOP_SPEED = 0;

  public enum CurrentAction
  {
    PULLING_IN,
    SPITTING_OUT,
    DOING_NOTHING
  }

  private CurrentAction currentAction = CurrentAction.DOING_NOTHING;

  private boolean colorKnown = false;
  private NormalizedRGBA colors = new NormalizedRGBA();

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

  public void updateState()
  {
    // Get the normalized colors from the sensor
    colors = colorSensor.getNormalizedColors();
    colorKnown = true;
  }

  public ObservedObject getObservedObject()
  {
    if ( !colorKnown )
    { updateState(); }

    // Update the hsvValues array by passing it to Color.colorToHSV()
    Color.colorToHSV( colors.toColor(), hsvValues );

    if( colorSensor instanceof DistanceSensor )
    {
      double distance = ( ( DistanceSensor ) colorSensor ).getDistance( DistanceUnit.CM );

      if( Double.valueOf( distance ).isNaN() || distance > 10 )
      {
        return ObservedObject.NOTHING;
      }
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

  // You can give the sensor a gain value, will be multiplied by the sensor's raw value before the
  // normalized color values are calculated. Color sensors (especially the REV Color Sensor V3)
  // can give very low values (depending on the lighting conditions), which only use a small part
  // of the 0-1 range that is available for the red, green, and blue values. In brighter conditions,
  // you should use a smaller gain than in dark conditions. If your gain is too high, all of the
  // colors will report at or near 1, and you won't be able to determine what color you are
  // actually looking at. For this reason, it's better to err on the side of a lower gain
  // (but always greater than  or equal to 1).
  final float gain = 2;

  // Once per loop, we will update this hsvValues array. The first element (0) will contain the
  // hue, the second element (1) will contain the saturation, and the third element (2) will
  // contain the value. See http://web.archive.org/web/20190311170843/https://infohost.nmt.edu/tcc/help/pubs/colortheory/web/hsv.html
  // for an explanation of HSV color.
  final float[] hsvValues = new float[3];

  private CRServo leftServo = null;
  private CRServo rightServo = null;
  private NormalizedColorSensor colorSensor = null;

  public Intake( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  private void initObjects()
  {
    leftServo = createServo( "leftIntakeServo" );
    rightServo = createServo( "rightIntakeServo" );

    // Get a reference to our sensor object. It's recommended to use NormalizedColorSensor over
    // ColorSensor, because NormalizedColorSensor consistently gives values between 0 and 1, while
    // the values you get from ColorSensor are dependent on the specific sensor you're using.
    colorSensor = hardwareMap.get( NormalizedColorSensor.class, "color" );
  }

  private void initState()
  {
    initServo( leftServo, DcMotorSimple.Direction.FORWARD );
    initServo( rightServo, DcMotorSimple.Direction.REVERSE );

    // If possible, turn the light on in the beginning (it might already be on anyway,
    // we just make sure it is if we can).
    if( colorSensor instanceof SwitchableLight )
    {
      ( ( SwitchableLight ) colorSensor ).enableLight( true );
    }

    // Tell the sensor our desired gain value)
    colorSensor.setGain( gain );
  }

  private void initServo( CRServo servo, DcMotorSimple.Direction direction )
  {
    servo.setDirection( direction );
    servo.setPower( STOP_SPEED );
  }

  private void setServoSpeed( double speed )
  {
    leftServo.setPower( speed );
    rightServo.setPower( speed );
  }

  public void pullInSample()
  {
    setServoSpeed( FAST_SPEED );
    currentAction = CurrentAction.PULLING_IN;
  }

  public void spitOutSample()
  {
    setServoSpeed( -SLOW_SPEED );
    currentAction = CurrentAction.SPITTING_OUT;
  }

  @Override
  public void stop()
  {
    super.stop();
    currentAction = CurrentAction.DOING_NOTHING;
  }

  public Boolean actUponColor()
  {
    if(currentAction == CurrentAction.DOING_NOTHING )
    { return false; }

    boolean sampleDetected = getObservedObject() != ObservedObject.NOTHING;

    switch( currentAction )
    {
      case PULLING_IN:
        if( sampleDetected )
        {
          stop();
          return true;
        }
        break;
      case SPITTING_OUT:
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
//    telemetry.addLine( String.format( "Left Intake Servo -  %s", leftServo.getPower() ) );
 //   telemetry.addLine( String.format( "Right Intake Servo -  %s", rightServo.getPower() ) );

    if ( !colorKnown )
    { updateState(); }

    // Update the hsvValues array by passing it to Color.colorToHSV()
    Color.colorToHSV( colors.toColor(), hsvValues );

//    telemetry.addLine().addData( "Red", "%.3f", colors.red ).addData( "Green", "%.3f", colors.green ).addData( "Blue", "%.3f", colors.blue );
    telemetry.addLine().addData( "Hue", "%.3f", hsvValues[ 0 ] ).addData( "Saturation", "%.3f", hsvValues[ 1 ] );

    /* If this color sensor also has a distance sensor, display the measured distance.
     * Note that the reported distance is only useful at very close range, and is impacted by
     * ambient light and surface reflectivity. */
    if (colorSensor instanceof DistanceSensor ) {
      telemetry.addData("Distance (cm)", "%.3f", ((DistanceSensor) colorSensor).getDistance( DistanceUnit.CM));
    }

    switch( getObservedObject() )
    {
      case RED_SAMPLE:
        telemetry.addLine( "we see a red sample");
        break;
      case BLUE_SAMPLE:
        telemetry.addLine( "we see a blue sample");
 break;
      case YELLOW_SAMPLE:
        telemetry.addLine( "we see a yellow sample");
        break;
      case NOTHING:
        telemetry.addLine( "we see a no sample");
        break;
    }
  }
}