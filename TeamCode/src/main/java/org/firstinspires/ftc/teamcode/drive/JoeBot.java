package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.drive.modules.Lift;

public class JoeBot
{

  public ExtensionArm extensionArm = null;
  public Lift lift = null;

  public JoeBot( HardwareMap hardwareMap, Telemetry telemetry )
  {
    extensionArm = new ExtensionArm( hardwareMap, telemetry );
    lift = new Lift( hardwareMap, telemetry );
  }

  public void stop()
  {
    extensionArm.stop();
    lift.stop();
  }
}
