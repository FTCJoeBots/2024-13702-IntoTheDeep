package org.firstinspires.ftc.teamcode.modules.vision;

public enum Color
{
  RED( 0 ),
  YELLOW( 1 ),
  BLUE( 2 ),
  NOTHING( -1 );

  Color( int value )
  {
    this.value = value;
  }

  public final int value;
}
