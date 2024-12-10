package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.Lift;

public class MoveLift extends AbstractAction implements Action
{
  private Lift.Position position;

  public MoveLift( JoeBot robot, Lift.Position position )
  {
    super( robot, 3000 );
    this.robot = robot;
    this.position = position;
  }

  public MoveLift( JoeBot robot,
                   Lift.Position position,
                   int maxTime )
  {
    super( robot, maxTime );
    this.robot = robot;
    this.position = position;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.debug( String.format( "MoveLift: %s", position ) );
      robot.updateState();
      robot.lift().travelTo( position );
      super.initialize();
    }
    else
    {
      robot.lift().updateState();
    }

    //let the action complete if the maximum time has been exceeded
    //that is ok, we sometimes want to let te lift continue to drift down while we are driving
    if( timeExceeded() )
    { return false; }

    return robot.lift().isMoving();
  }
}
