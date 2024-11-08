package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.teamcode.enums.Button;
import org.firstinspires.ftc.teamcode.enums.Participant;

import java.util.Set;

public class Gamepads
{
  public Gamepads( Gamepad gamepad1, Gamepad gamepad2 )
  {
    this.gamepad1 = gamepad1;
    this.gamepad2 = gamepad2;
    storeLastButtons();
  }

  public boolean buttonPressed( Participant participant, Button button )
  {
    if( ( participant == Participant.DRIVER ||
          participant == Participant.DRIVER_OR_OPERATOR ) &&
        buttonDown( gamepad1, button ) &&
        !buttonDown( previousButtons1, button ) )
    { return true; }

    return ( participant == Participant.OPERATOR ||
             participant == Participant.DRIVER_OR_OPERATOR ) &&
           buttonDown( gamepad2, button ) &&
           !buttonDown( previousButtons2, button );
  }

  public boolean buttonsPressed( Participant participant, Set<Button> buttons )
  {
    if( ( participant == Participant.DRIVER ||
          participant == Participant.DRIVER_OR_OPERATOR ) &&
        buttonsPressed( buttons, gamepad1, previousButtons1 ) )
    { return true; }

    if( ( participant == Participant.OPERATOR ||
          participant == Participant.DRIVER_OR_OPERATOR ) &&
      buttonsPressed( buttons, gamepad2, previousButtons2 ) )
    { return true; }

    return false;
  }

  public void storeLastButtons()
  {
    previousButtons1.copy( gamepad1 );
    previousButtons2.copy( gamepad2 );
  }

  private boolean buttonDown( Gamepad gamepad, Button button )
  {
    switch( button )
    {
      case DPAD_LEFT:
        return gamepad.dpad_left;
      case DPAD_RIGHT:
        return gamepad.dpad_right;
      case DPAD_UP:
        return gamepad.dpad_up;
      case DPAD_DOWN:
        return gamepad.dpad_down;
      case LEFT_STICK:
        return gamepad.left_stick_button;
      case RIGHT_STICK:
        return gamepad.right_stick_button;
      case LEFT_BUMPER:
        return gamepad.left_bumper;
      case RIGHT_BUMPER:
        return gamepad.right_bumper;
      case A:
        return gamepad.a;
      case B:
        return gamepad.b;
      case X:
        return gamepad.x;
      case Y:
        return gamepad.y;
      case GUIDE:
        return gamepad.guide;
      case START:
        return gamepad.start;
      case BACK:
        return gamepad.back;
    }

    return false;
  }

  private boolean buttonsPressed( Set<Button> buttons, Gamepad gamepad, Gamepad previousButtons )
  {
    //check that all buttons are down
    for( Button button : buttons )
    {
      if( !buttonDown( gamepad, button ) )
      { return false; }
    }

    //at least one button must have been just pressed
    for( Button button : buttons )
    {
      if( !buttonDown( previousButtons, button ) )
      { return true; }
    }

    return false;
  }

  private final Gamepad gamepad1;
  private final Gamepad gamepad2;
  private final Gamepad previousButtons1 = new Gamepad();;
  private final Gamepad previousButtons2 = new Gamepad();
}
