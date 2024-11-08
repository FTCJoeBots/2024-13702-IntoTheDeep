package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Park", group = "13702" )
public class Park extends AbstractAutonomousOpMode
{
  public Park()
  {
    super( Team.BLUE, GameStrategy.PARK );
  }
}
