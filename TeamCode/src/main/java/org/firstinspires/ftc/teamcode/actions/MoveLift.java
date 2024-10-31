package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.modules.Lift;

public class MoveLift implements Action
{
  private Lift lift = null;
  private Lift.Position position;

  private boolean initialized = false;

  public MoveLift( Lift lift, Lift.Position position )
  {
    this.lift = lift;
    this.position = position;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !initialized )
    {
      lift.travelTo( position );
      initialized = true;
    }

    return !lift.isMoving();
  }
}
