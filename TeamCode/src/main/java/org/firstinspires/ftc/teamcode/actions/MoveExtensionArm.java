package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.modules.ExtensionArm;

public class MoveExtensionArm implements Action
{
  private ExtensionArm extensionArm = null;
  private ExtensionArm.Position position;

  private boolean initialized = false;

  public MoveExtensionArm( ExtensionArm extensionArm, ExtensionArm.Position position )
  {
    this.extensionArm = extensionArm;
    this.position = position;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !initialized )
    {
      extensionArm.travelTo( position );
      initialized = true;
    }

    return !extensionArm.isMoving();
  }
}
