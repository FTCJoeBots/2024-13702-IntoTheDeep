package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.drive.modules.DriveSystem;
import org.firstinspires.ftc.teamcode.drive.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.drive.modules.Intake;
import org.firstinspires.ftc.teamcode.drive.modules.Lift;

public class JoeBot
{
  public static ExtensionArm extensionArm = null;
  public static Lift lift = null;
  public static Intake intake = null;
  public static DriveSystem drive = null;

  public JoeBot( HardwareMap hardwareMap, Telemetry telemetry )
  {
    //avoid recreating modules to avoid reseting encoders in between autonomus and tele op
    if( extensionArm == null )
    {
      extensionArm = new ExtensionArm( hardwareMap, telemetry );
      lift = new Lift( hardwareMap, telemetry );
      intake = new Intake( hardwareMap, telemetry );
      drive = new DriveSystem( hardwareMap, telemetry );
    }
  }

  public void stop()
  {
    extensionArm.stop();
    lift.stop();
    intake.stop();
    drive.stop();
  }
}
