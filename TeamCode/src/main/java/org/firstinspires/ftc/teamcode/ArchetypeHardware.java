package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;


/*
* This is the hardware class, it should be initialized at every Opmode
* This is about 16x easier than declaring all your motors and servos at the beginning of a class
*/
public class ArchetypeHardware extends LinearOpMode {
    public DcMotor frontLeft,frontRight,backLeft,backRight,gLift,relicArm;
    public Servo leftG,rightG,sensorStick,relicGrab;
    public double speedmultiplier;
    public static final int MOTOR_TICKS = 1120;// Andymark Motor Encoder
    public static final double mmWheelDiameter = 101.6;// For figuring circumference
    public static final double pi = 3.14159265358979;
    public static final double MM_TO_INCHES = 0.0393700787;
    public static final double COUNTS_PER_INCH = MM_TO_INCHES*pi*mmWheelDiameter;
    public ElapsedTime runtime = new ElapsedTime();
    public ColorSensor stick;
    VuforiaLocalizer vuforia;
    VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
    VuforiaTrackable relicTemplate = relicTrackables.get(0);
    OpenGLMatrix pose = ((VuforiaTrackableDefaultListener)relicTemplate.getListener()).getPose();
    RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
    public int loc=1;

    //variables that can be changed here instead of int the opMode
    public final double BOTTOM_DIST_TO_JEWEL = 0;
    //bottom distance to the jewel, about 0, used in bottom methods
    public final double TOP_DIST_TO_JEWEL = 0;
    //top distance to the jewel, used in top methods
    public final double STRAFE_COEFFICIENT = 0.5;
    //the ratio between the strafing distance covered and the normal in the same amount of rotations
    public final double PI = 3.14159265358979323846264;
    //pi
    public final double ROBOT_RADIUS_INCHES = 3;
    //robot's radius, this should be adjusted until the robot can drive in a circle with 2*pi*ROBOT_RADIUS_INCHES with encoder drive
    public final double GLYPH_CLOSE_POS = .25;
    //edit until when the servos are set into position, the glypht is all the way closed
    public final double GLYPH_OPEN_POS = .75;
    //edit until when the servoes are set into position, the glypht is all the way open
    public final double STICK_UP_POS = 0;
    //edit until the stick is all the way up when the poisition is STICK_UP_POS
    public final double STICK_DOWN_POS = .6;
    //edit until the stick is all the way down when the position is STICK_DOWN_POS


    public void init(HardwareMap hwMap) {
        speedmultiplier = 1.0;
        frontLeft = hwMap.get(DcMotor.class, "frontLeft");
        frontRight = hwMap.get(DcMotor.class, "frontRight");
        backLeft = hwMap.get(DcMotor.class, "rearLeft");
        backRight = hwMap.get(DcMotor.class, "rearRight");
        gLift = hwMap.get(DcMotor.class, "gLift");
        leftG = hwMap.get(Servo.class, "leftG");
        rightG = hwMap.get(Servo.class, "rightG");
        sensorStick = hwMap.get(Servo.class, "stick");
        setDefaultDirection();
        setZeroPowerBehavior();
        stop();
        //vuforia
        final String TAG = "Vuforia";
        OpenGLMatrix lastLocation = null;
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "Aeysdur/////AAAAGcYxiLk5WEnKlpLTU6AwnhIoVkBamf+dSq3vRLtf1ubbbnaTMM45ND/dHalvOvVnAhVf5KHRuGyG6icyOc/2v/TUqbqR9sRFUkyxMp8toeGbceYXi5LTZifAwHD0yWG0/QIXljssxIeLjgm63KN9pnJevxJCqm0G2RmGkbhRMJsGoXtkQuu3v2RVKoS85nQe+5QFbgIfH3GhqwsSHypXqxq8j/SIYfNSF3vF3GnENrK2J+i5SViuIRc+n3jFQMjvG1bB4oF6j1+gnooZ4wP8gedLfTUx4wqysQoKVVaU7AIAbo5/OvtiELArEO/k6BD7qau5dJVivjfEHjEFQ95cPDQp3VbdvfmVrT2TsRAmOCfP";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        relicTemplate.setName("relicVuMarkTemplate");
    }
    //This Drive Function is better, because if you hit a dead zone, it throws an exception
    //However this Drive is Dual Stick
    public void drive(double x, double y, double z) {
        frontLeft.setPower(scalePower(speedmultiplier*(y-x)));
        backLeft.setPower(scalePower(speedmultiplier*(y+x)));
        frontRight.setPower(scalePower(speedmultiplier*(z+x)));
        backRight.setPower(scalePower(speedmultiplier*(z-x)));
    }

    public void OGdrive(double x, double y, double z) {
        double frontLeftY = y;
        double frontRightY = y;
        double backLeftY = y;
        double backRightY = y;

        //left-right motion
        double frontLeftX = x;
        double frontRightX = -1*x;
        double backLeftX = -1*x;
        double backRightX = x;

        //turning
        double frontLeftT = z;
        double frontRightT = -1*z;
        double backLeftT = z;
        double backRightT = -1*z;

        //summing the vectors
        double frontLeftTotal = frontLeftY + frontLeftX + frontLeftT;
        double frontRightTotal = frontRightY + frontRightX + frontRightT;
        double backLeftTotal = backLeftY + backLeftX + backLeftT;
        double backRightTotal = backRightY + backRightX + backRightT;

        frontLeft.setPower(scalePower(frontLeftTotal));
        frontRight.setPower(scalePower(frontRightTotal));
        backLeft.setPower(scalePower(backLeftTotal));
        backRight.setPower(scalePower(backRightTotal));
    }
    public void quadrantDrive(double x, double y, double t, double s) {
        if(Math.abs(x)>Math.abs(y)&&x>0) {
            frontLeft.setPower(s);
            frontRight.setPower(-s);
            backLeft.setPower(-s);
            backRight.setPower(s);
        }
        else if(Math.abs(x)<Math.abs(y)&&x<0) {
            frontLeft.setPower(-s);
            frontRight.setPower(s);
            backLeft.setPower(s);
            backRight.setPower(-s);
        }
        else if(Math.abs(y)>Math.abs(x)&&y>0) {
            frontLeft.setPower(s);
            frontRight.setPower(s);
            backLeft.setPower(s);
            backRight.setPower(s);
        }
        else if(Math.abs(y)>Math.abs(x)&&y<0) {
            frontLeft.setPower(-1);
            frontRight.setPower(-1);
            backLeft.setPower(-1);
            backRight.setPower(-1);
        }
        else if(Math.abs(t)>0) {
            double sign = Math.abs(t)/t;
            frontLeft.setPower(s*sign);
            frontRight.setPower(-s*sign);
            backLeft.setPower(s*sign);
            backRight.setPower(-s*sign);
        }
    }
    public void setMotorPower(double power){
        frontRight.setPower(power);
        frontLeft.setPower(power);
        backRight.setPower(power);
        backLeft.setPower(power);
    }

    public void setDefaultDirection(){
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        gLift.setDirection(DcMotor.Direction.REVERSE);
    }

    public double scalePower(double power) {
        //This Is The PowerScaling
        float scaledPower;
        power = Range.clip(power, -1, 1);
        float[] possiblePowerValues = {
                0.00f, 0.05f, 0.09f, 0.10f, 0.12f,
                0.15f, 0.18f, 0.24f, 0.30f, 0.36f,
                0.43f, 0.50f, 0.60f, 0.72f, 0.85f,
                1.00f, 1.00f
        };
        int powerIndex = (int)(power * 16.0);
        if (powerIndex < 0) {
            powerIndex = -powerIndex;
        } else if (powerIndex > 16) {
            powerIndex = 16;
        }
        if (power < 0) {
            scaledPower = -possiblePowerValues[powerIndex];
        } else {
            scaledPower = possiblePowerValues[powerIndex];
        }
        return scaledPower;
    }

    public void setZeroPowerBehavior(){
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void driveByTime(double seconds, String direction, double speed){
        runtime.reset();
        double x = 0.0, y = 0.0, z = 0.0;
        speedmultiplier = speed;
        if(direction.equals("FORWARD")){
            y = 1.0;
            z = 1.0;
        } else if(direction.equals("BACKWARDS")){
            y = -1.0;
            z = -1.0;
        } else if(direction.equals("LEFT") || direction.equals("PORTSIDE")){
            x = -1.0;
        } else if(direction.equals("RIGHT") || direction.equals("STARBOARD")){
            x = 1.0;
        } else if(direction.equals("TURNLEFT")) {
            z=-1.0;
        } else if(direction.equals("TURNRIGHT")) {
            z=1.0;
        } else {
            stop();
        }
        while( runtime.seconds()< seconds){
            quadrantDrive(x,y,z,1);
        }
    }
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = backLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = backRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            frontLeft.setTargetPosition(newLeftTarget);
            backLeft.setTargetPosition(newLeftTarget);
            frontRight.setTargetPosition(newRightTarget);
            backRight.setTargetPosition(newRightTarget);


            // Turn On RUN_TO_POSITION
            frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            frontRight.setPower(Math.abs(speed));
            frontLeft.setPower(Math.abs(speed));
            backRight.setPower(Math.abs(speed));
            backLeft.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (frontRight.isBusy() && backLeft.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.update();
            }

            // Stop all motion;
            frontRight.setPower(0);
            frontLeft.setPower(0);
            backRight.setPower(0);
            backLeft.setPower(0);

            // Turn off RUN_TO_POSITION
            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            //  sleep(250);   // optional pause after each move
        }
    }

    public void sidewaysEncoderDrive(double speed, double inches, double timeoutS) {
        int newFLorBRTarget;
        int newFRorBLTarget;
        if(opModeIsActive()) {
            newFLorBRTarget = frontRight.getCurrentPosition() + (int)(-inches*COUNTS_PER_INCH*1/STRAFE_COEFFICIENT);
            newFRorBLTarget = frontLeft.getCurrentPosition() + (int)(inches*COUNTS_PER_INCH*1/STRAFE_COEFFICIENT);
            frontLeft.setTargetPosition(newFLorBRTarget);
            backRight.setTargetPosition(newFLorBRTarget);
            frontRight.setTargetPosition(newFRorBLTarget);
            backLeft.setTargetPosition(newFRorBLTarget);
            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (frontRight.isBusy() && backLeft.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d ", newFLorBRTarget);
                telemetry.update();
            }
            frontRight.setPower(0);
            frontLeft.setPower(0);
            backRight.setPower(0);
            backLeft.setPower(0);
            frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public void lowerStick() {
        sensorStick.setPosition(STICK_DOWN_POS);
    }

    public void raiseStick() {
        sensorStick.setPosition(STICK_UP_POS);
    }

    public void dropGlyph() {
        leftG.setPosition(GLYPH_OPEN_POS);
        rightG.setPosition(GLYPH_OPEN_POS);
    }
    public void pickGlyph() {
        leftG.setPosition(GLYPH_CLOSE_POS);
        rightG.setPosition(GLYPH_CLOSE_POS);
    }
    public void extendRelicArm() {
        runtime.reset();
        while(runtime.seconds()<4) {
            relicArm.setPower(1);
        }
    }
    public void retractRelicArm() {
        runtime.reset();
        while(runtime.seconds()<4) {
            relicArm.setPower(-1);
        }
    }
    public void runOpMode() {

    }

}