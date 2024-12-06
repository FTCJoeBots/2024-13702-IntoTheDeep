package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Park", group = "13702" )
public class Park extends AbstractAutonomousOpMode
{
  public Park()
  {
    super( Team.BLUE, AutonomousState.HAVE_NOTHING );
  }

  @Override
  protected Vector2d defaultPos()
  {
    return Location.STARTING_POSITION_SPECIMENS;
  }

  @Override
  protected double minimumTime()
  {
    return 0;
  }

  @Override
  public void loop()
  {
    if( state != AutonomousState.PARKED )
    {
      park();
    }
  }
}
