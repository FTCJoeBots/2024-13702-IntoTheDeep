package org.firstinspires.ftc.teamcode.modules.vision;

public class Sample
{
  Sample()
  {}

  Sample( double[] pythonData )
  {
    horizontalPosition = pythonData[ 0 ];
    verticalPosition = pythonData[ 1 ];
    area = pythonData[ 2 ];
    
    double colorValue = pythonData[ 3 ];
    if( colorValue == Color.RED.value )
    { color = Color.RED; }
    else if( colorValue == Color.BLUE.value )
    { color=Color.BLUE;}
    else if( colorValue == Color.YELLOW.value )
    { color=Color.YELLOW;}
    else
    { color=Color.NOTHING;}
  }

  public double horizontalPosition = 0;
  public double verticalPosition = 0;
  public double area = 0;
  public Color color = Color.NOTHING;
  public int age = 0;
}
