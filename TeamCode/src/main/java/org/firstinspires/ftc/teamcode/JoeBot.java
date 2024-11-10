package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.actions.GiveUpSample;
import org.firstinspires.ftc.teamcode.actions.MoveExtensionArmToClimb;
import org.firstinspires.ftc.teamcode.actions.MoveLiftToClimb;
import org.firstinspires.ftc.teamcode.actions.GrabSample;
import org.firstinspires.ftc.teamcode.actions.MoveExtensionArm;
import org.firstinspires.ftc.teamcode.actions.MoveLift;
import org.firstinspires.ftc.teamcode.actions.OperateIntake;
import org.firstinspires.ftc.teamcode.modules.AbstractModule;
import org.firstinspires.ftc.teamcode.modules.drive.Drive;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.enums.Bar;
import org.firstinspires.ftc.teamcode.enums.Basket;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

public class JoeBot
{
  private Telemetry telemetry = null;
  private ExtensionArm extensionArm = null;
  private Lift lift = null;
  private Intake intake = null;

  private MecanumDrive mecanumDrive = null;
  private Drive drive = null;

  private static Pose2d pose = new Pose2d( 0, 0, 0 );

  public JoeBot( boolean forAutonomous, HardwareMap hardwareMap, Telemetry telemetry )
  {
    if( !AbstractModule.encodersReset )
    { telemetry.addLine( "Resetting Encoders" ); }

    this.telemetry = telemetry;
    extensionArm = new ExtensionArm( hardwareMap, telemetry );
    lift = new Lift( hardwareMap, telemetry );
    intake = new Intake( hardwareMap, telemetry );

    if( forAutonomous )
    {
      mecanumDrive = new MecanumDrive( hardwareMap, pose );
    }
    else
    {
      drive = new Drive( hardwareMap, telemetry, pose );
    }
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

  public MecanumDrive mecanumDrive()
  { return mecanumDrive; }

  public void coast()
  {
    if( drive != null )
    {
      drive.coast();
    }

    if( mecanumDrive != null )
    {
      mecanumDrive.leftFront.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      mecanumDrive.leftBack.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      mecanumDrive.rightBack.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
      mecanumDrive.rightFront.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.BRAKE );
    }
  }

  public void brake()
  {
    if( drive != null )
    {
      drive.brake();
    }

    if( mecanumDrive != null )
    {
      mecanumDrive.leftFront.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
      mecanumDrive.leftBack.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
      mecanumDrive.rightBack.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
      mecanumDrive.rightFront.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
    }
  }

  public void stop()
  {
    extensionArm.stop();
    lift.stop();
    intake.stop();

    stopDrive();
  }

  public void resetPos( Vector2d position )
  {
    this.pose = new Pose2d( position, 0 );

    if( drive != null )
    { drive.resetPose( pose ); }

    if( mecanumDrive != null )
    { mecanumDrive.pose = pose; }
  }

  public void cachePos()
  {
    if( drive != null )
    {
      pose = drive.getPos();
    }
    else if( mecanumDrive != null )
    {
      pose = mecanumDrive.pose;
    }
  }

  public void updateState()
  {
    if( drive != null )
    {
      drive.updateState();
    }

    lift.updateState();
    extensionArm.updateState();
    intake.updateState();
  }

  public void grabSample( boolean isSpecimen )
  {
    telemetry.log().add( String.format( "Grab Sample Motion: isSpecimen=%s", isSpecimen ) );

    //Prevent robot from continuous it's last wheel velocities (e.g. rotating)
    //while the motion if being performed
    stopDrive();

    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this,
          isSpecimen ?
            Lift.Position.SPECIMEN_FLOOR :
            Lift.Position.SAMPLE_FLOOR ),
        new GrabSample( this ),
        new ParallelAction(
          new MoveLift( this, Lift.Position.TRAVEL_WITH_SPECIMEN ),
          new MoveExtensionArm( this, ExtensionArm.Position.RETRACTED_WITH_SAMPLE.value )
        )
      )
    );
  }

  public void wait( int milliseconds )
  {
    Actions.runBlocking(
      new SequentialAction(
        new SleepAction( milliseconds )
      )
    );
  }

  public void giveUpSample()
  {
    telemetry.log().add( "Give Up Sample" );

    //Prevent robot from continuous it's last wheel velocities (e.g. rotating)
    //while the motion if being performed
    stopDrive();

    Actions.runBlocking( new GiveUpSample( this ) );
  }

  public void placeSampleInBasket( Basket basket )
  {
    telemetry.log().add( String.format( "Place Sample In Basket Motion: %s", basket ) );

    //Prevent robot from continuous it's last wheel velocities (e.g. rotating)
    //while the motion if being performed
    stopDrive();

    final int currentPosition = extensionArm.getMotorPosition();
    final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_DUMP_IN_BASKET.value;

    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this,
                      basket == Basket.HIGH_BASKET ?
                        Lift.Position.HIGH_BASKET :
                        Lift.Position.LOW_BASKET,
          8000 ),
        new MoveExtensionArm( this, extendedPosition ),
        new OperateIntake( this, Intake.Direction.PUSH, 500 ),
        new MoveExtensionArm( this, ExtensionArm.Position.FULLY_RETRACTED.value ),
        new MoveLift( this, Lift.Position.FLOOR, 0 )
      )
    );
  }

  private void stopDrive()
  {
    if( drive != null )
    { drive.stop(); }
  }

  public void hangSpecimen( Bar bar )
  {
    telemetry.log().add( String.format( "Hang Specimen Motion: %s", bar ) );

    //Prevent robot from continuous it's last wheel velocities (e.g. rotating)
    //while the motion if being performed
    stopDrive();

    final int currentPosition = extensionArm.getMotorPosition();
    final int extendedPosition = currentPosition + ExtensionArm.Position.EXTEND_TO_HANG_SAMPLE.value;

    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this,
          bar == Bar.HIGH_BAR ?
                        Lift.Position.ABOVE_HIGH_SPECIMEN_BAR :
                        Lift.Position.ABOVE_LOW_SPECIMEN_BAR,
          6000 ),
        new MoveExtensionArm( this, extendedPosition ),
        new MoveLift( this,
                      bar == Bar.HIGH_BAR ?
                        Lift.Position.SPECIMEN_CLIPPED_ONTO_HIGH_BAR :
                        Lift.Position.SPECIMEN_CLIPPED_ONTO_LOW_BAR,
                        1000 ),
        new MoveExtensionArm( this, ExtensionArm.Position.FULLY_RETRACTED.value ),
        new MoveLift( this, Lift.Position.FLOOR, 0 )
      )
    );
  }

  //TODO - once lift is moved set drive wheels to coast and FULLY exten the arm.
  //stop that action once the velocity of hte extension arm drops below a certain amount.
  //this will prevent gear mash but allow this to work from farther away and allow us to extend arm quickly!
  public void levelOneAscent()
  {
    telemetry.log().add( "Level One Ascent:" );

    //Prevent robot from continuous it's last wheel velocities (e.g. rotating)
    //while the motion if being performed
    stopDrive();

    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this, Lift.Position.AT_LOW_HANG_BAR ),
        new MoveExtensionArm( this, ExtensionArm.Position.EXTEND_TO_TOUCH_BAR.value )
      )
    );
  }

  public void levelTwoAscent()
  {
    //Turning off for now as the robot gets stuck and the red line on the left lift motor can break
    /*
    telemetry.log().add( "Level Two Ascent:" );

    //Prevent robot from continuous it's last wheel velocities (e.g. rotating)
    //while the motion if being performed
    stopDrive();

    Actions.runBlocking(
      new SequentialAction(
        new MoveLift( this, Lift.Position.ABOVE_LOW_HANG_BAR ),
        new MoveExtensionArm( this, ExtensionArm.Position.EXTEND_TO_CLIMB.value ),
        new MoveLift( this, Lift.Position.TOUCHING_LOW_HANG_BAR, 1000 ),
        new MoveExtensionArmToClimb( this ),
        new MoveLiftToClimb( this )
      )
    );
    */
  }

}
