package org.firstinspires.ftc.teamcode;


import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Reporter
{
  private Telemetry driverStation       = null;
  private Telemetry dashboard           = null;
  private Telemetry multipleTelemetry   = null;
  private boolean   dashboardEnabled    = false;

  public Reporter ( Telemetry driverStation )
  {
    this.driverStation = driverStation;
  }

  public void toggleDashboard()
  {
    dashboardEnabled = !dashboardEnabled;

    if( dashboardEnabled &&
        multipleTelemetry == null )
    {
      dashboard = FtcDashboard.getInstance().getTelemetry();
      multipleTelemetry = new MultipleTelemetry( driverStation, dashboard );
    }
  }

  public void addLine( String text )
  {
    if( dashboardEnabled )
    {
      multipleTelemetry.addLine( text );
    }
    else
    {
      driverStation.addLine( text );
    }

  }

  public Telemetry.Log log() {
    return driverStation.log();
  }

  public void update() {
    if (dashboardEnabled){
      multipleTelemetry.update();
    } else {
      driverStation.update();
    }
  }

  public void addData(String caption, String format, Object... args) {
    if (dashboardEnabled){
      multipleTelemetry.addData(caption, format, args);
    } else {
      driverStation.addData(caption, format, args);
    }
  }

  public void addData(String caption, Object value) {
    if (dashboardEnabled){
      multipleTelemetry.addData(caption, value);
    } else {
      driverStation.addData(caption, value);
    }
  }


  public void clearAll() {
    if (dashboardEnabled){
      multipleTelemetry.clearAll();
    } else {
      driverStation.clearAll();
    }
  }
}
