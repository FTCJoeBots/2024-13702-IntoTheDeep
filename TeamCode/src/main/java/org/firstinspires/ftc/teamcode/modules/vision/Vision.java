package org.firstinspires.ftc.teamcode.modules.vision;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.Reporter;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.List;

public class Vision extends AbstractModule
{
  private Limelight3A camera;
  private LLStatus status;
  private LLResult result;
  private Sample sample;
  private boolean dashboardOn = false;

  LimeLightImageTools tools;

  private int nextSnapshot = 1;

  public Vision( HardwareMap hardwareMap, Reporter reporter )
  {
    super( hardwareMap, reporter );
    initObjects();
    initState();

    sample = new Sample();
  }

  public void takeSnapshot()
  {
    camera.captureSnapshot( String.format( "Capture %d", nextSnapshot ) );
    nextSnapshot++;
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
      reporter.addData( "horizontal position ", Math.round( sample.horizontalPosition ) );
      reporter.addData( "vertical position ", Math.round( sample.verticalPosition ) );
      reporter.addData( "area", Math.round( sample.area ) );
      reporter.addData( "color", sample.color );
      reporter.addData( "age", sample.age );
    }
    else
    {
      reporter.addLine( "nothing observed!" );
    }

    if (result != null)
    {
      // Access general information
      Pose3D botpose = result.getBotpose();

      if( result.isValid() )
      {
        reporter.addData( "tx", result.getTx() );
        reporter.addData( "txnc", result.getTxNC() );
        reporter.addData( "ty", result.getTy() );
        reporter.addData( "tync", result.getTyNC() );
        reporter.addData( "Botpose", botpose.toString() );

        // Access fiducial results
        List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
        for( LLResultTypes.FiducialResult fr : fiducialResults )
        {
          reporter.addData( "Fiducial", "ID: %d, Family: %s, X: %.2f, Y: %.2f", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees() );
        }

        // Access color results
        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();
        for( LLResultTypes.ColorResult cr : colorResults )
        {
          reporter.addData( "Color", "X: %.2f, Y: %.2f", cr.getTargetXDegrees(), cr.getTargetYDegrees() );
        }
      }
    }
  }
  public void toggleDashboard () {
    dashboardOn = !dashboardOn;
    if (dashboardOn){
      tools.forwardAll();
      FtcDashboard.getInstance().startCameraStream( tools.getStreamSource(), 10 );
    } else {
      FtcDashboard.getInstance().stopCameraStream();
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
    camera.deleteSnapshots();

    double[] cameraData = new double[ 3 ];
    cameraData[0] = 1; //red yes
    cameraData[1] = 0; //blue no
    cameraData[2] = 1; //yellow yes
    camera.updatePythonInputs( cameraData );
    
    tools = new LimeLightImageTools( camera );
    tools.setDriverStationStreamSource();

    //TODO - only when dashboard is toggled on,..

  }
}