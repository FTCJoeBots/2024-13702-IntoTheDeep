package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Level 1 Ascent", group = "13702" )
public class Level1Ascent extends AbstractAutonomousOpMode
{
  public Level1Ascent()
  {
    super( Team.BLUE, GameStrategy.LEVEL_1_ASCENT );
  }
}
