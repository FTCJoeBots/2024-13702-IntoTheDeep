package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.actions.MoveExtensionArm;
import org.firstinspires.ftc.teamcode.actions.MoveLift;
import org.firstinspires.ftc.teamcode.actions.OperateIntake;
import org.firstinspires.ftc.teamcode.modules.drive.Drive;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.modules.Lift;

public class JoeBot
{ 
  private ExtensionArm extensionArm = null;
  private Lift lift = null;
  private Intake intake = null;
  private Drive drive = null;

  public JoeBot( HardwareMap hardwareMap, Telemetry telemetry )
  {
    //avoid recreating modules to avoid reseting encoders in between autonomus and tele op
    if( extensionArm == null )
    {
      extensionArm = new ExtensionArm( hardwareMap, telemetry );
      lift = new Lift( hardwareMap, telemetry );
      intake = new Intake( hardwareMap, telemetry );
      drive = new Drive( hardwareMap, telemetry );
    }
  }

  public ExtensionArm extensionArm()
  { return extensionArm; }

  public Lift lift()
  { return lift; }

  public Intake intake()
  { return intake; }

  public Drive drive()
  { return drive; }

  public void stop()
  {
    extensionArm.stop();
    lift.stop();
    intake.stop();
    drive.stop();
  }

  public void placeSampleInBasket( Lift.Position position )
  {
    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( lift, position ),
        new MoveExtensionArm( extensionArm, ExtensionArm.Position.EXREND_TO_DUMP_IN_BASKET ),
        new OperateIntake( intake, Intake.Direction.PUSH ),
        new MoveExtensionArm( extensionArm, ExtensionArm.Position.FULLY_RETRACTED ),
        new MoveLift( lift, Lift.Position.FLOOR )
      )
    );
  }
}
