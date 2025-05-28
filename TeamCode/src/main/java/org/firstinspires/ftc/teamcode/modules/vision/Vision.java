package org.firstinspires.ftc.teamcode.modules.vision;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;

import java.util.List;

public class Vision extends AbstractModule
{
  private Limelight3A camera;
  private LLStatus status;
  private LLResult result;
  private Sample sample;

  public Vision( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( hardwareMap, telemetry );
    initObjects();
    initState();

    sample = new Sample();
  }

  public void updateState()
  {
    status = camera.getStatus();
    result = camera.getLatestResult();

    Sample latestSample = new Sample( result.getPythonOutput() );

    if( latestSample.color != Color.NOTHING )
    { sample=latestSample;}
    else
    {
      sample.age++;

      if( sample.age >= 60 )
      {
        sample = new Sample();
      }

    }
  }

  public void stop()
  {
    camera.stop();
  }

  //Prints out the extension arm motor position
  @Override
  public void printTelemetry()
  {
    if( sample.color != Color.NOTHING )
    {
      telemetry.addData( "horizontal position ", Math.round( sample.horizontalPosition ) );
      telemetry.addData( "vertical position ", Math.round( sample.verticalPosition ) );
      telemetry.addData( "area", Math.round( sample.area ) );
      telemetry.addData( "color", sample.color );
      telemetry.addData( "age", sample.age );
    }
    else
    {
      telemetry.addLine( "nothing observed!" );
    }

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