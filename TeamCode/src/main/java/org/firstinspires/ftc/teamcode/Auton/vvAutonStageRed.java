/* Copyright (c) 2019 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.Auton;

import android.util.Size;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.Core.vvHardware;
import org.firstinspires.ftc.teamcode.Core.vvHardwareRR;
import org.firstinspires.ftc.teamcode.Core.vvRoadRunnerDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;
import org.firstinspires.ftc.teamcode.Core.vvHardware;

import java.util.List;
import java.util.Objects;

/*
 * Auton Red - Stage Side
 * Start the robot on the furthest tile edge from the truss (left side)
 *
 */
@Autonomous(name = "vvAutonStageRed", group = "2 - Red Auton")

public class vvAutonStageRed extends LinearOpMode {
    vvHardwareRR robot = new vvHardwareRR(this);

    private ElapsedTime runtime = new ElapsedTime();

    private static final boolean USE_WEBCAM = true;  // true for webcam, false for phone camera

    // TFOD_MODEL_ASSET points to a model file stored in the project Asset location,
    // this is only used for Android Studio when using models in Assets.
    private static final String TFOD_MODEL_ASSET = "vvCenterStage7Nov.tflite";
    // TFOD_MODEL_FILE points to a model file stored onboard the Robot Controller's storage,
    // this is used when uploading models directly to the RC using the model upload interface.
    //private static final String TFOD_MODEL_FILE = "/sdcard/FIRST/tflitemodels/vvCenterStage7Nov.tflite";
    // Define the labels recognized in the model for TFOD (must be in training order!)
    private static final String[] LABELS = {
            "BlueProp", "Pixel", "RedProp"
    };

    /**
     * The variable to store our instance of the TensorFlow Object Detection processor.
     */
    private TfodProcessor tfod;

    /**
     * The variable to store our instance of the vision portal.
     */
    private VisionPortal visionPortal;

    //public vvTFOD(LinearOpMode opmode) { myOpMode = opmode;}
    @Override
    public void runOpMode() {

        ElapsedTime runtime = new ElapsedTime();

        vvRoadRunnerDrive vvdrive = new vvRoadRunnerDrive(hardwareMap);

        robot.init();

        // We want to start the bot at x: 14, y: -60, heading: 90 degrees
        Pose2d startPose = new Pose2d(-40, -63, Math.toRadians(90));

        vvdrive.setPoseEstimate(startPose);

        // Purple Stage Drop Trajectories - Blue **Note these are flipped in the Backdrop
        TrajectorySequence purpleDropTopBlue = vvdrive.trajectorySequenceBuilder(startPose) //Also Red Back
                .forward(38)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> robot.movePickUp(robot.autonPickupLow, robot.pickUpPwr))
                .back(10)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> robot.armPos(robot.armIdle + 5, robot.armEPower))
                .build();
        TrajectorySequence purpleDropLeftBlue = vvdrive.trajectorySequenceBuilder(startPose)
                .forward(28)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> robot.movePickUp(robot.autonPickupLow, robot.pickUpPwr))
                .turn(Math.toRadians(60))
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.armPos(robot.armIdle + 5, robot.armEPower))
                .forward(9)
                .waitSeconds(1)
                .back(3)
                .build();
        TrajectorySequence purpleDropRightBlue = vvdrive.trajectorySequenceBuilder(startPose)
                .forward(34)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> robot.movePickUp(robot.autonPickupLow, robot.pickUpPwr))
                .turn(Math.toRadians(-45))
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.armPos(robot.armIdle + 5, robot.armEPower))
                .back(4)
                .waitSeconds(1)
                .build();
        // Purple Stage Drop Trajectories - Red **Note these are flipped in the Backdrop
        TrajectorySequence purpleDropTopRed = vvdrive.trajectorySequenceBuilder(startPose) //Also Blue Back
                .forward(32)
                .UNSTABLE_addTemporalMarkerOffset(-1.5, () -> robot.movePickUp(robot.autonPickupIdle - 5, robot.pickUpPwr))
                .back(6)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> robot.armPos(robot.armIdle + 5, robot.armEPower))
                .waitSeconds(1)
                .build();
        TrajectorySequence purpleDropLeftRed = vvdrive.trajectorySequenceBuilder(startPose)
                .forward(28)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> robot.movePickUp(robot.autonPickupLow, robot.pickUpPwr))
                .turn(Math.toRadians(60))
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.armPos(robot.armIdle + 5, robot.armEPower))
                .forward(9)
                .waitSeconds(1)
                .back(9)
                .build();
        TrajectorySequence purpleDropRightRed = vvdrive.trajectorySequenceBuilder(startPose)
                .forward(32)
                .UNSTABLE_addTemporalMarkerOffset(-1, () -> robot.movePickUp(robot.autonPickupLow, robot.pickUpPwr))
                .turn(Math.toRadians(-50))
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.armPos(robot.armIdle + 5, robot.armEPower))
                .forward(6)
                .waitSeconds(1)
                .build();
        // Yellow Stage Drop Trajectories - Red
        TrajectorySequence yellowStageDropTopRed = vvdrive.trajectorySequenceBuilder(purpleDropTopRed.end())
                .back(3)
                .UNSTABLE_addTemporalMarkerOffset(-0.2, () -> robot.armPos(robot.armStack, robot.armEPower))
                .turn(Math.toRadians(90))
                .forward(12)
                .strafeRight(40)
                .UNSTABLE_addDisplacementMarkerOffset(-6, () -> robot.movePickUp(robot.autonPickupStack, robot.pickUpPwr))
                .forward(7)
                .UNSTABLE_addTemporalMarkerOffset(-1,() -> robot.rightWheel.setPower(0.9))
                .waitSeconds(1)
                .UNSTABLE_addTemporalMarkerOffset(0,() -> robot.rightWheel.setPower(0))
                .lineToConstantHeading(new Vector2d(24, 3))
                .UNSTABLE_addDisplacementMarkerOffset(-62, () -> robot.armPos(robot.armDoor, robot.armEPower))
                .UNSTABLE_addDisplacementMarkerOffset(-60,() -> robot.movePickUp(robot.autonPickupDoor,robot.pickUpPwr))
                .UNSTABLE_addDisplacementMarkerOffset(-10, () -> robot.armPos(robot.armLow, robot.armEPower))
                .UNSTABLE_addDisplacementMarkerOffset(-8,() -> robot.movePickUp(robot.autonPickupLow,robot.pickUpPwr))
                .turn(Math.toRadians(180))
                .splineToConstantHeading(new Vector2d(42,-30),Math.toRadians(0))
                .forward(6)
                .build();
        // Yellow Backdrop Trajectories - Blue
        TrajectorySequence yellowBackDropTopBlue = vvdrive.trajectorySequenceBuilder(purpleDropTopRed.end())
                .back(3)
                .turn(Math.toRadians(90))
                .forward(44)
                .UNSTABLE_addDisplacementMarkerOffset(-30, () -> robot.armPos(robot.armLow, robot.armEPower))
                .strafeRight(10)
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.movePickUp(robot.autonPickupLow, robot.pickUpPwr))
                .forward(2)
                .build();
        TrajectorySequence yellowBackDropRightBlue = vvdrive.trajectorySequenceBuilder(purpleDropRightRed.end())
                .back(6)
                .turn(Math.toRadians(135))
                .forward(42)
                .UNSTABLE_addDisplacementMarkerOffset(-30, () -> robot.armPos(robot.armLow, robot.armEPower))
                .strafeRight(9)
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.movePickUp(robot.autonPickupLow, robot.pickUpPwr))
                .forward(4)
                .build();
        TrajectorySequence yellowBackDropLeftBlue = vvdrive.trajectorySequenceBuilder(purpleDropLeftRed.end())
                .strafeLeft(10)
                .turn(Math.toRadians(35))
                .forward(44)
                .UNSTABLE_addDisplacementMarkerOffset(-30, () -> robot.armPos(robot.armLow, robot.armEPower))
                .strafeRight(10)
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.movePickUp(robot.autonPickupLow, robot.pickUpPwr))
                .forward(4)
                .build();
        // Yellow Backdrop Trajectories - Red
        TrajectorySequence yellowBackDropTopRed = vvdrive.trajectorySequenceBuilder(purpleDropTopBlue.end())
                .turn(Math.toRadians(-90))
                .forward(48)
                .UNSTABLE_addDisplacementMarkerOffset(-30, () -> robot.armPos(robot.armLow, robot.armEPower))
                .strafeRight(4)
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .forward(4)
                .build();
        TrajectorySequence yellowBackDropRightRed = vvdrive.trajectorySequenceBuilder(purpleDropRightBlue.end())
                .back(6)
                .turn(Math.toRadians(-45))
                .strafeRight(8)
                .forward(48)
                .UNSTABLE_addDisplacementMarkerOffset(-30, () -> robot.armPos(robot.armLow, robot.armEPower))
                .strafeRight(11)
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .forward(3)
                .build();
        TrajectorySequence yellowBackDropLeftRed = vvdrive.trajectorySequenceBuilder(purpleDropLeftBlue.end())
                .back(6)
                .turn(Math.toRadians(-145))
                .forward(48)
                .UNSTABLE_addDisplacementMarkerOffset(-30, () -> robot.armPos(robot.armLow, robot.armEPower))
                .strafeRight(10)
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .forward(4)
                .build();
        // Final Strafe Trajectories - Blue
        TrajectorySequence blueTopEnd = vvdrive.trajectorySequenceBuilder(yellowBackDropTopBlue.end())
                .back(6)
                .strafeLeft(48)
                .UNSTABLE_addTemporalMarkerOffset(-0.8, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .UNSTABLE_addTemporalMarkerOffset(-0.2, () -> robot.armPos(robot.armIdle, robot.armEPower))
                .build();
        TrajectorySequence blueRightEnd = vvdrive.trajectorySequenceBuilder(yellowBackDropRightBlue.end())
                .back(6)
                .strafeLeft(56)
                .UNSTABLE_addTemporalMarkerOffset(-0.8, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .UNSTABLE_addTemporalMarkerOffset(-0.2, () -> robot.armPos(robot.armIdle, robot.armEPower))
                .build();
        TrajectorySequence blueLeftEnd = vvdrive.trajectorySequenceBuilder(yellowBackDropLeftBlue.end())
                .back(6)
                .strafeLeft(36)
                .UNSTABLE_addTemporalMarkerOffset(-0.8, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .UNSTABLE_addTemporalMarkerOffset(-0.2, () -> robot.armPos(robot.armIdle, robot.armEPower))
                .build();
        // Final Strafe Trajectories - Red
        TrajectorySequence redTopEnd = vvdrive.trajectorySequenceBuilder(yellowBackDropTopRed.end())
                .back(6)
                .strafeRight(48)
                .UNSTABLE_addTemporalMarkerOffset(-0.8, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .UNSTABLE_addTemporalMarkerOffset(-0.2, () -> robot.armPos(robot.armIdle, robot.armEPower))
                .build();
        TrajectorySequence redRightEnd = vvdrive.trajectorySequenceBuilder(yellowBackDropRightRed.end())
                .back(4)
                .strafeRight(36)
                .UNSTABLE_addTemporalMarkerOffset(-0.8, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .UNSTABLE_addTemporalMarkerOffset(-0.2, () -> robot.armPos(robot.armIdle, robot.armEPower))
                .build();
        TrajectorySequence redLeftEnd = vvdrive.trajectorySequenceBuilder(yellowBackDropLeftRed.end())
                .back(6)
                .strafeRight(56)
                .UNSTABLE_addTemporalMarkerOffset(-0.8, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .UNSTABLE_addTemporalMarkerOffset(-0.2, () -> robot.armPos(robot.armIdle, robot.armEPower))
                .build();
        // Final Strafe Trajectories - Red, moving to the center
        TrajectorySequence redStageTopEnd = vvdrive.trajectorySequenceBuilder(yellowStageDropTopRed.end())
                .back(6)
                .strafeLeft(38)
                .UNSTABLE_addTemporalMarkerOffset(-0.8, () -> robot.movePickUp(robot.autonPickupIdle, robot.pickUpPwr))
                .UNSTABLE_addTemporalMarkerOffset(-0.2, () -> robot.armPos(robot.armIdle, robot.armEPower))
                .build();

        initTfod();
        String spikeLoc;
        // Wait for the DS start button to be touched.
        telemetry.addData(">", "Robot Ready");
        telemetry.addData("DS preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch Play to start OpMode");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            while (opModeIsActive()) {

                telemetryTfod();
                spikeLoc = startDetection();
                // Push telemetry to the Driver Station.
                telemetry.addData("Location Heading", spikeLoc);
                telemetry.update();

                Pose2d poseEstimate = vvdrive.getPoseEstimate();
                vvdrive.update();

                // Share the CPU.
                sleep(200);

                if (Objects.equals(spikeLoc, "LEFT")){
                    robot.movePickUp(5, robot.pickUpPwr);
                    sleep(500);
                    robot.armPos(robot.armStart, robot.armEPower);
                    sleep(500);
                    vvdrive.followTrajectorySequence(purpleDropLeftRed);
                    telemetry.addData("Parallel Position: ", poseEstimate.getX());
                    telemetry.addData("Perpendicular Position: ", poseEstimate.getY());
                    telemetry.update();
                    robot.rightWheel.setPower(-0.9);
                    sleep(1000);
                    robot.rightWheel.setPower(0);
                }
                if (Objects.equals(spikeLoc, "CENTER")){
                    telemetry.addLine("Running...");
                    telemetry.update();
                    robot.movePickUp(5, robot.pickUpPwr);
                    sleep(250);
                    robot.armPos(robot.armStart, robot.armEPower);
                    vvdrive.followTrajectorySequence(purpleDropTopRed);
                    robot.rightWheel.setPower(-0.9);
                    sleep(500);
                    robot.rightWheel.setPower(0);
                    robot.armPos(robot.armStart, robot.armEPower);
                    vvdrive.followTrajectorySequence(yellowStageDropTopRed);
                    robot.leftWheel.setPower(0.9);
                    robot.rightWheel.setPower(-0.9);
                    sleep(500);
                    robot.leftWheel.setPower(0);
                    robot.rightWheel.setPower(0);
                    vvdrive.followTrajectorySequence(redStageTopEnd);
                }
                if (Objects.equals(spikeLoc, "RIGHT")){
                    robot.movePickUp(5, robot.pickUpPwr);
                    sleep(500);
                    robot.armPos(robot.armStart, robot.armEPower);
                    sleep(500);
                    vvdrive.followTrajectorySequence(purpleDropRightRed);
                    telemetry.addData("Parallel Position: ", poseEstimate.getX());
                    telemetry.addData("Perpendicular Position: ", poseEstimate.getY());
                    telemetry.update();
                    robot.rightWheel.setPower(-0.9);
                    sleep(1000);
                    robot.rightWheel.setPower(0);
                }
                if ((Objects.equals(spikeLoc, "NOTFOUND"))){ //Right is default
                    robot.movePickUp(5, robot.pickUpPwr);
                    sleep(500);
                    robot.armPos(robot.armStart, robot.armEPower);
                    sleep(500);
                    vvdrive.followTrajectorySequence(purpleDropRightRed);
                    telemetry.addData("Parallel Position: ", poseEstimate.getX());
                    telemetry.addData("Perpendicular Position: ", poseEstimate.getY());
                    telemetry.update();
                    robot.rightWheel.setPower(-0.9);
                    sleep(1000);
                    robot.rightWheel.setPower(0);
                }//This will change based upon side

                telemetry.addLine("Running...");
                telemetry.addData("Parallel Position: ", poseEstimate.getX());
                telemetry.addData("Perpendicular Position: ", poseEstimate.getY());
                telemetry.update();

            break;
            }
        }

        // Save more CPU resources when camera is no longer needed.
        visionPortal.close();

    }   // end runOpMode()

    /**
     * Initialize the TensorFlow Object Detection processor.
     */
    protected String startDetection() {
        String teamPropPosition = "NOTFOUND";

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {

            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());


            if (recognition.getLabel() == "RedProp" || recognition.getLabel() == "BlueProp") {

                double x = (recognition.getLeft() + recognition.getRight()) / 2;
                double y = (recognition.getTop() + recognition.getBottom()) / 2;
                double propHeight = (recognition.getBottom() - recognition.getTop());
                double propWidth = (recognition.getRight() - recognition.getLeft());

                telemetry.addLine("propHeight:" + propHeight + ", propWidth:" + propWidth);
                telemetry.addData("- Position", "%.0f / %.0f", x, y);

                if (propHeight > 70 && propHeight < 170 && propWidth > 70 && propWidth < 170) {
                    if (x < 200) {
                        teamPropPosition = "LEFT";
                    } else if (x >= 201 && x < 500) {
                        teamPropPosition = "CENTER";
                    } else {
                        teamPropPosition = "RIGHT";
                    }
                    break;
                }
            }
        }   // end for() loop
        telemetry.addLine("after for loop in getTeamPropPosition");
        visionPortal.stopStreaming();
        return teamPropPosition;
    }

    public void initTfod() {

        // Create the TensorFlow processor by using a builder.
        tfod = new TfodProcessor.Builder()

                // With the following lines commented out, the default TfodProcessor Builder
                // will load the default model for the season. To define a custom model to load,
                // choose one of the following:
                //   Use setModelAssetName() if the custom TF Model is built in as an asset (AS only).
                //   Use setModelFileName() if you have downloaded a custom team model to the Robot Controller.
                .setModelAssetName(TFOD_MODEL_ASSET)
                //.setModelFileName(TFOD_MODEL_FILE)

                // The following default settings are available to un-comment and edit as needed to
                // set parameters for custom models.
                .setModelLabels(LABELS)
                .setIsModelTensorFlow2(true)
                .setIsModelQuantized(true)
                .setModelInputSize(300)
                .setModelAspectRatio(16.0 / 9.0)

                .build();

        // Create the vision portal by using a builder.
        VisionPortal.Builder builder = new VisionPortal.Builder();

        // Set the camera (webcam vs. built-in RC phone camera).
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "Webcam1"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        // Choose a camera resolution. Not all cameras support all resolutions.
        builder.setCameraResolution(new Size(640, 480));

        // Enable the RC preview (LiveView).  Set "false" to omit camera monitoring.
        builder.enableLiveView(true);

        // Set the stream format; MJPEG uses less bandwidth than default YUY2.
        builder.setStreamFormat(VisionPortal.StreamFormat.MJPEG);

        // Choose whether or not LiveView stops if no processors are enabled.
        // If set "true", monitor shows solid orange screen if no processors enabled.
        // If set "false", monitor shows camera view without annotations.
        builder.setAutoStopLiveView(false);

        // Set and enable the processor.
        builder.addProcessor(tfod);

        // Build the Vision Portal, using the above settings.
        visionPortal = builder.build();

        // Set confidence threshold for TFOD recognitions, at any time.
        //tfod.setMinResultConfidence(0.75f);

        // Disable or re-enable the TFOD processor at any time.
        visionPortal.setProcessorEnabled(tfod, true);

    }   // end method initTfod()

    /**
     * Add telemetry about TensorFlow Object Detection (TFOD) recognitions.
     */
    private void telemetryTfod() {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2;
            double y = (recognition.getTop() + recognition.getBottom()) / 2;

            telemetry.addData("", " ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }   // end method telemetryTfod()

    public void locTfod() {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2;
            double y = (recognition.getTop() + recognition.getBottom()) / 2;

            telemetry.addData("", " ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }   // end method locTfod()

}   // end class
