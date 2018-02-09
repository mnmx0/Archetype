package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
/*
  7955 Archetype Mechanum (FIRST Relic Recovery) TeleOp
  2017
  Denis and Max
*/

@TeleOp(name="ArchTeleOp", group="Linear OpMode")

public class MechanumOp extends LinearOpMode{

    public ArchetypeHardware robot = new ArchetypeHardware();
    public void runOpMode () {

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        robot.frontLeft.setDirection(DcMotor.Direction.FORWARD);
        robot.frontRight.setDirection(DcMotor.Direction.FORWARD);
        robot.backLeft.setDirection(DcMotor.Direction.FORWARD);
        robot.backRight.setDirection(DcMotor.Direction.FORWARD);
        robot.gLift.setDirection(DcMotor.Direction.REVERSE);
        double s = 1;
        waitForStart();
        robot.runtime.reset();

        while (opModeIsActive()) {
           if(gamepad1.left_bumper) {
               s-=0.1;
           }
           if(gamepad1.right_bumper) {
               s+=0.1;
           }
           if(gamepad2.a) {
               robot.extendRelicArm();
           }
           if(gamepad2.b) {
               robot.retractRelicArm();
           }
           if(gamepad1.y) {
               robot.pickGlyph();
           }
           if(gamepad1.b) {
               robot.dropGlyph();
           }
           robot.quadrantDrive(gamepad1.left_stick_x,gamepad1.left_stick_y,gamepad1.right_stick_x,s);
        }
    }
}