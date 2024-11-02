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
    //avoid recreating modules to avoid resetting encoders in between autonomous and tele op
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

  public enum Basket
  {
    HIGH_BASKET,
    LOW_BASKET
  }

  public void placeSampleInBasket( Basket basket )
  {
    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( lift,
                      basket == Basket.HIGH_BASKET ?
                        Lift.Position.HIGH_BASKET :
                        Lift.Position.LOW_BASKET ),
        new MoveExtensionArm( extensionArm, ExtensionArm.Position.EXTEND_TO_DUMP_IN_BASKET ),
        new OperateIntake( intake, Intake.Direction.PUSH ),
        new MoveExtensionArm( extensionArm, ExtensionArm.Position.FULLY_RETRACTED ),
        new MoveLift( lift, Lift.Position.FLOOR )
      )
    );
  }

  public enum Bar
  {
    HIGH_BAR,
    LOW_BAR
  }

  public void hangSpecimen( Bar bar )
  {
    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( lift,
          bar == Bar.HIGH_BAR ?
                        Lift.Position.ABOVE_HIGH_SPECIMEN_BAR :
                        Lift.Position.ABOVE_LOW_SPECIMEN_BAR ),
        new MoveExtensionArm( extensionArm, ExtensionArm.Position.EXTEND_TO_HANG_SAMPLE ),
        new MoveLift( lift,
                      bar == Bar.LOW_BAR ?
                        Lift.Position.HANG_FROM_HIGH_HANG_BAR :
                        Lift.Position.HANG_FROM_LOW_HANG_BAR ),
        new MoveExtensionArm( extensionArm, ExtensionArm.Position.FULLY_RETRACTED )
      )
    );
  }

  /*
  public void grabSample( boolean isSpecimen )
  {
    Actions.runBlocking(
      new SequentialAction(
        //TODO
      )
    );
  }
  */

  public void climb()
  {
    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( lift, Lift.Position.ABOVE_LOW_HANG_BAR ),
        new MoveExtensionArm( extensionArm, ExtensionArm.Position.EXTEND_TO_CLIMB ),
        new MoveLift( lift, Lift.Position.CLIMB )
      )
    );
  }

}
