package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous( name = "Blue Team Baskets", group = "13702" )
public class BlueTeamBaskets extends AbstractAutonomousOpMode
{
  public BlueTeamBaskets()
  {
    super( Team.BLUE, GameStrategy.PLACE_SAMPLES_IN_BASKETS );
  }
}
