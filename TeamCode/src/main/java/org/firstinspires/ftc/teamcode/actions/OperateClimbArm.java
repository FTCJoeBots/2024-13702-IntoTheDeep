package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.ClimbArm;
import org.firstinspires.ftc.teamcode.modules.Intake;

public class OperateClimbArm extends AbstractAction implements Action
{
  private boolean retract;

  public OperateClimbArm( JoeBot robot, boolean retract )
  {
    super( robot, 5000 );
    this.robot = robot;
    this.retract = retract;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      if( retract )
      {
        robot.debug( "OperateClimbArm: retractHooks" );
        robot.climbArm().retractHooks();
      }
      else
      {
        robot.debug( "OperateClimbArm: raiseHooks" );
        robot.climbArm().raiseHooks();
      }

      super.initialize();
    }

    //stop if it is taking too long
    if( timeExceeded() )
    {
      return false;
    }

    return robot.climbArm().isMoving();
  }
}
