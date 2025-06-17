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

  private void toggleDashboard()
  {
    dashboardEnabled = !dashboardEnabled;

    if( dashboardEnabled &&
        multipleTelemetry == null )
    {
      dashboard = FtcDashboard.getInstance().getTelemetry();
      multipleTelemetry = new MultipleTelemetry( driverStation, dashboard );
    }
  }

  void addLine( String text )
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

}
