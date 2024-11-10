package org.firstinspires.ftc.teamcode.enums;

import com.acmerobotics.roadrunner.Vector2d;

public enum Location
{
  SPECIMEN_BAR( 25, 0 ),
  STARTING_POSITION_BASKETS( 0, 15.1 ), //left edge of robot is 2 tiles over from corner
  STARTING_POSITION_SPECIMENS( 0, -15.6 ),
  SAMPLE_BASKETS( 11.1, 50.2 ),

  NEAR_THE_OBSERVATION_ZONE( 11.8 , -49.6 ),
  OBSERVATION_ZONE( -2.9, -53.3 ),

  NEAR_ASCENT_ZONE( 65, 34.6 ),
  ASCENT_ZONE( 65, 15.8 ),

  NEAR_YELLOW_SAMPLES( 44.1, 27.3 ),
  YELLOW_SAMPLE_1( 33.4, 43.8 ),
  YELLOW_SAMPLE_2( 63.6, 44.5 ),
  YELLOW_SAMPLE_3( 61.4, 47.2 ),

  NEAR_TEAM_SAMPLES( 28.0, -40 ),
  TEAM_SAMPLE_1( 35, -50 ),
  TEAM_SAMPLE_2( 35, -60 ),
  TEAM_SAMPLE_3( 35, -67 );

  Location( double x, double y )
  {
    this.value = new Vector2d( x,y );
  }

  public final Vector2d value;
}
