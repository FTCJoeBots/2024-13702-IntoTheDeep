package org.firstinspires.ftc.teamcode.opmode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SampleLift {
  public DcMotor LiftMotor=null;

  void init (HardwareMap hardwareMap) {
    LiftMotor=hardwareMap.get(DcMotor.class,"leftLiftMotor");
    LiftMotor.setPower(0);
    LiftMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    LiftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    LiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    LiftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
  }

}