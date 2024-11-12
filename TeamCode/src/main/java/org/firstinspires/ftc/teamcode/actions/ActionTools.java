package org.firstinspires.ftc.teamcode.actions;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.canvas.Canvas;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.JoeBot;

public class ActionTools
{
  public static void runBlocking( JoeBot robot, Action action )
  {
//    FtcDashboard dash = FtcDashboard.getInstance();
//    Canvas previewCanvas = new Canvas();
//    action.preview(previewCanvas);

    boolean running = true;
    while( running &&
           !Thread.currentThread().isInterrupted() )
    {
      TelemetryPacket packet = new TelemetryPacket();
//      packet.fieldOverlay().getOperations().addAll(previewCanvas.getOperations());

      robot.clearBulkCache();
      running = action.run(packet);

//      dash.sendTelemetryPacket(packet);
    }
  }
}
