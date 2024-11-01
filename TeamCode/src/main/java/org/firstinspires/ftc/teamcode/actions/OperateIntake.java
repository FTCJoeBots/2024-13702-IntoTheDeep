package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.modules.Intake;

public class OperateIntake implements Action
{
  private Intake intake = null;
  private Intake.Direction direction;

  private boolean initialized = false;

  public OperateIntake( Intake intake, Intake.Direction direction )
  {
    this.intake = intake;
    this.direction = direction;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !initialized )
    {
      if( direction == Intake.Direction.PULL  )
      {
        intake.pullSampleBack();
      }
      else
      {
        intake.pushSampleForward();
      }

      initialized = true;
    }

    return !intake.isMoving();
  }
}
