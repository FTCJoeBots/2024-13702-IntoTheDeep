package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous( name = "Blue Team Specimens", group = "13702" )
public class BlueTeamSpecimens extends AbstractAutonomousOpMode
{
  public BlueTeamSpecimens()
  {
    super( Team.BLUE, GameStrategy.HANG_SPECIMENS_ON_BARS );
  }
}
