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

  public static Vector2d computePosition( Vector2d location )
  {
    double x = -location.y;
    double y = location.x - 61;
    return new Vector2d( x, y );
  }
  
  public static void main(String[] args) {
    MeepMeep meepMeep = new MeepMeep( 800 );

    RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
      // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
      .setConstraints(50, 50, Math.PI, Math.PI, 15)
      .build();

//     basketStrategy( myBot );
    colomaApproach( myBot );
    fastStrafeApproach( myBot );
    splineGrabApproach( myBot );
    splineTackleApproach( myBot );
    splineTackleApproach2( myBot );

    meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_LIGHT)
      .setDarkMode(false)
      .setBackgroundAlpha(0.95f)
      .addEntity(myBot)
      .start();
  }
  //============================================================
  static void basketStrategy( RoadRunnerBotEntity myBot )
  {
    final Vector2d STARTING_POSITION_BASKETS = new Vector2d( 0, 15.1 );
    final Vector2d SPECIMEN_BAR_LEFT = new Vector2d( 25, 7 );
    final Vector2d YELLOW_SAMPLE_1 = new Vector2d( 23.5, 48.7 );
    final Vector2d YELLOW_SAMPLE_2 = new Vector2d( 23.5, 58 );
     final Vector2d YELLOW_SAMPLE_3 = new Vector2d( 23, 58.8 );
     final Vector2d SAMPLE_BASKETS = new Vector2d( 9.8, 52);

    final Vector2d STRAFE_1 = new Vector2d( 50, 50 );
    final Vector2d STRAFE_2 = new Vector2d( 50, 62 );
    final Vector2d STRAFE_3 = new Vector2d( 15, 62 );

    final Vector2d NEAR_ASCENT_ZONE = new Vector2d( 59, 52 );
    final Vector2d ASCENT_ZONE = new Vector2d( 59, 24 );
    final double faceForward = Math.toRadians ( 90 );
    final double faceBasket = Math.toRadians( 225 );
    final double faceRight = Math.toRadians( 0 );
    final double faceSample = Math.toRadians( 135 );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( computePosition( STARTING_POSITION_BASKETS ), faceForward ) )
      //hang specimen
      .strafeToLinearHeading( computePosition( SPECIMEN_BAR_LEFT ), faceForward )
      .waitSeconds( hangSpecimenDelay )

      // grab sample 1
      .strafeToLinearHeading( computePosition( YELLOW_SAMPLE_1 ), faceForward )
      .waitSeconds( grabSpecimenDelay )

      // place sample in a basket
      .strafeToLinearHeading( computePosition( SAMPLE_BASKETS ), faceBasket )
      .waitSeconds( placeSampleDelay )

      // grab sample 2
      .strafeToLinearHeading( computePosition( YELLOW_SAMPLE_2 ), faceForward )
      .waitSeconds( grabSpecimenDelay )

      // place sample in a basket
      .strafeToLinearHeading( computePosition( SAMPLE_BASKETS ), faceBasket )
      .waitSeconds( placeSampleDelay )

      // grab sample 3
//      .strafeToLinearHeading( computePosition( YELLOW_SAMPLE_3 ), faceSample )
//      .waitSeconds( grabSpecimenDelay )
//
//      // place sample in a basket
//      .strafeToLinearHeading( computePosition( SAMPLE_BASKETS ), faceBasket )
//      .waitSeconds( placeSampleDelay )

      //strafe 3
      .strafeToLinearHeading( computePosition( STRAFE_1 ), faceRight )
     .strafeToLinearHeading( computePosition( STRAFE_2 ), faceRight )
      .strafeToLinearHeading( computePosition( STRAFE_3 ), faceRight )
//      .setReversed( true )
//        .splineToLinearHeading( new Pose2d( computePosition( STRAFE_3 ), faceRight ), 1.6 )




      // level one ascent at the end of the autonomous.
      .strafeToLinearHeading( computePosition( NEAR_ASCENT_ZONE ), faceRight )
      .strafeToLinearHeading( computePosition( ASCENT_ZONE ), faceRight )
            .waitSeconds( level1Delay )


        //start animation
      .build());
  }
  //============================================================
  //21.03 seconds: strafe two, hangs two, park
  static void colomaApproach( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 25, -7 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 0, -15.6 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 20, -62.5 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 10, -62.5 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 10, -62.5 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 28, -34 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 50.0, -34 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 51, -45 );
    final Vector2d TEAM_SAMPLE_2 = new Vector2d( 51, -54 );
    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceLeft = Math.toRadians( 180 );

    Vector2d strafePos1 = new Vector2d( STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, TEAM_SAMPLE_1.y );
    Vector2d strafePos2 = new Vector2d( STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, TEAM_SAMPLE_2.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( computePosition( STARTING_POSITION_SPECIMENS ), faceUp ) )

      //hang specimen
      .strafeTo( computePosition( SPECIMEN_BAR_RIGHT ) )
      .waitSeconds( hangSpecimenDelay )

      //strafe in sample
      .strafeToLinearHeading( computePosition( NEAR_TEAM_SAMPLES_1 ), faceLeft )
      .strafeToLinearHeading( computePosition( NEAR_TEAM_SAMPLES_2 ), faceLeft )
      .strafeToLinearHeading( computePosition( TEAM_SAMPLE_1 ), faceLeft )
      .strafeToLinearHeading( computePosition( strafePos1 ), faceLeft )

      //grab specimen
      .strafeToLinearHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .strafeTo( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ) )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( computePosition( SPECIMEN_BAR_RIGHT ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //strafe in sample
      .strafeToLinearHeading( computePosition( NEAR_TEAM_SAMPLES_1 ), faceLeft )
      .strafeToLinearHeading( computePosition( NEAR_TEAM_SAMPLES_2 ), faceLeft )
      .strafeToLinearHeading( computePosition( TEAM_SAMPLE_2 ), faceLeft )
      .strafeToLinearHeading( computePosition( strafePos2 ), faceLeft )

      .build());
  }
  //============================================================
  //26.07 seconds: strafe 1, hang three, park, with 0.5 second wait for human operator
  static void fastStrafeApproach( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 25, -7 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 0, -15.6 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 20, -47 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 11, -47 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 10, -47 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 25, -35 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 54, -35 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 54, -45 );
    final Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 0, -61 );//4.5, -53.8 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceRight = Math.toRadians( 0 );

    Vector2d strafePos1 = new Vector2d( STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, TEAM_SAMPLE_1.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( computePosition( STARTING_POSITION_SPECIMENS ), faceUp ) )

      //hang specimen
      .strafeTo( computePosition( SPECIMEN_BAR_RIGHT ) )
      .waitSeconds( hangSpecimenDelay )

      //strafe in sample
      .strafeToLinearHeading( computePosition( NEAR_TEAM_SAMPLES_1 ), faceRight )
      .strafeToLinearHeading( computePosition( NEAR_TEAM_SAMPLES_2 ), faceRight )
      .strafeToLinearHeading( computePosition( TEAM_SAMPLE_1 ), faceRight )
      .strafeToLinearHeading( computePosition( strafePos1 ), faceRight )

      //grab specimen
      .strafeToLinearHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( 0.5 ) //wait for human operator
      .strafeTo( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ) )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( computePosition( new Vector2d( SPECIMEN_BAR_RIGHT.x,
        SPECIMEN_BAR_RIGHT.y + 6 ) ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //grab specimen
      .strafeToLinearHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( 0.5 ) //wait for human operator
      .strafeToLinearHeading( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( computePosition( new Vector2d( SPECIMEN_BAR_RIGHT.x,
        SPECIMEN_BAR_RIGHT.y + 2*6 ) ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //park
      .strafeTo( computePosition( PARK_IN_OBSERVATION_ZONE ) )

      .build());
  }
  //============================================================
  //25.15 seconds: strafe 1, hang three, park, and 0.5 second wait for human operator
  static void splineGrabApproach( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 25, -7 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 0, -15.6 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 20, -47 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 11, -47 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 10, -47 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 25, -28 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 47.0, -35 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 54, -44 );
    final Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 0, -61 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceRight = Math.toRadians( 0 );

    Vector2d strafePos1 = new Vector2d( STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, TEAM_SAMPLE_1.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( computePosition( STARTING_POSITION_SPECIMENS ), faceUp ) )

      //hang specimen
      .strafeTo( computePosition( SPECIMEN_BAR_RIGHT ) )
      .waitSeconds( hangSpecimenDelay )

      //strafe in sample
      .strafeToConstantHeading( computePosition( NEAR_TEAM_SAMPLES_1 ) )
      .splineToConstantHeading( computePosition( NEAR_TEAM_SAMPLES_2 ), faceUp )
      .splineToSplineHeading( new Pose2d( computePosition( TEAM_SAMPLE_1 ), faceRight ), 1.6 )
      .strafeToLinearHeading( computePosition( strafePos1 ), faceRight )

      //grab specimen
      .strafeToSplineHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( 0.5 ) //wait for human operator
      .strafeTo( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ) )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( computePosition( SPECIMEN_BAR_RIGHT ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //grab specimen
      .strafeToSplineHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( 0.5 ) //wait for human operator
      .strafeToLinearHeading( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( grabSpecimenDelay )

      //hang specimen
      .strafeToLinearHeading( computePosition( SPECIMEN_BAR_RIGHT ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //park
      .strafeTo( computePosition( PARK_IN_OBSERVATION_ZONE ) )

      .build());
  }
  //============================================================
  //25.07 seconds: strafe 2, hang three, park
  //29.19 with park forward and spline heading interpolation when hanging specimens
  static void splineTackleApproach( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 25, -7 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 0, -15.6 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 20, -47 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 11, -47 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 3, -47 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 25, -28 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 47.0, -35 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 54, -44 );
    final Vector2d TEAM_SAMPLE_2 = new Vector2d( 54, -54 );
    final Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 0, -61 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceRight = Math.toRadians( 0 );

    Vector2d strafePos1 = new Vector2d( STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, TEAM_SAMPLE_1.y );
    Vector2d strafePos2 = new Vector2d( STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x - 2, TEAM_SAMPLE_2.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( computePosition( STARTING_POSITION_SPECIMENS ), faceUp ) )

      //hang specimen
      .strafeTo( computePosition( SPECIMEN_BAR_RIGHT ) )
      .waitSeconds( hangSpecimenDelay )

      //strafe in first sample
      .strafeToConstantHeading( computePosition( new Vector2d( NEAR_TEAM_SAMPLES_1.x - 2,
                                                               NEAR_TEAM_SAMPLES_1.y - 0 ) ) )
      .splineToConstantHeading( computePosition( NEAR_TEAM_SAMPLES_2 ), faceUp )
      .splineToSplineHeading( new Pose2d( computePosition( TEAM_SAMPLE_1 ), faceRight ), 1.6 )
      .strafeToLinearHeading( computePosition( strafePos1 ), faceRight )

      //grab specimen
      .strafeToLinearHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( 0.5 ) //wait for human operator
      .strafeTo( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ) )

      //hang specimen
      .strafeToSplineHeading( computePosition( SPECIMEN_BAR_RIGHT ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //grab specimen
      .strafeToLinearHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( 0.5 ) //wait for human operator
      .strafeToLinearHeading( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ), faceDown )

      //hang specimen
//      .strafeToLinearHeading( computePosition( SPECIMEN_BAR_RIGHT ), faceUp )
      .strafeToSplineHeading( computePosition( SPECIMEN_BAR_RIGHT ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //strafe in second sample - takes 5 seconds, if running low we should just park instead?
//      .strafeToConstantHeading( computePosition( new Vector2d( NEAR_TEAM_SAMPLES_1.x,
//        NEAR_TEAM_SAMPLES_1.y - 5.5 ) ) )
      .strafeToLinearHeading( computePosition( new Vector2d( NEAR_TEAM_SAMPLES_1.x - 4,
                                                               NEAR_TEAM_SAMPLES_1.y - 5.5 ) ),
        Math.toRadians( 45 ) )
      .splineToSplineHeading( new Pose2d( computePosition( new Vector2d( NEAR_TEAM_SAMPLES_2.x,
                                                                         NEAR_TEAM_SAMPLES_2.y - 5.5 ) ), faceRight ), 1.6 )
      .splineToConstantHeading( computePosition( TEAM_SAMPLE_2 ), faceRight )
      .strafeToLinearHeading( computePosition( strafePos2 ), faceRight )

      //park facing up
      .turn( faceUp )

      .build());
  }
  //============================================================
  //28.18 seconds: strafe 2, hang three, park
  static void splineTackleApproach2( RoadRunnerBotEntity myBot )
  {
    final Vector2d SPECIMEN_BAR_RIGHT = new Vector2d( 25, -7 );
    final Vector2d STARTING_POSITION_SPECIMENS = new Vector2d( 0, -15.6 );
    final Vector2d NEAR_THE_OBSERVATION_ZONE = new Vector2d( 20, -47 );
    final Vector2d STRAFE_SAMPLE_INTO_OBSERVATION_ZONE = new Vector2d( 11, -47 );
    final Vector2d RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE = new Vector2d( 2.4, -48 );
    final Vector2d NEAR_TEAM_SAMPLES_1 = new Vector2d( 23, -28 );
    final Vector2d NEAR_TEAM_SAMPLES_2 = new Vector2d( 47, -36 );
    final Vector2d TEAM_SAMPLE_1 = new Vector2d( 54, -44 );
    final Vector2d TEAM_SAMPLE_2 = new Vector2d( 54, -54 );
    final Vector2d PARK_IN_OBSERVATION_ZONE = new Vector2d( 5, -50 );

    final double faceUp = Math.toRadians( 90 );
    final double faceDown = Math.toRadians( 270 );
    final double faceRight = Math.toRadians( 0 );

    Vector2d strafePos1 = new Vector2d( STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x + 4, TEAM_SAMPLE_1.y );
    Vector2d strafePos2 = new Vector2d( STRAFE_SAMPLE_INTO_OBSERVATION_ZONE.x, TEAM_SAMPLE_2.y );

    myBot.runAction(myBot.getDrive().actionBuilder(
        new Pose2d( computePosition( STARTING_POSITION_SPECIMENS ), faceUp ) )

      //hang specimen
      .strafeTo( computePosition( SPECIMEN_BAR_RIGHT ) )
      .waitSeconds( hangSpecimenDelay )

      //strafe in first sample
      .strafeToLinearHeading( computePosition( NEAR_TEAM_SAMPLES_1 ), faceUp )
      .splineToConstantHeading( computePosition( NEAR_TEAM_SAMPLES_2 ), faceUp )
      .splineToSplineHeading( new Pose2d( computePosition( TEAM_SAMPLE_1 ), faceRight ), 0 )
      .strafeToLinearHeading( computePosition( strafePos1 ), faceRight )

      //strafe in second sample
      .strafeToLinearHeading( computePosition( new Vector2d( strafePos1.x + 20,
                                                             strafePos1.y + 2 ) ), faceRight )
      .splineToConstantHeading( computePosition( TEAM_SAMPLE_2 ), faceRight )
      .strafeToLinearHeading( computePosition( strafePos2 ), faceRight )

      //grab specimen
      .strafeToLinearHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( 1 ) //wait for human operator
      .strafeTo( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ) )

      //hang specimen
      .strafeToSplineHeading( computePosition( SPECIMEN_BAR_RIGHT ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //grab specimen
      .strafeToLinearHeading( computePosition( NEAR_THE_OBSERVATION_ZONE ), faceDown )
      .waitSeconds( 1 ) //wait for human operator
      .strafeToLinearHeading( computePosition( RETRIEVE_SPECIMEN_IN_OBSERVATION_ZONE ), faceDown )

      //hang specimen
      .strafeToSplineHeading( computePosition( SPECIMEN_BAR_RIGHT ), faceUp )
      .waitSeconds( hangSpecimenDelay )

      //park facing up
      .strafeTo( computePosition( PARK_IN_OBSERVATION_ZONE ) )

      .build());
  }
  //============================================================
}