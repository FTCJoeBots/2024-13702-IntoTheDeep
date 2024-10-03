package org.firstinspires.ftc.teamcode.drive.opmode;

//import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//
import org.firstinspires.ftc.teamcode.drive.JoeBot;
import org.firstinspires.ftc.teamcode.drive.modules.Flipper;

@Autonomous(group = "drive")
public class ModuleTester extends LinearOpMode {
    @Override

    public void runOpMode() throws InterruptedException {

        //Allocate a robot for us to control
        Flipper flipper = new Flipper( hardwareMap );

        waitForStart();

        if (isStopRequested())
        { return; }

        //wait 1 second for flipper to move to the target position
        final int WAIT_DELAY = 1000;

        while ( !isStopRequested() &&
                 opModeIsActive() )
        {
            //test moving the flipper left and then right two times
            for( int i = 0; i < 2; i++ ) {
                flipper.setTwistPosition(Flipper.TwistPosition.LEFT);
                sleep(WAIT_DELAY);
                flipper.setTwistPosition(Flipper.TwistPosition.RIGHT);
                sleep(WAIT_DELAY);
            }

            //test running the motor at each speed
            for( Flipper.MotorPower motorPower : Flipper.MotorPower.values() ) {
                flipper.setExtensionMotorSpeed(motorPower);
                sleep(WAIT_DELAY);
            }
        }
    }
}
