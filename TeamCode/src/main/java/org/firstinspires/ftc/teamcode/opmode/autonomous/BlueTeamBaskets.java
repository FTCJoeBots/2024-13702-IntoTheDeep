package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Blue Team Baskets", group = "13702" )
public class BlueTeamBaskets extends AbstractBasketOpMode
{
  public BlueTeamBaskets()
  {
    super( Team.BLUE );
  }
}
