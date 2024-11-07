package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;

public class MoveExtensionArmToClimb extends AbstractAction implements Action
{
  private int position;

  public MoveExtensionArmToClimb( JoeBot robot )
  {
    super( robot, 1000 );
    this.robot = robot;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.telemetry().log().add( "MoveExtensionArmToClimb" );
      robot.extensionArm().climb();
      super.initialize();
    }

    robot.extensionArm().updateState();

    //stop if it is taking too long
    if( timeExceeded() )
    {
      robot.extensionArm().stop();
    }

    return robot.extensionArm().isMoving();
  }
}
