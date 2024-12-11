package org.firstinspires.ftc.teamcode;
//very good auto

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.TankDrive;


@Config
@Autonomous(name = "AutoTest", group = "idk")
public class AutoTester extends LinearOpMode {

    @Override
    public void runOpMode() {

        Pose2d startPose = new Pose2d(0, 0, Math.toRadians(-90));// Starting position
        TankDrive drive = new  TankDrive(hardwareMap, startPose);


        Action start = drive.actionBuilder(drive.pose)
                .lineToX(6)
                .build();



        while(!isStopRequested() && !opModeIsActive()) {}

        waitForStart();

        Actions.runBlocking(new SequentialAction(

                start

        ));

        if (isStopRequested()) return;
    }
}