package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;

public class GrabSample extends AbstractAction implements Action
{
  public GrabSample( JoeBot robot )
  {
    super( robot, 1000 );
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      final int currentPosition = robot.extensionArm().getMotorPosition();
      final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_GRAB_SAMPLE.value;

      robot.telemetry().log().add( "GrabSample" );
      robot.intake().pullSampleBack();
      robot.extensionArm().travelTo( extendedPosition );
      super.initialize();
    }

    robot.extensionArm().updateState();
    robot.intake().updateState();

    //stop if it is taking too long
    if( timeExceeded() )
    {
      robot.extensionArm().stop();
      robot.intake().stop();
    }
    //stop extending once we grab the sample
    else if( !robot.intake().isMoving() )
    {
      robot.extensionArm().stop();
    }
    //stop intake if extension arm can no longer extend
    else if( !robot.extensionArm().isMoving() )
    {
      robot.intake().stop();
    }

    return robot.extensionArm().isMoving() &&
           robot.intake().isMoving();
  }
}
