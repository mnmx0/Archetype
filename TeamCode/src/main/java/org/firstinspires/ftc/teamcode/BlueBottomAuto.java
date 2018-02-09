package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;

/**
 * Created by mnmx0 on 1/24/2018.
 */
@Autonomous(name = "Bottom blue", group = "Autonomous")
public class BlueBottomAuto extends LinearOpMode {
    ArchetypeHardware robot  = new ArchetypeHardware();
    public void runOpMode() throws InterruptedException {
        init();
        robot.encoderDrive(1,-robot.BOTTOM_DIST_TO_JEWEL,-robot.BOTTOM_DIST_TO_JEWEL,10);
        robot.lowerStick();
        if(robot.stick.red()>30) {
            robot.driveByTime(0.25, "FORWARDS", 1);
            robot.raiseStick();
            robot.driveByTime(0.25, "BACKWARDS", 1);
        }
        else if(robot.stick.blue()>30) {
            robot.driveByTime(0.25, "BACKWARDS", 1);
            robot.raiseStick();
            robot.driveByTime(0.25, "FORWARDS", 1);
        }
        if(robot.vuMark == RelicRecoveryVuMark.CENTER) {
            robot.loc = 1;
        } else if(robot.vuMark == RelicRecoveryVuMark.LEFT) {
            robot.loc = 2;
        } else if(robot.vuMark == RelicRecoveryVuMark.RIGHT) {
            robot.loc = 0;
        }
        robot.encoderDrive(1,robot.BOTTOM_DIST_TO_JEWEL-12,robot.BOTTOM_DIST_TO_JEWEL-12,10);
        robot.encoderDrive(1,robot.PI*robot.ROBOT_RADIUS_INCHES,-robot.PI*robot.ROBOT_RADIUS_INCHES,10);
        robot.sidewaysEncoderDrive(1,-robot.loc*6,10);
        robot.encoderDrive(1,10,10,10);
        robot.dropGlyph();
    }
}