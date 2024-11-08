package org.firstinspires.ftc.teamcode.opmode;

import com.acmerobotics.roadrunner.Vector2d;

public enum Location
{
  STARTING_POSITION_BUCKETS( 3, 6 ),
  STARTING_POSITION_SPECIMENS( 4, 8 ),
  SAMPLE_BASKETS( 5, 9),
  SPECIMEN_BAR( 5, 9),
  NEAR_THE_OBSERVATION_ZONE( 8,88),
  OBSERVATION_ZONE( 5, 9),
  ASCENT_ZONE( 5, 9),
  YELLOW_SAMPLE_1( 5, 9),
  YELLOW_SAMPLE_2( 5, 9),
  YELLOW_SAMPLE_3( 5, 9),
  YELLOW_SAMPLE_4( 5, 9),
  YELLOW_SAMPLE_5( 5, 9),
  YELLOW_SAMPLE_6( 5, 9),
  TEAM_SAMPLE_1( 5, 9),
  TEAM_SAMPLE_2( 5, 9),
  TEAM_SAMPLE_3( 5, 9);

  Location( double x, double y )
  {
    this.value = new Vector2d( x,y );
  }

  public final Vector2d value;
}
