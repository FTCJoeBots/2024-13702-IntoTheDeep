package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.drive.modules.ExtensionArm;
import org.firstinspires.ftc.teamcode.drive.modules.Flipper;

public class JoeBot {

    public Flipper flipper = null;
    public ExtensionArm extensionArm = null;

    public JoeBot( HardwareMap hardwareMap )
    {
        flipper = new Flipper( hardwareMap );
        extensionArm = new ExtensionArm( hardwareMap );
    }
}
