package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Gamepads;
import org.firstinspires.ftc.teamcode.JoeBot;
import org.firstinspires.ftc.teamcode.enums.Button;
import org.firstinspires.ftc.teamcode.enums.Participant;
import org.firstinspires.ftc.teamcode.enums.PresetDirection;
import org.firstinspires.ftc.teamcode.enums.RotateDirection;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.modules.Lift;
import org.firstinspires.ftc.teamcode.enums.Bar;
import org.firstinspires.ftc.teamcode.enums.Basket;
import org.firstinspires.ftc.teamcode.modules.drive.AngleTools;

import java.util.EnumSet;
import java.util.List;

//Tell framework that this is a TeleOp mode
@TeleOp( name = "Manual Joe Bot", group = "Iterative Opmode" )
public class ManualJoeBot extends OpMode
{
  private enum Module
  {
    NONE, CLIMB_ARM, LIFT, EXTENSION_ARM, INTAKE, DRIVE,
  }

  private ElapsedTime time = null;
  private Module currentModule = Module.values()[ 0 ];
  private JoeBot robot = null;
  private Gamepads gamepads = null;

  private final double angleTolerance = 20;

  //We run this when the user hits "INIT" on the app
  @Override
  public void init()
  {
    time = new ElapsedTime();

    gamepads = new Gamepads( gamepad1, gamepad2 );

    robot = new JoeBot( false, hardwareMap, telemetry );
    robot.gamepads = gamepads;

    telemetry.addLine( "Initialized Manual" );
    telemetry.update();

    //Allow robot to be pushed around before the start button is pressed
    robot.drive().coast();

    //auto retract arm and drop lift in case level 1 ascent was previously performed in Autonomous
    if( robot.extensionArm().getMotorPosition() > ExtensionArm.Position.FULLY_RETRACTED.value )
    {
      addMessage( "Automatically Retracting Extension Arm" );
      robot.extensionArm().fullyRetract();
    }

    if( robot.lift().liftPosition() > Lift.Position.FLOOR.value )
    {
      addMessage( "Automatically Dropping Lift" );
      robot.lift().travelTo( Lift.Position.FLOOR );
    }
  }

  @Override
  public void init_loop()
  {
    //Print out location so we can calibrate X,Y positions and verify heading
    robot.drive().printTelemetry();
  }

  @Override
  public void start()
  {
    //Prevent robot from being pushed around
    robot.brake();
    robot.updateState( true );
  }

  private void addMessage( String message)
  {
    telemetry.log().add( message );
  }

  @Override
  public void loop()
  {
    //always update the color sensor so we can check if we have a sample before triggering a motion
    robot.updateState( true );

    //==================
    //Extension Arm
    //==================
    //Fully extend - B + Y
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.Y ) ) &&
        !robot.lift().isHigh() )
    {
      robot.extensionArm().fullyExtend();
    }
    //Manually extend - Y
    else if( gamepad2.y )
    {
      boolean liftIsHigh = robot.lift().isHigh();
      robot.extensionArm().manuallyExtend( liftIsHigh );
    }

    //Full retract - B + A
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.A ) ) )
    {
      robot.retrieveSample();
    }
    //Manually retract - A
    else if( gamepad2.a )
    {
      robot.extensionArm().manuallyRetract();
    }

    //==================
    //Lift
    //==================
    //High basket - x + dpad_up
    if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_UP, Button.X ) ) &&
         robot.intake().hasSample() )
//        ( AngleTools.angleDifference( robot, 135 ) < angleTolerance || !JoeBot.competition ) )
    {
      robot.placeSampleInBasket( Basket.HIGH_BASKET );
    }
    //Low basket - x + dpad_down
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.DPAD_DOWN, Button.X ) ) &&
      robot.intake().hasSample() )
//             ( AngleTools.angleDifference( robot, 135 ) < angleTolerance || !JoeBot.competition ) )
    {
      robot.placeSampleInBasket( Basket.LOW_BASKET );
    }
    //Move lift to bottom - B + dpad_down
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.B, Button.DPAD_DOWN ) ) )
    {
      robot.lift().travelTo( Lift.Position.FLOOR );
      addMessage( "Move Lift to Floor" );
    }

    //Hang specimen from high bar - x + dpad_left
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.X, Button.DPAD_LEFT ) ) &&
      robot.intake().hasSample() )
    //             ( AngleTools.angleDifference( robot, 0 ) < angleTolerance || !JoeBot.competition ) )
    {
      robot.hangSpecimen( Bar.HIGH_BAR );
    }
    //Hang specimen from low bar - x + dpad_right
    else if( gamepads.buttonsPressed( Participant.OPERATOR, EnumSet.of( Button.X, Button.DPAD_RIGHT ) ) &&
      robot.intake().hasSample() )
    //             ( AngleTools.angleDifference( robot, 0 ) < angleTolerance || !JoeBot.competition ) )
    {
      robot.hangSpecimen( Bar.LOW_BAR );
    }

    //Level 1 ascent - left bumper
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.LEFT_BUMPER ) )
    {
      robot.levelOneAscent();
    }
    //Level 2 ascent - Right Bumper
    else if( gamepads.buttonPressed( Participant.OPERATOR, Button.RIGHT_BUMPER ) )
    {
      robot.levelTwoAscent();
    }
    //Climb - dpad_down + b
    else if( gamepad2.dpad_down && gamepad2.b && gamepad2.x )
    {
      if( robot.lift().climb() )
      { telemetry.addLine( "Climb" ); }
    }
    //Raise lift - dpad_up
    else if( gamepad2.dpad_up && !gamepad2.x )
    {
      if( robot.lift().fastLift() )
      { telemetry.addLine( "Raise lift" ); }
    }
    //Lower lift- dpad_down
    else if( gamepad2.dpad_down && !gamepad2.x )
    {
      if( robot.lift().fastDrop() )
      { telemetry.addLine( "Lower lift" ); }
    }

    //==================
    //Climb Arm
    //==================
    robot.climbArm().setPower( -gamepad2.left_trigger + gamepad2.right_trigger );

    //==================
    //Intake
    //==================
    //Grab sample - X + pull back left stick
    if( !robot.intake().hasSample() &&
        gamepad2.x &&
        gamepad2.left_stick_y > 0 )
    {
      robot.grabSample( false );
      robot.intake().updateState( true );
      if( robot.intake().hasSample() )
      { robot.lift().travelTo( Lift.Position.ABOVE_HIGH_SPECIMEN_BAR ); }
    }
    //Grab specimen - X + push forward left stick
    else if( !robot.intake().hasSample() &&
             gamepad2.x &&
             gamepad2.left_stick_y < 0 )
    {
      robot.grabSample( true );
      robot.intake().updateState( true );
      if( robot.intake().hasSample() )
      { robot.lift().travelTo( Lift.Position.ABOVE_HIGH_SPECIMEN_BAR ); }
    }
    //Pull in sample
    else if( gamepad2.left_stick_y > 0 &&
        //avoid trapping a sample in the center of the robot
        ( !robot.intake().hasSample() ||
          robot.lift().liftPosition() < 1000 ) )
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
    /*
    if( gamepads.buttonPressed( Participant.DRIVER, Button.BACK ) )
    {
      robot.resetPos();
    }
    */

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

    /*
    if( gamepads.buttonPressed( Participant.DRIVER, Button.LEFT_STICK ) )
    {
      robot.drive().togglePerspective();
    }
    */

    double forward = gamepad1.left_stick_y;
    double strafe = -( gamepad1.left_stick_x + gamepad1.right_stick_x );
    double rotate = gamepad1.left_trigger - gamepad1.right_trigger;

    if( gamepad1.b )
    {
      final double scaleFactor = 0.5;
      forward *= scaleFactor;
      strafe *= scaleFactor;
      rotate *= scaleFactor;
    }

    robot.drive().move( forward, strafe, rotate );

    //Cycle through telemetry
    if( gamepads.buttonPressed( Participant.DRIVER_OR_OPERATOR, Button.GUIDE ) )
    {
      final Module[] modules = Module.values();

      if( currentModule == modules[ modules.length - 1 ] )
      { currentModule = modules[ 0 ]; }
      else
      { currentModule = modules[ currentModule.ordinal() + 1 ]; }
    }

    telemetry.addLine( currentModule.name() );

    long fps = Math.round( 1.0 / time.seconds() );
    telemetry.addData( "FPS", "%s", fps );
    time.reset();

    switch( currentModule )
    {
      case CLIMB_ARM:
        robot.climbArm().printTelemetry();
        break;
      case LIFT:
        robot.lift().printTelemetry();
        break;
      case EXTENSION_ARM:
        robot.extensionArm().printTelemetry();
        break;
      case INTAKE:
        robot.intake().printTelemetry();
        break;
      case DRIVE:
        robot.drive().printTelemetry();
        break;
    }

    telemetry.update();
    gamepads.storeLastButtons();
  }

  //Called when the OpMode terminates
  @Override
  public void stop()
  {
    robot.stop();

    //store position so it can be restored when we start TeleOp
    robot.cachePos();

    //allow robot to be pushed around
    robot.coast();

    JoeBot.competition = false;
  }

}
