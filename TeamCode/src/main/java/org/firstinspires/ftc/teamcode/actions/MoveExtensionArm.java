package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;

public class MoveExtensionArm extends AbstractAction implements Action
{
  private int position;

  public MoveExtensionArm( JoeBot robot, int position )
  {
    super( robot, 2000 );
    this.robot = robot;
    this.position = position;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.telemetry().log().add( String.format( "MoveExtensionArm: %s", position ) );
      robot.extensionArm().travelTo( position );
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
