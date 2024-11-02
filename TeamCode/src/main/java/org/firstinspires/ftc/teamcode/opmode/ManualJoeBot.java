package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.modules.drive.PresetDirection;
import org.firstinspires.ftc.teamcode.modules.drive.RotateDirection;
import org.firstinspires.ftc.teamcode.modules.Lift;

import java.util.EnumSet;
import java.util.List;

//Tell framework that this is a TeleOp mode
@TeleOp( name = "Manual Joe Bot", group = "Iterative Opmode" )
public class ManualJoeBot extends OpMode
{
  private enum Module
  {
    DRIVE, INTAKE, LIFT, EXTENSION_ARM
  }

  private ElapsedTime time = null;
  private Module currentModule = Module.DRIVE;
  private List<LynxModule> hubs;
  private JoeBot robot = null;
  private Gamepads gamepads = null;

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    time = new ElapsedTime();
    currentModule = Module.DRIVE;

    //setup bulk reads
    hubs = hardwareMap.getAll( LynxModule.class );
    for( LynxModule module : hubs )
    {
      module.setBulkCachingMode( LynxModule.BulkCachingMode.MANUAL );
    }

    robot = new JoeBot( hardwareMap, telemetry );
    gamepads = new Gamepads( gamepad1, gamepad2 );

    telemetry.addLine( "Initialized Manual" );
    telemetry.update();
  }

  @Override
  public void init_loop()
  {
    //Allow robot to be pushed around before the start button is pressed
    robot.drive().coast();
  }

  @Override
  public void start()
  {
    //Prevent robot from being pushed around
    robot.drive().brake();
  }

  private void addMessage( String message)
  {
    telemetry.log().add( message );
  }

  @Override
  public void loop()
  {
    //Clear the BulkCache once per control cycle
    for( LynxModule module : hubs )
    {
      module.clearBulkCache();
    }

    robot.drive().updateLocation();

    //==================
    //Extension Arm
    //==================
    robot.extensionArm().updateState();

    //Fully extend - B + Y
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.Y ) ) &&
        !robot.lift().isHigh() )
    {
      robot.extensionArm().fullyExtend();
      addMessage( "Fully extend arm" );
    }
    //Manually extend - Y
    else if( gamepad2.y )
//    else if( gamepads.buttonDown( Participant.OPERATOR, Button.Y ) )
    {
      boolean liftIsHigh = robot.lift().isHigh();
      if( robot.extensionArm().manuallyExtend( liftIsHigh ) )
      { addMessage( "Manually extend arm" ); }
    }

    //Full retract - B + A
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.A ) ) )
    {
      robot.extensionArm().fullyRetract();
      addMessage( "Fully retract arm" );
    }
    //Manually retract - A
    else if( gamepad2.a )
//    else if( gamepads.buttonDown( Participant.OPERATOR, Button.A ) )
    {
      if( robot.extensionArm().manuallyRetract() )
      { addMessage( "Manually retract arm" ); }
    }

    //==================
    //Lift
    //==================
    robot.lift().updateState();

    //High basket - x + dpad_up
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_UP, Button.X ) ) )
    {
      if( robot.intake().getObservedObject() != Intake.ObservedObject.NOTHING )
      {
        robot.placeSampleInBasket( JoeBot.Basket.HIGH_BASKET );
        addMessage( "Place Sample in High Basket" );
      }
      else if( robot.lift().travelTo( Lift.Position.HIGH_BASKET ) )
      { addMessage( "Move Lift to High Basket" ); }
    }
    //Low basket - x + dpad_down
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_DOWN, Button.X ) ) )
    {
      if( robot.intake().getObservedObject() != Intake.ObservedObject.NOTHING )
      {
        robot.placeSampleInBasket( JoeBot.Basket.LOW_BASKET );
        addMessage( "Place Sample in Low Basket" );
      }
      else if( robot.lift().travelTo( Lift.Position.LOW_BASKET ) )
      { addMessage( "Move Lift to Low Basket" ); }
    }
    //Move lift to bottom - x + a
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.A, Button.X ) ) )
    {
      if( robot.lift().travelTo( Lift.Position.FLOOR ) )
      { addMessage( "Move Lift to Floor" ); }
    }

    //Hang specimen from high bar - x + dpad_left
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.X, Button.DPAD_LEFT ) ) )
    {
      robot.hangSpecimen( JoeBot.Bar.HIGH_BAR );
      addMessage( "Hang Specimen from High Bar" );
    }
    //Hang specimen from low bar - x + dpad_right
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.X, Button.DPAD_RIGHT ) ) )
    {
      robot.hangSpecimen( JoeBot.Bar.LOW_BAR );
      addMessage( "Climb" );
    }

    //Climbing - x + start
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.START, Button.X ) ) )
    {
      robot.climb();
      addMessage( "Climb" );
    }
    //Raise lift slow (high torque) - dpad_up + b
    else if( gamepad2.dpad_up && gamepad2.b && !gamepad2.x )
//    else if( gamepads.buttonsDown( Participant.OPERATOR, EnumSet.of( Button.DPAD_UP, Button.B ) ) )
    {
      if( robot.lift().slowLift() )
      { telemetry.addLine( "Raise lift slow" ); }
    }
    //Lower lift slow (high torque) - dpad_down + b
    else if( gamepad2.dpad_down && gamepad2.b && !gamepad2.x )
//    else if( gamepads.buttonsDown( Participant.OPERATOR, EnumSet.of( Button.DPAD_DOWN, Button.B ) ) )
    {
      if( robot.lift().slowDrop() )
      { telemetry.addLine( "Lower lift slow" ); }
    }
    //Raise lift fast - dpad_up
    else if( gamepad2.dpad_up && !gamepad2.x )
    //    else if( gamepads.buttonDown( Participant.OPERATOR, Button.DPAD_UP ) )
    {
      if( robot.lift().fastLift() )
      { telemetry.addLine( "Raise lift fast" ); }
    }
    //Lower lift fast- dpad_down
    else if( gamepad2.dpad_down && !gamepad2.x )
//    else if( gamepads.buttonDown( Participant.OPERATOR, Button.DPAD_DOWN ) )
    {
      if( robot.lift().fastDrop() )
      { telemetry.addLine( "Lower lift fast" ); }
    }

    //==================
    //Intake
    //==================
    robot.intake().resetColor();

    if( robot.intake().actUponColor() )
    {
      gamepad1.rumbleBlips( 3 );
      gamepad2.rumbleBlips( 3 );
    }

    //Pull in sample
    if( gamepad2.left_stick_y > 0 &&
        //prevent sucking in a sample if the lift is in the air
        //to avoid dumping into the robot
        robot.lift().liftPosition() < 200 )
    {
      robot.intake().pullSampleBack();
    }
    //Spit out sample
    else if ( gamepad2.left_stick_y < 0 )
    {
      robot.intake().pushSampleForward();
    }
    //Stop
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.LEFT_STICK ) ||
             gamepads.buttonPressed( Participant.OPERATOR, Button.B ) )
    {
      robot.intake().stop();
    }

    //==================
    //Drive
    //==================

    if( gamepads.buttonPressed( Participant.DRIVER, Button.BACK ) )
    {
      robot.drive().resetPose();
    }

    //Turn around
    if( gamepads.buttonPressed( Participant.DRIVER, Button.RIGHT_BUMPER ) )
    {
      robot.drive().turnAround( RotateDirection.RIGHT );
    }
    else if( gamepads.buttonPressed( Participant.DRIVER, Button.LEFT_BUMPER ) )
    {
      robot.drive().turnAround( RotateDirection.LEFT );
    }

    //Standard directions
    else if( gamepads.buttonsPressed( Participant.DRIVER, EnumSet.of( Button.DPAD_LEFT, Button.DPAD_UP ) ) )
    {
      robot.drive().faceDirection( PresetDirection.UP_LEFT);
    }
    else if( gamepads.buttonsPressed( Participant.DRIVER, EnumSet.of( Button.DPAD_LEFT, Button.DPAD_DOWN ) ) )
    {
      robot.drive().faceDirection( PresetDirection.DOWN_LEFT );
    }
    else if( gamepads.buttonsPressed( Participant.DRIVER, EnumSet.of( Button.DPAD_RIGHT, Button.DPAD_UP ) ) )
    {
      robot.drive().faceDirection( PresetDirection.UP_RIGHT );
    }
    else if( gamepads.buttonsPressed( Participant.DRIVER, EnumSet.of( Button.DPAD_RIGHT, Button.DPAD_DOWN ) ) )
    {
      robot.drive().faceDirection( PresetDirection.DOWN_RIGHT );
    }
    else if( gamepads.buttonPressed( Participant.DRIVER, Button.DPAD_LEFT))
    {
      robot.drive().faceDirection( PresetDirection.LEFT );
    }
    else if( gamepads.buttonPressed( Participant.DRIVER, Button.DPAD_RIGHT ) )
    {
      robot.drive().faceDirection( PresetDirection.RIGHT );
    }
    else if( gamepads.buttonPressed( Participant.DRIVER, Button.DPAD_UP))
    {
      robot.drive().faceDirection( PresetDirection.FOREWARD );
    }
    else if( gamepads.buttonPressed( Participant.DRIVER, Button.DPAD_DOWN))
    {
      robot.drive().faceDirection( PresetDirection.BACKWARD );
    }

    if( gamepads.buttonPressed( Participant.DRIVER, Button.LEFT_STICK ) )
    {
      robot.drive().togglePerspective();
    }


    final double forward = gamepad1.left_stick_y;
    final double strafe = -( gamepad1.left_stick_x + gamepad1.right_stick_x );
    final double rotate = gamepad1.left_trigger - gamepad1.right_trigger;
    robot.drive().move( forward, strafe, rotate );

    //Cycle through telemetry
    if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.GUIDE ) )
    {
      Module[] modules = Module.values();

      if( currentModule == modules[ modules.length - 1 ] )
      { currentModule = modules[ 0 ]; }
      else
      { currentModule = Module.values()[ currentModule.ordinal() + 1 ]; }
    }

    telemetry.addData( "%s", currentModule );

    switch( currentModule )
    {
      case DRIVE:
        robot.drive().printTelemetry();
        break;
      case INTAKE:
        robot.intake().printTelemetry();
        break;
      case LIFT:
        robot.lift().printTelemetry();
        break;
      case EXTENSION_ARM:
        robot.extensionArm().printTelemetry();
        break;
    }

    gamepads.storeLastButtons();

    long fps = Math.round( 1.0 / time.seconds() );
    telemetry.addData( "FPS", "%s", fps );
    telemetry.update();
    time.reset();
  }

  //Called when the OpMode terminates
  @Override
  public void stop()
  {
    robot.stop();
    robot.drive().coast();
  }

}
