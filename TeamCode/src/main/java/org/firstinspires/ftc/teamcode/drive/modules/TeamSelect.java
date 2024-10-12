package org.firstinspires.ftc.teamcode.drive.modules;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.SwitchableLight;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class TeamSelect extends AbstractModule
{
  //Preset positions we can extend the arm to
  public enum Team { RED, BLUE }

  private Team team = Team.RED;

  private boolean wasPressed = false;

  private TouchSensor teamSelectButton = null;
  private SwitchableLight redTeamIndicator = null;
  private SwitchableLight blueTeamIndicator = null;

  public TeamSelect( HardwareMap hardwareMap, Telemetry telemetry )
  {
    super( telemetry );
    initObjects( hardwareMap );
    initState();
  }

  public void pollButton()
  {
    boolean isPressed = teamSelectButton.isPressed();

    if( isPressed && !wasPressed )
    { toggleTeam(); }

    wasPressed = isPressed;
  }

  //Stops the extension arm motor
  public void stop()
  {}

  //Prints out the extension arm motor position
  public void printTelemetry()
  {
    final String color = team == Team.RED ? "Red" : "Blue";
    telemetry.addLine( String.format( "Team - %s", color ) );
  }

  private void initObjects( HardwareMap hardwareMap )
  {
    teamSelectButton = hardwareMap.get( TouchSensor.class, "teamSelectButton" );
    redTeamIndicator = hardwareMap.get( SwitchableLight.class, "blueTeamIndicator" );
    blueTeamIndicator = hardwareMap.get( SwitchableLight.class, "blueTeamIndicator" );
  }

  private void initState()
  {
    updateTeamIndicator();
  }

  private void toggleTeam()
  {
    team = team == Team.RED ?
      Team.BLUE :
      Team.RED;

    updateTeamIndicator();
  }

  private void updateTeamIndicator()
  {
    redTeamIndicator.enableLight( team == Team.RED );
    blueTeamIndicator.enableLight( team == Team.BLUE );
  }
}
