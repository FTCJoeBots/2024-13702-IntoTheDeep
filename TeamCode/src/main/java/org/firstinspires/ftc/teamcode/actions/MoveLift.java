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

  private void initialize()
  {
    telemetry.log().add( String.format( "MoveLift: %s", position ) );
    lift.travelTo( position );
    super.intialize();
  }

  //TODO - add logging to file so we can figure out what is going on
  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !initialized )
    {
      initialize();
    }

    telemetry.log().add( String.format( "lift moving?: %s", lift.isMoving() ) );
    telemetry.log().add( String.format( "lift pos: %s", lift.liftPosition() ) );
    telemetry.update();

    lift.updateState();

    return !timeExceeded() &&
           lift.isMoving();
  }
}
