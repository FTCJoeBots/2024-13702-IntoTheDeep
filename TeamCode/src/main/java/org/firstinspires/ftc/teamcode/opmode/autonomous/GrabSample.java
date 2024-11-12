package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Grab Sample", group = "13702" )
public class GrabSample extends AbstractAutonomousOpMode
{
  public GrabSample()
  {
    super( Team.BLUE, GameStrategy.GRAB_SAMPLE, AutonomousState.HAVE_NOTHING );
  }
}
