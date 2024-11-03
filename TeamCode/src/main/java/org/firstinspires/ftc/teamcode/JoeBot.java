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

  public void updateState()
  {
    drive.updateState();
    lift.updateState();
    extensionArm.updateState();
    intake.updateState();
  }

  public void placeSampleInBasket( Telemetry telemetry, Basket basket )
  {
    telemetry.log().add( String.format( "placeSampleInBasket: %s", basket ) );

    final int currentPosition = extensionArm.getMotorPosition();
    final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_DUMP_IN_BASKET.value;

    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( telemetry, lift,
                      basket == Basket.HIGH_BASKET ?
                        Lift.Position.HIGH_BASKET :
                        Lift.Position.LOW_BASKET ),
        new MoveExtensionArm( telemetry, extensionArm, extendedPosition ),
        new OperateIntake( telemetry, intake, Intake.Direction.PUSH, 500 ),
        new MoveExtensionArm( telemetry, extensionArm, ExtensionArm.Position.FULLY_RETRACTED.value ),
        new MoveLift( telemetry, lift, Lift.Position.FLOOR )
      )
    );
  }

  public enum Bar
  {
    HIGH_BAR,
    LOW_BAR
  }

  public void hangSpecimen( Telemetry telemetry, Bar bar )
  {
    final int currentPosition = extensionArm.getMotorPosition();
    final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_HANG_SAMPLE.value;

    telemetry.log().add( String.format( "hangSpecimen: %s", bar ) );
    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( telemetry, lift,
          bar == Bar.HIGH_BAR ?
                        Lift.Position.ABOVE_HIGH_SPECIMEN_BAR :
                        Lift.Position.ABOVE_LOW_SPECIMEN_BAR ),
        new MoveExtensionArm( telemetry, extensionArm, extendedPosition ),
        new MoveLift( telemetry, lift,
                      bar == Bar.HIGH_BAR ?
                        Lift.Position.SPECIMEN_CLIPPED_ONTO_HIGH_BAR :
                        Lift.Position.SPECIMEN_CLIPPED_ONTO_LOW_BAR,
                        500 ),
        new MoveExtensionArm( telemetry, extensionArm, ExtensionArm.Position.FULLY_RETRACTED.value ),
        new MoveLift( telemetry, lift, Lift.Position.FLOOR )
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

  public void climb( Telemetry telemetry )
  {
    telemetry.log().add( "climb" );
    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( telemetry, lift, Lift.Position.ABOVE_LOW_HANG_BAR ),
        new MoveExtensionArm( telemetry, extensionArm, ExtensionArm.Position.EXTEND_TO_CLIMB.value ),
        new MoveLift( telemetry, lift, Lift.Position.HANG_FROM_LOW_HANG_BAR )
      )
    );
  }

}
