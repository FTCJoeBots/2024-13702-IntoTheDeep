package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Give Up Sample", group = "13702" )
public class GiveUpSample extends AbstractAutonomousOpMode
{
  public GiveUpSample()
  {
    super( Team.BLUE, GameStrategy.GIVE_UP_SAMPLE, AutonomousState.HAVE_SAMPLE );
  }
}
