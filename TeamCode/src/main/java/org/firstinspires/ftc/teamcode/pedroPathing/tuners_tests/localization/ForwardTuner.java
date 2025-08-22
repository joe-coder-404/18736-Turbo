package org.firstinspires.ftc.teamcode.pedroPathing.tuners_tests.localization;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.localization.PoseUpdater;
import com.pedropathing.util.Constants;
import com.pedropathing.util.DashboardPoseTracker;
import com.pedropathing.util.Drawing;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.constants.LConstants;
import org.firstinspires.ftc.teamcode.pedroPathing.DataStorage.LinearMultiplierDataBass;

@Config
@Autonomous(name = "Forward Localizer Tuner", group = ".Localization")
public class ForwardTuner extends OpMode {
    private PoseUpdater poseUpdater;
    private DashboardPoseTracker dashboardPoseTracker;
    private Telemetry telemetryA;

    private LinearMultiplierDataBass db; // database (optional)

    // --- NEW: in-memory running average state ---
    private double runningAvg = 0.0;
    private int sampleCount = 0;

    public static double DISTANCE = 48;

    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        poseUpdater = new PoseUpdater(hardwareMap, FConstants.class, LConstants.class);
        dashboardPoseTracker = new DashboardPoseTracker(poseUpdater);

        // If your DB was causing issues, you can comment this out safely.
        db = new LinearMultiplierDataBass(hardwareMap.appContext);

        telemetryA = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        telemetryA.addLine("Pull your robot forward " + DISTANCE + " inches. Multiplier will be averaged.");
        telemetryA.update();

        Drawing.drawRobot(poseUpdater.getPose(), "#4CAF50");
        Drawing.sendPacket();
    }

    public void loop() {
        poseUpdater.update();

        double fwdMult = poseUpdater.getLocalizer().getForwardMultiplier();
        double x = poseUpdater.getPose().getX();
        if (Math.abs(fwdMult) < 1e-9) fwdMult = 1.0; // fallback safe

        double multiplier = DISTANCE / (x / fwdMult);

        telemetryA.addData("distance moved (x)", x);
        telemetryA.addData("multiplier", "%.6f", multiplier);

        // --- Save permanently to DB only when A is pressed ---
        if (gamepad1.a) {
            db.insertMultiplier(multiplier);
        }

        // Always show permanent DB average
        double avgFromDb = db.getAverageMultiplier();
        telemetryA.addData("average multiplier (DB)", "%.6f", avgFromDb);

        telemetryA.update();

        Drawing.drawPoseHistory(dashboardPoseTracker, "#4CAF50");
        Drawing.drawRobot(poseUpdater.getPose(), "#4CAF50");
        Drawing.sendPacket();
    }
}
