package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.ClimbArm;
import org.firstinspires.ftc.teamcode.modules.Intake;

public class OperateClimbArm extends AbstractAction implements Action
{
  public OperateClimbArm( JoeBot robot )
  {
    super( robot, 5000 );
    this.robot = robot;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.debug( "OperateClimbArm: climbing" );
      robot.climbArm().climb();

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
