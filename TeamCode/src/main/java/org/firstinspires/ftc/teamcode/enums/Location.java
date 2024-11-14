package org.firstinspires.ftc.teamcode.enums;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Vector2d;

@Config
public class Location
{
  public static Vector2d SPECIMEN_BAR_LEFT = new Vector2d( 23, 7 );
  public static Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 23, -7 );

  //left edge of robot is 2 tiles over from left corner
  public static Vector2d STARTING_POSITION_BASKETS = new Vector2d( 0, 15.1 );

  //right edge of robot is 2 tiles over from right corner
  public static Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 0, -15.6 );
  public static Vector2d SAMPLE_BASKETS = new Vector2d( 8.8, 53 );

  public static Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 20.5, -61.9 );
  public static Vector2d OBSERVATION_ZONE = new Vector2d( 7, -53.8 );
  public static Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 4.5, -53.8 );

  public static Vector2d NEAR_ASCENT_ZONE = new Vector2d( 63.9, 52 );
  public static Vector2d ASCENT_ZONE = new Vector2d( 62.2, 25 );

  public static Vector2d NEAR_YELLOW_SAMPLES_1 = new Vector2d( 30, 40 );
  public static Vector2d NEAR_YELLOW_SAMPLES_2 = new Vector2d( 60, 40 );
  public static Vector2d YELLOW_SAMPLE_1 = new Vector2d( 22.3, 48 );
  public static Vector2d YELLOW_SAMPLE_2 = new Vector2d( 52.9, 59.2 );
  public static Vector2d YELLOW_SAMPLE_3 = new Vector2d( 50, 57.6 );

  public static Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 30.0, -34 );
  public static Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 50.0, -34 );
  public static Vector2d TEAM_SAMPLE_1 = new Vector2d( 60, -45 );
  public static Vector2d TEAM_SAMPLE_2 = new Vector2d( 60, -52 );
  public static Vector2d TEAM_SAMPLE_3 = new Vector2d( 60, -61.9 );

  public enum NamedLocation
  {
    _SPECIMEN_BAR_LEFT,
    _SPECIMEN_BAR_RIGHT,
    _STARTING_POSITION_BASKETS,
    _STARTING_POSITION_SPECIMENS,
    _SAMPLE_BASKETS,
    _NEAR_THE_OBSERVATION_ZONE,
    _OBSERVATION_ZONE,
    _PARK_IN_OBSERVATION_ZONE,
    _NEAR_ASCENT_ZONE,
    _ASCENT_ZONE,
    _NEAR_YELLOW_SAMPLES_1,
    _NEAR_YELLOW_SAMPLES_2,
    _YELLOW_SAMPLE_1,
    _YELLOW_SAMPLE_2,
    _YELLOW_SAMPLE_3,
    _NEAR_TEAM_SAMPLES_1,
    _NEAR_TEAM_SAMPLES_2,
    _TEAM_SAMPLE_1,
    _TEAM_SAMPLE_2,
    _TEAM_SAMPLE_3
  }

  public static Vector2d position( NamedLocation location )
  {
    switch( location )
    {
      case _SPECIMEN_BAR_LEFT:
        return SPECIMEN_BAR_LEFT;
      case _SPECIMEN_BAR_RIGHT:
        return SPECIMEN_BAR_RIGHT;
      case _STARTING_POSITION_BASKETS:
        return STARTING_POSITION_BASKETS;
      case _STARTING_POSITION_SPECIMENS:
        return STARTING_POSITION_SPECIMENS;
      case _SAMPLE_BASKETS:
        return SAMPLE_BASKETS;
      case _NEAR_THE_OBSERVATION_ZONE:
        return NEAR_THE_OBSERVATION_ZONE;
      case _OBSERVATION_ZONE:
        return OBSERVATION_ZONE;
      case _PARK_IN_OBSERVATION_ZONE:
        return PARK_IN_OBSERVATION_ZONE;
      case _NEAR_ASCENT_ZONE:
        return NEAR_ASCENT_ZONE;
      case _ASCENT_ZONE:
        return ASCENT_ZONE;
      case _NEAR_YELLOW_SAMPLES_1:
        return NEAR_YELLOW_SAMPLES_1;
      case _NEAR_YELLOW_SAMPLES_2:
        return NEAR_YELLOW_SAMPLES_2;
      case _YELLOW_SAMPLE_1:
        return YELLOW_SAMPLE_1;
      case _YELLOW_SAMPLE_2:
        return YELLOW_SAMPLE_2;
      case _YELLOW_SAMPLE_3:
        return YELLOW_SAMPLE_3;
      case _NEAR_TEAM_SAMPLES_1:
        return NEAR_TEAM_SAMPLES_1;
      case _NEAR_TEAM_SAMPLES_2:
        return NEAR_TEAM_SAMPLES_2;
      case _TEAM_SAMPLE_1:
        return TEAM_SAMPLE_1;
      case _TEAM_SAMPLE_2:
        return TEAM_SAMPLE_2;
      case _TEAM_SAMPLE_3:
        return TEAM_SAMPLE_3;

      default:
        return new Vector2d( 0, 0 );
    }
  }
}
