package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Blue Team Specimens", group = "13702" )
public class BlueTeamSpecimens extends AbstractAutonomousOpMode
{
  public BlueTeamSpecimens()
  {
    super( Team.BLUE, GameStrategy.HANG_SPECIMENS_ON_BARS );
  }
}
