package org.firstinspires.ftc.teamcode.opmode.autonomous;

import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.enums.Team;

@Autonomous( name = "Grab Sample", group = "13702" )
@Disabled
public class GrabSample extends AbstractAutonomousOpMode
{
  public GrabSample()
  {
    super( Team.BLUE, AutonomousState.HAVE_NOTHING );
  }

  @Override
  protected Vector2d defaultPos()
  {
    return new Vector2d( 0, 0 );
  }

  @Override
  protected double minimumTime()
  {
    return 0;
  }

  @Override
  public void loop()
  {
    robot.intake().updateState( true );
    if( !robot.intake().hasSample() )
    {
      robot.grabSample( false );
    }
  }
}
