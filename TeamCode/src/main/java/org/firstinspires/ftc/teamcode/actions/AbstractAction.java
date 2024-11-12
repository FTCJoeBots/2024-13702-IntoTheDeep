package org.firstinspires.ftc.teamcode.actions;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.JoeBot;

@Config
public abstract class AbstractAction
{
  protected JoeBot robot = null;
  private boolean initialized = false;

  //maximum time the action can run for in milliseconds
  public static int defaultMaxTime = 6000;

  private final int maxTime;
  private ElapsedTime time = null;

  public AbstractAction( JoeBot robot )
  {
    this.robot = robot;
    maxTime = defaultMaxTime;
  }

  public AbstractAction( JoeBot robot, int maxTime )
  {
    this.robot = robot;
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
    return time != null &&
           time.milliseconds() >= maxTime;
  }
}
