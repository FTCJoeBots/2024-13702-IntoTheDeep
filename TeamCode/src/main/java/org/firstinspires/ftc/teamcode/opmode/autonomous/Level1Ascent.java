package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.enums.Location;
import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Level 1 Ascent", group = "13702" )
public class Level1Ascent extends AbstractAutonomousOpMode
{
  public Level1Ascent()
  {
    super( Team.BLUE, AutonomousState.HAVE_NOTHING );
  }

  @Override
  protected Vector2d defaultPos()
  {
    return Location.STARTING_POSITION_BASKETS;
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
      level1Ascent();
    }
  }
}
