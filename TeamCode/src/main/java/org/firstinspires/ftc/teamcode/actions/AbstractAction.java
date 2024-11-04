package org.firstinspires.ftc.teamcode.actions;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.JoeBot;

public abstract class AbstractAction
{
  protected JoeBot robot = null;
  private boolean initialized = false;

  //maximum time the action can run for in milliseconds
  private int maxTime = 5000;
  private ElapsedTime time = null;

  public AbstractAction( JoeBot robot )
  {
    this.robot = robot;
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
    return maxTime > 0 &&
           time != null &&
           time.milliseconds() >= maxTime;
  }
}
