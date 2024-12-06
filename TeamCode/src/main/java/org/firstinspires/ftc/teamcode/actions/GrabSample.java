package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;

public class GrabSample extends AbstractAction implements Action
{
  public static int defaultMaxTime = 4000;
  private boolean isSpecimen;

  public GrabSample( JoeBot robot, boolean isSpecimen )
  {
    super( robot, isSpecimen ? 2000 : defaultMaxTime );
    this.isSpecimen = isSpecimen;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.updateState( true );
      final int currentPosition = robot.extensionArm().getMotorPosition();
      final int extendedPosition = currentPosition +
        ( isSpecimen ?
        ExtensionArm.Position.EXTEND_TO_GRAB_SPECIMEN.value :
        ExtensionArm.Position.EXTEND_TO_GRAB_SAMPLE.value );

      robot.debug( isSpecimen ? "GrabSpecimen" : "GrabSample" );
      robot.intake().pullSampleBack();
      robot.extensionArm().travelToWithPower( extendedPosition, ExtensionArm.Speed.GRAB_SPECIMEN.value );
      super.initialize();
    }
    else
    {
      robot.updateState();
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
