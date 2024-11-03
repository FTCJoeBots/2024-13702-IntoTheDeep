package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.modules.Intake;

public class OperateIntake extends AbstractAction implements Action
{
  private Intake intake = null;
  private Intake.Direction direction;

  public OperateIntake( Telemetry telemetry, Intake intake, Intake.Direction direction, int maxTime )
  {
    super( telemetry, maxTime );
    this.intake = intake;
    this.direction = direction;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      if( direction == Intake.Direction.PULL  )
      {
        telemetry.log().add( "OperateIntake: pullSampleBack" );
        intake.pullSampleBack();
      }
      else
      {
        telemetry.log().add( "OperateIntake: pushSampleForward" );
        intake.pushSampleForward();
      }

      super.initialize();
    }

    intake.updateState();
    return !timeExceeded() &&
           intake.isMoving();
  }
}
