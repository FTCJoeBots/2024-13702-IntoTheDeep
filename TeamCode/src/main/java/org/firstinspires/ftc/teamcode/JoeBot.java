package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.modules.DriveSystem;
import org.firstinspires.ftc.teamcode.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.modules.Intake;
import org.firstinspires.ftc.teamcode.modules.Lift;

public class JoeBot
{
  private static ExtensionArm m_extensionArm = null;
  private static Lift m_lift = null;
  private static Intake m_intake = null;
  private static DriveSystem m_drive = null;

  public JoeBot( HardwareMap hardwareMap, Telemetry telemetry )
  {
    //avoid recreating modules to avoid reseting encoders in between autonomus and tele op
    if( m_extensionArm == null )
    {
      m_extensionArm = new ExtensionArm( hardwareMap, telemetry );
      m_lift = new Lift( hardwareMap, telemetry );
      m_intake = new Intake( hardwareMap, telemetry );
      m_drive = new DriveSystem( hardwareMap, telemetry );
    }
  }

  public ExtensionArm extensionArm()
  { return m_extensionArm; }

  public Lift lift()
  { return m_lift; }

  public Intake intake()
  { return m_intake; }

  public DriveSystem drive()
  { return m_drive; }

  public void stop()
  {
    m_extensionArm.stop();
    m_lift.stop();
    m_intake.stop();
    m_drive.stop();
  }
}
