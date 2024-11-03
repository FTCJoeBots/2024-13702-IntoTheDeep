package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.modules.Lift;

public class MoveLift extends AbstractAction implements Action
{
  private Lift lift = null;
  private Lift.Position position;

  public MoveLift( Telemetry telemetry, Lift lift, Lift.Position position )
  {
    super( telemetry );
    this.lift = lift;
    this.position = position;
  }

  public MoveLift( Telemetry telemetry,
                   Lift lift,
                   Lift.Position position,
                   int maxTime )
  {
    super( telemetry, maxTime );
    this.lift = lift;
    this.position = position;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      telemetry.log().add( String.format( "MoveLift: %s", position ) );
      lift.travelTo( position );
      super.initialize();
    }

    lift.updateState();
    return !timeExceeded() &&
           lift.isMoving();
  }
}
