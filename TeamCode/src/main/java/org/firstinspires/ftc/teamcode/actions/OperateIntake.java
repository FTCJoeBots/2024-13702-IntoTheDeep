package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.Intake;

public class OperateIntake extends AbstractAction implements Action
{
  private Intake.Direction direction;

  public OperateIntake( JoeBot robot, Intake.Direction direction, int maxTime )
  {
    super( robot, maxTime );
    this.robot = robot;
    this.direction = direction;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.updateState( true );
      if( direction == Intake.Direction.PULL  )
      {
        robot.debug( "OperateIntake: pullSampleBack" );
        robot.intake().pullSampleBack();
      }
      else
      {
        robot.debug( "OperateIntake: pushSampleForward" );
        robot.intake().pushSampleForward();
      }

      super.initialize();
    }
    else
    {
      robot.intake().updateState( true );
    }

    //stop if it is taking too long
    if( timeExceeded() )
    {
      return false;
    }

    return robot.intake().isMoving();
  }
}
