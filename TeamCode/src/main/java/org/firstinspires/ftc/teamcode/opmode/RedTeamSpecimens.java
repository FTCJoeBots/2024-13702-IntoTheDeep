package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous( name = "Red Team Specimens", group = "13702" )
public class RedTeamSpecimens extends AbstractAutonomousOpMode
{
  public RedTeamSpecimens()
  {
    super( Team.RED, GameStrategy.HANG_SPECIMENS_ON_BARS );
  }
}
