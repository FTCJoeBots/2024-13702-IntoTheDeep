package org.firstinspires.ftc.teamcode.actions;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;

public class MoveExtensionArm extends AbstractAction implements Action
{
  private ExtensionArm extensionArm = null;
  private int position;

  public MoveExtensionArm( Telemetry telemetry, ExtensionArm extensionArm, int position )
  {
    super( telemetry, 2000 );
    this.extensionArm = extensionArm;
    this.position = position;
  }

  @Override
  public boolean run( @NonNull TelemetryPacket packet )
  {
    if( !isInitialized() )
    {
      telemetry.log().add( String.format( "MoveExtensionArm: %s", position ) );
      extensionArm.travelTo( position );
      super.initialize();
    }

    extensionArm.updateState();
    return !timeExceeded() &&
           extensionArm.isMoving();
  }
}
