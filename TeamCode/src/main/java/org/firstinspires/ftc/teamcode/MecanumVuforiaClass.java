package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import static com.sun.tools.javac.util.Constants.format;

/**
 * Created by mnmx0 on 10/11/2017.
 */

@Autonomous(name="Archetype VuforiaOp", group ="FirstAttempt")
public class MecanumVuforiaClass extends MechanumOp {

    ColorSensor colorSensor;

    public static final String TAG = "Vuforia";

    OpenGLMatrix lastLocation = null;

    VuforiaLocalizer vuforia;

    private DcMotor frontLeft = null;
    private DcMotor frontRight = null;
    private DcMotor backLeft = null;
    private DcMotor backRight = null;


    @Override
    public void runOpMode() {

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        parameters.vuforiaLicenseKey = "Aeysdur/////AAAAGcYxiLk5WEnKlpLTU6AwnhIoVkBamf+dSq3vRLtf1ubbbnaTMM45ND/dHalvOvVnAhVf5KHRuGyG6icyOc/2v/TUqbqR9sRFUkyxMp8toeGbceYXi5LTZifAwHD0yWG0/QIXljssxIeLjgm63KN9pnJevxJCqm0G2RmGkbhRMJsGoXtkQuu3v2RVKoS85nQe+5QFbgIfH3GhqwsSHypXqxq8j/SIYfNSF3vF3GnENrK2J+i5SViuIRc+n3jFQMjvG1bB4oF6j1+gnooZ4wP8gedLfTUx4wqysQoKVVaU7AIAbo5/OvtiELArEO/k6BD7qau5dJVivjfEHjEFQ95cPDQp3VbdvfmVrT2TsRAmOCfP";

        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate");

        telemetry.addData(">", "Press Play to start");
        telemetry.update();
        waitForStart();
        //max originating variables
        int i = 0;

        float hsvValues[] = {0F, 0F, 0F};
        final float values[] = hsvValues;

        char column;

        while (opModeIsActive()) {

            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);

                telemetry.addData("VuMark", "%s visible", vuMark);
                i = 1;

                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener)relicTemplate.getListener()).getPose();
                telemetry.addData("Pose", format(pose));

                if (pose != null) {
                    VectorF trans = pose.getTranslation();
                    Orientation rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

                    double tX = trans.get(0);
                    double tY = trans.get(1);
                    double tZ = trans.get(2);

                    double rX = rot.firstAngle;
                    double rY = rot.secondAngle;
                    double rZ = rot.thirdAngle;
                }
                if (vuMark == RelicRecoveryVuMark.CENTER) {
                    column = 'C';
                }
                else if(vuMark == RelicRecoveryVuMark.RIGHT) {
                    column = 'R';
                }
                else if(vuMark == RelicRecoveryVuMark.LEFT) {
                    column = 'L';
                }
        }
    }
}
