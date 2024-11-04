package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.actions.GrabSample;
import org.firstinspires.ftc.teamcode.actions.MoveExtensionArm;
import org.firstinspires.ftc.teamcode.actions.MoveLift;
import org.firstinspires.ftc.teamcode.actions.OperateIntake;
import org.firstinspires.ftc.teamcode.modules.drive.Drive;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.modules.Lift;

public class JoeBot
{
  private Telemetry telemetry = null;
  private ExtensionArm extensionArm = null;
  private Lift lift = null;
  private Intake intake = null;
  private Drive drive = null;

  public JoeBot( HardwareMap hardwareMap, Telemetry telemetry )
  {
    this.telemetry = telemetry;
    extensionArm = new ExtensionArm( hardwareMap, telemetry );
    lift = new Lift( hardwareMap, telemetry );
    intake = new Intake( hardwareMap, telemetry );
    drive = new Drive( hardwareMap, telemetry );
  }

  public Telemetry telemetry()
  { return telemetry; }

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

  public void updateState()
  {
    drive.updateState();
    lift.updateState();
    extensionArm.updateState();
    intake.updateState();
  }

  public void grabSample( boolean isSpecimen )
  {
    telemetry.log().add( String.format( "grabSample isSpecimen=%s", isSpecimen ) );

    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this,
          isSpecimen ?
            Lift.Position.SPECIMEN_FLOOR :
            Lift.Position.SAMPLE_FLOOR ),
        new GrabSample( this ),
        new MoveExtensionArm( this, ExtensionArm.Position.RETRACTED_WITH_SAMPLE.value )
      )
    );
  }

  public enum Basket
  {
    HIGH_BASKET,
    LOW_BASKET
  }

  public void placeSampleInBasket( Basket basket )
  {
    telemetry.log().add( String.format( "placeSampleInBasket: %s", basket ) );

    final int currentPosition = extensionArm.getMotorPosition();
    final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_DUMP_IN_BASKET.value;

    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this,
                      basket == Basket.HIGH_BASKET ?
                        Lift.Position.HIGH_BASKET :
                        Lift.Position.LOW_BASKET ),
        new MoveExtensionArm( this, extendedPosition ),
        new OperateIntake( this, Intake.Direction.PUSH, 500 ),
        new MoveExtensionArm( this, ExtensionArm.Position.FULLY_RETRACTED.value ),
        new MoveLift( this, Lift.Position.FLOOR )
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
    final int currentPosition = extensionArm.getMotorPosition();
    final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_HANG_SAMPLE.value;

    telemetry.log().add( String.format( "hangSpecimen: %s", bar ) );
    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this,
          bar == Bar.HIGH_BAR ?
                        Lift.Position.ABOVE_HIGH_SPECIMEN_BAR :
                        Lift.Position.ABOVE_LOW_SPECIMEN_BAR ),
        new MoveExtensionArm( this, extendedPosition ),
        new MoveLift( this,
                      bar == Bar.HIGH_BAR ?
                        Lift.Position.SPECIMEN_CLIPPED_ONTO_HIGH_BAR :
                        Lift.Position.SPECIMEN_CLIPPED_ONTO_LOW_BAR,
                        500 ),
        new MoveExtensionArm( this, ExtensionArm.Position.FULLY_RETRACTED.value ),
        new MoveLift( this, Lift.Position.FLOOR )
      )
    );
  }

  public void climb()
  {
    telemetry.log().add( "climb" );
    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this, Lift.Position.ABOVE_LOW_HANG_BAR ),
        new MoveExtensionArm( this, ExtensionArm.Position.EXTEND_TO_CLIMB.value ),
        new MoveLift( this, Lift.Position.HANG_FROM_LOW_HANG_BAR )
      )
    );
  }

}
