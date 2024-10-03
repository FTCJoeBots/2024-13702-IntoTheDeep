package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.drive.modules.ExtensionArm;

public class JoeBot {

    public ExtensionArm extensionArm = null;

    public JoeBot( HardwareMap hardwareMap )
    {
        extensionArm = new ExtensionArm( hardwareMap );
    }

    public void stop()
    {
      extensionArm.stop();
    }
}
