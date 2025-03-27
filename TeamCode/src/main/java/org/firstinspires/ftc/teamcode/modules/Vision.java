package org.firstinspires.ftc.teamcode.modules;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

public class Vision extends AbstractModule
{
  private Limelight3A camera;
  private LLStatus status;
  private LLResult result;

  public Vision( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();
  }

  public void updateState()
  {
    status = camera.getStatus();
    result = camera.getLatestResult();
  }

  public void stop()
  {
    camera.stop();
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    telemetry.addData("Name", "%s",
      status.getName());
    telemetry.addData("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
      status.getTemp(), status.getCpu(),(int)status.getFps());
    telemetry.addData("Pipeline", "Index: %d, Type: %s",
      status.getPipelineIndex(), status.getPipelineType());

    if (result != null)
    {
      // Access general information
      Pose3D botpose = result.getBotpose();

      if( result.isValid() )
      {
        telemetry.addData( "tx", result.getTx() );
        telemetry.addData( "txnc", result.getTxNC() );
        telemetry.addData( "ty", result.getTy() );
        telemetry.addData( "tync", result.getTyNC() );
        telemetry.addData( "Botpose", botpose.toString() );

        // Access fiducial results
        List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
        for( LLResultTypes.FiducialResult fr : fiducialResults )
        {
          telemetry.addData( "Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees() );
        }

        // Access color results
        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();
        for( LLResultTypes.ColorResult cr : colorResults )
        {
          telemetry.addData( "Color", "X: %.2f, Y: %.2f", cr.getTargetXDegrees(), cr.getTargetYDegrees() );
        }
      }
    }
  }

  private void initObjects()
  {
    camera = hardwareMap.get(Limelight3A.class, "limelight");
  }

  private void initState()
  {
    camera.pipelineSwitch(0);
    camera.start();
  }
}