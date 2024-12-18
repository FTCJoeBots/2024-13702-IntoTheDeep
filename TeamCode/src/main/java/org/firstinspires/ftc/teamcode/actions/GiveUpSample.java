package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;

public class GiveUpSample extends AbstractAction implements Action
{
  public static int defaultMaxTime = 500;
  public GiveUpSample( JoeBot robot )
  {
    super( robot, defaultMaxTime );
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.debug( "GiveUpSample" );
      robot.intake().updateState( true );
      robot.intake().pushSampleForward();
      super.initialize();
    }
    else
    {
      robot.intake().updateState( true );
    }

    //stop if it is taking too long
    if( timeExceeded() )
    {
      return false;
    }

    return robot.intake().isMoving();
  }
}
