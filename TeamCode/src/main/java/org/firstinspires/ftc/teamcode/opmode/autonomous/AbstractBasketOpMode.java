package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

import org.firstinspires.ftc.teamcode.enums.Bar;
import org.firstinspires.ftc.teamcode.enums.Basket;
import org.firstinspires.ftc.teamcode.enums.Button;
import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.enums.Participant;
import org.firstinspires.ftc.teamcode.enums.Team;
import org.firstinspires.ftc.teamcode.modules.Lift;

public abstract class AbstractBasketOpMode extends AbstractAutonomousOpMode
{
  protected AbstractBasketOpMode( Team team )
  {
    super( team, AutonomousState.HAVE_SPECIMEN );
  }

  @Override
  protected Vector2d defaultPos()
  {
    return Location.STARTING_POSITION_BASKETS;
  }

  @Override
  protected double minimumTime()
  {
    return 2;
  }

  @Override
  public void init_loop()
  {
    if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_LEFT )   ||
        gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.DPAD_RIGHT ) ||
        gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.GUIDE ) )
    {
      final AutonomousState[] states = AutonomousState.values();
      state = states[ state.ordinal() < states.length - 1 ?
        state.ordinal() + 1 :
        0 ];
    }

    telemetry.addLine( String.format( "> Initial State: %s", state ) );
    telemetry.update();
    gamepads.storeLastButtons();
  }

  @Override
  public void loop()
  {
    if( state == AutonomousState.PARKED )
    { return; }

    if( timeRunningOut() )
    {
      robot.debug( "BasketAuto:timeRunningOut!" );
      level1Ascent();
    }
    else if( state == AutonomousState.HAVE_SPECIMEN )
    {
      robot.debug( "BasketAuto:HAVE_SPECIMEN -> hangSpecimen" );
      final double faceForward = 0;
      robot.lift().travelTo( Lift.Position.ABOVE_HIGH_SPECIMEN_BAR );
      driveTo( new Pose2d( Location.SPECIMEN_BAR_LEFT, faceForward ) );

      if( enableLiftMotions )
      {
        robot.hangSpecimen( Bar.HIGH_BAR );
      }
      else
      {
        robot.giveUpSample();
      }

      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_SAMPLE )
    {
      robot.debug( "BasketAuto:HAVE_SAMPLE -> placeSampleInBasket" );
      final double faceBasket = Math.toRadians( 135 );
      robot.lift().travelTo( Lift.Position.HIGH_BASKET );
      driveTo( new Pose2d( Location.SAMPLE_BASKETS, faceBasket ) );

      if( enableLiftMotions )
      {
        robot.placeSampleInBasket( Basket.HIGH_BASKET );
      }
      else
      {
        robot.giveUpSample();
      }

      state = AutonomousState.HAVE_NOTHING;
    }
    else if( state == AutonomousState.HAVE_NOTHING )
    {
      if( neutralSamples == 1 )
      {
        robot.debug( String.format( "BasketAuto:HAVE_NOTHING -> neutralSamplesLeft %s", neutralSamples ) );
        level1Ascent();
      }
      else
      {
        //start moving lift
        robot.lift().travelTo( Lift.Position.SAMPLE_FLOOR );

        if( neutralSamples == 3 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo1" );
          final double faceForward = 0;
          driveTo( new Pose2d( Location.YELLOW_SAMPLE_1, faceForward ) );
        }
        else if( neutralSamples == 2 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo2" );
          final double faceForward = 0;
          driveTo( new Pose2d( Location.YELLOW_SAMPLE_2, faceForward ) );
        }
        else if( neutralSamples == 1 )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> driveTo3" );
          final double faceSample = Math.toRadians( 45 );
          driveTo( new Pose2d( Location.YELLOW_SAMPLE_3, faceSample ) );
        }

        if( timeRunningOut() )
        {
          robot.debug( "BasketAuto:timeRunningOut!" );
          level1Ascent();
          return;
        }

        robot.debug( "BasketAuto:HAVE_NOTHING -> grabSample" );
        robot.grabSample( false );
        neutralSamples--;

        robot.intake().updateState( true );
        if( robot.intake().hasSample() )
        {
          robot.debug( "BasketAuto:HAVE_NOTHING -> HAVE_SAMPLE" );
          state = AutonomousState.HAVE_SAMPLE;
        }
      }
    }
  }
}
