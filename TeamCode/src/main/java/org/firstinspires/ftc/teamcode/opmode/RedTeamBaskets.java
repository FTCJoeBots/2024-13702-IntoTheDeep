package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous( name = "Red Team Basket", group = "13702" )
public class RedTeamBaskets extends AbstractAutonomousOpMode
{
  public RedTeamBaskets()
  {
    super( Team.RED, GameStrategy.PLACE_SAMPLES_IN_BASKETS );
  }
}
