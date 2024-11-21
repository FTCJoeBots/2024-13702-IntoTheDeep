package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;

public class RunIntake extends AbstractAction implements Action
{
  public RunIntake( JoeBot robot, int maxTime )
  {
    super( robot, maxTime );
    this.robot = robot;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.intake().turnOn( -0.2 );
      super.initialize();
    }

    //stop if it is taking too long
    if( timeExceeded() )
    {
      robot.intake().stop();
      return false;
    }

    return true;
  }
}
