package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;

@Config
public class LiftStuckArm extends AbstractAction implements Action
{
  private final int desiredArmPosition;
  private final int raisedLiftPosition;

  public static int defaultMaxTime = 2000;

  public LiftStuckArm( JoeBot robot, int desiredArmPosition, int raisedLiftPosition )
  {
    super( robot, defaultMaxTime );
    this.robot = robot;
    this.desiredArmPosition = desiredArmPosition;
    this.raisedLiftPosition = raisedLiftPosition;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      robot.updateState();
      robot.debug( String.format( "LiftStuckArm: %s", desiredArmPosition ) );
      super.initialize();

      //raise the lift to unstick the arm if it cannot restract to the desired position
      if( robot.extensionArm().getMotorPosition() > desiredArmPosition + 1 )
      {
        robot.lift().travelTo( raisedLiftPosition );
        robot.extensionArm().travelTo( desiredArmPosition );
      }
      //arm is not stuck
      else
      {
        return false;
      }
    }
    else
    {
      robot.updateState();
    }

    //stop if it is taking too long
    if( timeExceeded() )
    {
      return false;
    }

    return robot.lift().isMoving() ||
           robot.extensionArm().isMoving();
  }
}
