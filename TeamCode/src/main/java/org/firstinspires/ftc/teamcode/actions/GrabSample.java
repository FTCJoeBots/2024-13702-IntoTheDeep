package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;

@Config
public class GrabSample extends AbstractAction implements Action
{
  public static int defaultMaxTime = 4000;

  public GrabSample( JoeBot robot )
  {
    super( robot, defaultMaxTime );
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.updateState();
      final int currentPosition = robot.extensionArm().getMotorPosition();
      final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_GRAB_SAMPLE.value;

      robot.debug( "GrabSample" );
      robot.intake().pullSampleBack();
      robot.extensionArm().travelToWithPower( extendedPosition, 0.5 );
      super.initialize();
    }
    else
    {
      robot.extensionArm().updateState();
      robot.intake().updateState();
    }

    //stop if it is taking too long
    if( timeExceeded() )
    {
      robot.debug( "GrabSample:timeExceeded - stopping arm and intake" );
      robot.extensionArm().stop();
      robot.intake().stop();
    }
    //stop extending once we grab the sample
    else if( !robot.intake().isMoving() &&
             robot.extensionArm().isMoving() )
    {
      robot.debug( "GrabSample:intake not moving -> stop extending" );
      robot.extensionArm().stop();
    }
    //stop intake if extension arm can no longer extend
    else if( !robot.extensionArm().isMoving() &&
             robot.intake().isMoving() )
    {
      robot.debug( "GrabSample:arm extended -> stop intake" );
      robot.intake().stop();
    }

    if( robot.extensionArm().isMoving() &&
        robot.intake().isMoving() )
    {
      return true;
    }
    else
    {
      robot.debug( "GrabSample: we're done" );
      return false;
    }
  }
}
