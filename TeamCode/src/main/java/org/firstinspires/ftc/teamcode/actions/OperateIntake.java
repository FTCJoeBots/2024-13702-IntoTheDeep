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
      if( direction == Intake.Direction.PULL  )
      {
        robot.telemetry().log().add( "OperateIntake: pullSampleBack" );
        robot.intake().pullSampleBack();
      }
      else
      {
        robot.telemetry().log().add( "OperateIntake: pushSampleForward" );
        robot.intake().pushSampleForward();
      }

      super.initialize();
    }

    robot.intake().updateState();

    //stop if it is taking too long
    if( timeExceeded() )
    {
      robot.intake().stop();
    }

    return robot.intake().isMoving();
  }
}
