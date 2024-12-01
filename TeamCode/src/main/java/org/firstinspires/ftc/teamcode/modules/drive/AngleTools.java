package org.firstinspires.ftc.teamcode.modules.drive;

import org.firstinspires.ftc.teamcode.enums.PresetDirection;
import org.firstinspires.ftc.teamcode.enums.RotateDirection;

public class AngleTools
{
  public static RotateDirection quickestDirection( double currentAngle, double nextAngle )
  {
    double left = nextAngle - currentAngle;
    if( left < 0 )
    {
      left += 360;
    }

    final double right = 360 - left;

    if( left <= right )
    { return RotateDirection.LEFT; }
    else
    { return RotateDirection.RIGHT; }
  }

  //converts [-180, 180] to [0, 360]
  public static double angleForHeading( double heading )
  {
    return heading + 180;
  }

  //returns a value between [ -180, 180 ]
  public static double headingForDirection( PresetDirection direction )
  {
    switch( direction )
    {
      case FOREWARD:
        return 0;
      case BACKWARD:
        return 180;
      case LEFT:
        return 90;
      case RIGHT:
        return -90;
      case DOWN_LEFT:
        return 135;
      case DOWN_RIGHT:
        return -135;
      case UP_LEFT:
        return 45;
      case UP_RIGHT:
        return -45;
      default:
        return 0;
    }
  }

  //returns the difference between two angles in degrees
  public static double angleDifference( double angle1, double angle2 )
  {
    //find the bigger and smaller angle and convert from [-180, 180] to [0, 360]
    final double biggerAngle = Math.max( angle1, angle2 ) + 180;
    final double smallerAngle = Math.min( angle1, angle2 ) + 180;
    return Math.min( biggerAngle - smallerAngle,
                     smallerAngle + (360 - biggerAngle ) );
  }
}
