package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {

  private static final double hangSpecimenDelay = 2;
  private static final double placeSampleDelay = 4;
  private static final double grabSpecimenDelay = 1.5;
  private static final double level1Delay = 1;

  public static void main(String[] args) {
    MeepMeep meepMeep = new MeepMeep( 800 );

    RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
      // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
      .setConstraints(50, 50, Math.PI, Math.PI, 15)
      .build();

     basketStrategy( myBot );
//    colomaApproach( myBot );
//    fastStrafeApproach( myBot );
//    splineGrabApproach( myBot );
//    splineTackleApproach( myBot );
//    splineTackleApproach2( myBot );

    meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_LIGHT)
      .setDarkMode(false)
      .setBackgroundAlpha(0.95f)
      .addEntity(myBot)
      .start();
  }
  //============================================================
  static void basketStrategy( RoadRunnerBotEntity myBot )
  {
    final Vector2d STARTING_POSITION_BASKETS = new Vector2d( -15.1, -61 );
    final Vector2d SPECIMEN_BAR_LEFT = new Vector2d( -7, -36 );
    final Vector2d YELLOW_SAMPLE_1 = new Vector2d( -48.7, -37.5 );
    final Vector2d YELLOW_SAMPLE_2 = new Vector2d( -58, -37.5 );
    final Vector2d YELLOW_SAMPLE_3 = new Vector2d( -58.8, -38 );
    final Vector2d SAMPLE_BASKETS = new Vector2d( -52, -51.2 );
    final Vector2d STRAFE_1 = new Vector2d( -50, -11 );
    final Vector2d STRAFE_2 = new Vector2d( -62, -11 );
    final Vector2d STRAFE_3 = new Vector2d( -62, -46 );
    final Vector2d NEAR_ASCENT_ZONE = new Vector2d( -52, -2 );
    final Vector2d ASCENT_ZONE = new Vector2d( -24, -2 );

    final double faceForward = Math.toRadians ( 90 );
    final double faceBasket = Math.toRadians( 225 );
    final double faceRight = Math.toRadians( 0 );
    final double faceSample = Math.toRadians( 135 );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( STARTING_POSITION_BASKETS, faceForward ) )
      //hang specimen
      .strafeToLinearHeading( SPECIMEN_BAR_LEFT, faceForward )
      .waitSeconds( hangSpecimenDelay )

      // grab sample 1
      .strafeToLinearHeading( YELLOW_SAMPLE_1, faceForward )
      .waitSeconds( grabSpecimenDelay )

      // place sample in a basket
      .strafeToLinearHeading( SAMPLE_BASKETS, faceBasket )
      .waitSeconds( placeSampleDelay )

      // grab sample 2
      .strafeToLinearHeading( YELLOW_SAMPLE_2, faceForward )
      .waitSeconds( grabSpecimenDelay )

      // place sample in a basket
      .strafeToLinearHeading( SAMPLE_BASKETS, faceBasket )
      .waitSeconds( placeSampleDelay )

      //strafe 3
     .strafeToLinearHeading( STRAFE_1, faceRight )
     .strafeToLinearHeading( STRAFE_2, faceRight )
     .strafeToLinearHeading( STRAFE_3, faceRight )

      // level one ascent at the end of the autonomous.
      .strafeToLinearHeading( NEAR_ASCENT_ZONE, faceRight )
      .strafeToLinearHeading( ASCENT_ZONE, faceRight )
     .waitSeconds( level1Delay )

      //start animation
      .build());
  }
  //============================================================
  //21.03 seconds: strafe two, hangs two, park
  static void colomaApproach( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 7, -36 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 15.6, -61 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 62.5, -41 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 62.5, -51 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 62.5,  -51 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 34, -33 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 34, -11 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 45, -10 );
    final Vector2d TEAM_SAMPLE_2 = new Vector2d( 54, -10 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceLeft = Math.toRadians( 180 );

    Vector2d strafePos1 = new Vector2d( TEAM_SAMPLE_1.x, STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.y );
    Vector2d strafePos2 = new Vector2d( TEAM_SAMPLE_2.x, STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( STARTING_POSITION_SPECIMENS, faceUp ) )

      //hang specimen
      .strafeTo( SPECIMEN_BAR_RIGHT )
      .waitSeconds( hangSpecimenDelay )

      //strafe in sample
      .strafeToLinearHeading( NEAR_TEAM_SAMPLES_1, faceLeft )
      .strafeToLinearHeading( NEAR_TEAM_SAMPLES_2, faceLeft )
      .strafeToLinearHeading( TEAM_SAMPLE_1, faceLeft )
      .strafeToLinearHeading( strafePos1, faceLeft )

      //grab specimen
      .strafeToLinearHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .strafeTo( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( SPECIMEN_BAR_RIGHT, faceUp )
      .waitSeconds( hangSpecimenDelay )

      //strafe in sample
      .strafeToLinearHeading( NEAR_TEAM_SAMPLES_1, faceLeft )
      .strafeToLinearHeading( NEAR_TEAM_SAMPLES_2, faceLeft )
      .strafeToLinearHeading( TEAM_SAMPLE_2, faceLeft )
      .strafeToLinearHeading( strafePos2, faceLeft )

      .build());
  }
  //============================================================
  //26.07 seconds: strafe 1, hang three, park, with 0.5 second wait for human player
  static void fastStrafeApproach( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 7, -36 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 15.6, -61 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 47, -41 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 47, -50 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 47, -51 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 35, -36 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 35, -7 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 45, -7 );
    final Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 61, -61 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceRight = Math.toRadians( 0 );

    Vector2d strafePos1 = new Vector2d( TEAM_SAMPLE_1.x, STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( STARTING_POSITION_SPECIMENS, faceUp ) )

      //hang specimen
      .strafeTo( SPECIMEN_BAR_RIGHT )
      .waitSeconds( hangSpecimenDelay )

      //strafe in sample
      .strafeToLinearHeading( NEAR_TEAM_SAMPLES_1, faceRight )
      .strafeToLinearHeading( NEAR_TEAM_SAMPLES_2, faceRight )
      .strafeToLinearHeading( TEAM_SAMPLE_1, faceRight )
      .strafeToLinearHeading( strafePos1, faceRight )

      //grab specimen
      .strafeToLinearHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .waitSeconds( 0.5 ) //wait for human player
      .strafeTo( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( new Vector2d( SPECIMEN_BAR_RIGHT.x - 6, SPECIMEN_BAR_RIGHT.y ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //grab specimen
      .strafeToLinearHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .waitSeconds( 0.5 ) //wait for human operator
      .strafeToLinearHeading( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE, faceDown )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( new Vector2d( SPECIMEN_BAR_RIGHT.x - 12, SPECIMEN_BAR_RIGHT.y ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //park
      .strafeTo( PARK_IN_OBSERVATION_ZONE )

      .build());
  }
  //============================================================
  //25.15 seconds: strafe 1, hang three, park, and 0.5 second wait for human player
  static void splineGrabApproach( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 7, -36 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 15.6, -61 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 47, -41 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 47, -50 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 47, -51 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 28, -36 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 35, -14 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 44, -7 );
    final Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 61, -61 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceRight = Math.toRadians( 0 );

    Vector2d strafePos1 = new Vector2d( TEAM_SAMPLE_1.x, STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( STARTING_POSITION_SPECIMENS, faceUp ) )

      //hang specimen
      .strafeTo( SPECIMEN_BAR_RIGHT )
      .waitSeconds( hangSpecimenDelay )

      //strafe in sample
      .strafeToConstantHeading( NEAR_TEAM_SAMPLES_1 )
      .splineToConstantHeading( NEAR_TEAM_SAMPLES_2, faceUp )
      .splineToSplineHeading( new Pose2d( TEAM_SAMPLE_1, faceRight ), 1.6 )
      .strafeToLinearHeading( strafePos1, faceRight )

      //grab specimen
      .strafeToSplineHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .waitSeconds( 0.5 ) //wait for human player
      .strafeTo( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( SPECIMEN_BAR_RIGHT, faceUp )
      .waitSeconds( hangSpecimenDelay )

      //grab specimen
      .strafeToSplineHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .waitSeconds( 0.5 ) //wait for human player
      .strafeToLinearHeading( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE, faceDown )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( SPECIMEN_BAR_RIGHT, faceUp )
      .waitSeconds( hangSpecimenDelay )

      //park
      .strafeTo( PARK_IN_OBSERVATION_ZONE )

      .build());
  }
  //============================================================
  static void splineTackleApproach( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 7, -36 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 15.6, -61 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 47, -41 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 47, -50 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 47, -58 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 28, -36 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 35, -14 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 44, -7 );
    final Vector2d TEAM_SAMPLE_2 = new Vector2d( 54, -7 );
    final Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 61, -61 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceRight = Math.toRadians( 0 );

    Vector2d strafePos1 = new Vector2d( TEAM_SAMPLE_1.x, STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.y );
    Vector2d strafePos2 = new Vector2d( TEAM_SAMPLE_2.x, STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( STARTING_POSITION_SPECIMENS, faceUp ) )

      //hang specimen
      .strafeTo( SPECIMEN_BAR_RIGHT )
      .waitSeconds( hangSpecimenDelay )

      //strafe in first sample
      .strafeToConstantHeading( new Vector2d( NEAR_TEAM_SAMPLES_1.x, NEAR_TEAM_SAMPLES_1.y - 2 ) )
      .splineToConstantHeading( NEAR_TEAM_SAMPLES_2, faceUp )
      .splineToSplineHeading( new Pose2d( TEAM_SAMPLE_1, faceRight ), 1.6 )
      .strafeToLinearHeading( strafePos1, faceRight )

      //grab specimen
      .strafeToLinearHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .waitSeconds( 0.5 ) //wait for human player
      .strafeTo( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE )

      //hang specimen
      .strafeToSplineHeading( SPECIMEN_BAR_RIGHT, faceUp )
      .waitSeconds( hangSpecimenDelay )

      //grab specimen
      .strafeToLinearHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .waitSeconds( 0.5 ) //wait for human player
      .strafeToLinearHeading( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE, faceDown )

      //hang specimen
//      .strafeToLinearHeading( SPECIMEN_BAR_RIGHT, faceUp )
      .strafeToSplineHeading( SPECIMEN_BAR_RIGHT, faceUp )
      .waitSeconds( hangSpecimenDelay )

      //strafe in second sample - takes 5 seconds, if running low we should just park instead?
      .strafeToLinearHeading( new Vector2d( NEAR_TEAM_SAMPLES_1.x + 5.5, NEAR_TEAM_SAMPLES_1.y - 4 ),
        Math.toRadians( 45 ) )
      .splineToSplineHeading( new Pose2d( new Vector2d( NEAR_TEAM_SAMPLES_2.x + 5.5, NEAR_TEAM_SAMPLES_2.y ), faceRight ), 1.6 )
      .splineToConstantHeading( TEAM_SAMPLE_2, faceRight )
      .strafeToLinearHeading( strafePos2, faceRight )

      //park facing up
      .turn( faceUp )

      .build());
  }
  //============================================================
  //28.18 seconds: strafe 2, hang three, park
  static void splineTackleApproach2( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 7, -36 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 15.6, -61 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 47, -41 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 47, -50 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 48, -58.6 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 28, -38 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 36, -14 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 44, -7 );
    final Vector2d TEAM_SAMPLE_2 = new Vector2d( 54, -7 );
    final Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 50, -56 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceRight = Math.toRadians( 0 );

    Vector2d strafePos1 = new Vector2d( TEAM_SAMPLE_1.x, STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.y + 4 );
    Vector2d strafePos2 = new Vector2d( TEAM_SAMPLE_2.x, STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( STARTING_POSITION_SPECIMENS, faceUp ) )

      //hang specimen
      .strafeTo( SPECIMEN_BAR_RIGHT )
      .waitSeconds( hangSpecimenDelay )

      //strafe in first sample
      .strafeToLinearHeading( NEAR_TEAM_SAMPLES_1, faceUp )
      .splineToConstantHeading( NEAR_TEAM_SAMPLES_2, faceUp )
      .splineToSplineHeading( new Pose2d( TEAM_SAMPLE_1, faceRight ), 0 )
      .strafeToLinearHeading( strafePos1, faceRight )

      //strafe in second sample
      .strafeToLinearHeading( new Vector2d( strafePos1.x - 2, strafePos1.y + 20 ), faceRight )
      .splineToConstantHeading( TEAM_SAMPLE_2, faceRight )
      .strafeToLinearHeading( strafePos2, faceRight )

      //grab specimen
      .strafeToLinearHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .waitSeconds( 1 ) //wait for human player
      .strafeTo( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE )

      //hang specimen
      .strafeToSplineHeading( SPECIMEN_BAR_RIGHT, faceUp )
      .waitSeconds( hangSpecimenDelay )

      //grab specimen
      .strafeToLinearHeading( NEAR_THE_OBSERVATION_ZONE, faceDown )
      .waitSeconds( 1 ) //wait for human player
      .strafeToLinearHeading( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE, faceDown )

      //hang specimen
      .strafeToSplineHeading( SPECIMEN_BAR_RIGHT, faceUp )
      .waitSeconds( hangSpecimenDelay )

      //park facing up
      .strafeTo( PARK_IN_OBSERVATION_ZONE )

      .build());
  }
  //============================================================
}