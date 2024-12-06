package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Red Team Baskets", group = "13702" )
public class RedTeamBaskets extends AbstractBasketOpMode
{
  public RedTeamBaskets()
  {
    super( Team.RED );
  }
}
