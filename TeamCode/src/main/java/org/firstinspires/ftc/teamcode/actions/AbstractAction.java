package org.firstinspires.ftc.teamcode.actions;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public abstract class AbstractAction
{
  protected Telemetry telemetry = null;
  private boolean initialized = false;

  //maximum time the action can run for in milliseconds
  private int maxTime = 5000;
  private ElapsedTime time = null;

  public AbstractAction( Telemetry telemetry )
  {
    this.telemetry = telemetry;
  }

  public AbstractAction( Telemetry telemetry, int maxTime )
  {
    this.telemetry = telemetry;
    this.maxTime = maxTime;
    time = new ElapsedTime();
  }

  protected boolean isInitialized()
  { return initialized; }

  protected void initialize()
  {
    if( time != null )
    { time.reset(); }

    initialized = true;
  }

  protected boolean timeExceeded()
  {
    return maxTime > 0 &&
           time != null &&
           time.milliseconds() >= maxTime;
  }
}
