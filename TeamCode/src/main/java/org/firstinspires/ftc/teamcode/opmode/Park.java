package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Autonomous( name = "Park", group = "13702" )
public class Park extends AbstractAutonomousOpMode
{
  public Park()
  {
    super( Team.BLUE, GameStrategy.PARK );
  }
}
