package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;

public class MoveLiftToClimb extends AbstractAction implements Action
{
  public MoveLiftToClimb( JoeBot robot )
  {
    super( robot, 3000 );
    this.robot = robot;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.updateState();
      robot.debug( "MoveLiftToClimb" );
      robot.lift().climb();
      super.initialize();
    }
    else
    {
      robot.lift().updateState();
    }
    //stop if it is taking too long
    if( timeExceeded() ||
        !robot.lift().isMoving() )
    {
      robot.lift().floatMotors();
      return false;
    }

    return true;
  }
}
