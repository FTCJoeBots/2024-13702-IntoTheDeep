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
    super( robot );
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
      robot.telemetry().log().add( String.format( "MoveLift: %s", position ) );
      robot.lift().travelTo( position );
      super.initialize();
    }

    robot.lift().updateState();

    //stop if it is taking too long
    if( timeExceeded() )
    {
      robot.lift().stop();
    }

    return robot.lift().isMoving();
  }
}
