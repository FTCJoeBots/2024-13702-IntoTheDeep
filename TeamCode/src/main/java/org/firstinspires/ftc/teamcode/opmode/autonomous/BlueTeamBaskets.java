package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Baskets", group = "13702" )
@Config
public class BlueTeamBaskets extends AbstractAutonomousOpMode
{
  public static AutonomousState defaultStartState = AutonomousState.HAVE_SAMPLE;

  public BlueTeamBaskets()
  {
    super( Team.BLUE, GameStrategy.PLACE_SAMPLES_IN_BASKETS, defaultStartState );
  }
}
