package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by mnmx0 on 1/13/2018.
 */
@Autonomous(name = "Autobasic", group = "Autonomous")
public class AutoBasic extends LinearOpMode {
    ArchetypeHardware robot = new ArchetypeHardware();
    @Override
    public void runOpMode() throws InterruptedException {
        robot.driveByTime(1, "FORWARDS", 1.0);
    }
}