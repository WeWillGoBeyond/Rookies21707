package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp
public class POC_FlywheelTuner extends OpMode {
    public DcMotorEx launcher;

    double highVelocity = 400;
    double lowVelocity = 200;
    double curTargetVelocity = highVelocity;
    double F = 0;
    double P = 0;
    double[] stepSizes= {10.0, 1.0, 0.1, 0.01, 0.001, 0.0001};
    int stepIndex = 1;

    @Override
    public void init(){
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setDirection(DcMotorSimple.Direction.FORWARD);
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        launcher.setPIDFCoefficients((DcMotor.RunMode.RUN_USING_ENCODER), pidfCoefficients);
        telemetry.addLine("Init complete");

    }
    @Override
    public void loop(){
        //get all gamepad commands
        //set target velocities
        //update telemetry

        if(gamepad1.yWasPressed()){
            if(curTargetVelocity == highVelocity){
                curTargetVelocity = lowVelocity;
            } else {
                curTargetVelocity = highVelocity;
            }
        }

        if(gamepad1.bWasPressed()){
            stepIndex = (stepIndex +1) % stepSizes.length;
        }

        if (gamepad1.dpadLeftWasPressed()){
            F += stepSizes[stepIndex];
        }
        if(gamepad1.dpadRightWasPressed()){
            F -= stepSizes[stepIndex];
        }

        if(gamepad1.dpadUpWasPressed()){
            P += stepSizes[stepIndex];
        }
        if(gamepad1.dpadDownWasPressed()){
            P -= stepSizes[stepIndex];
        }

        //set new PIDF coefficients
        PIDFCoefficients pidfCoefficients = new PIDFCoefficients(P, 0, 0, F);
        launcher.setPIDFCoefficients((DcMotor.RunMode.RUN_USING_ENCODER), pidfCoefficients);

        //set velocity
        launcher.setVelocity(curTargetVelocity);

        double curVelocity = launcher.getVelocity();
        double error = curTargetVelocity - curVelocity;

        telemetry.addData("Target Velocity", curTargetVelocity);
        telemetry.addData("Current velocity", "%.2f", curVelocity);
        telemetry.addData("Error", "%.2f", error);
        telemetry.addLine("------------------------------");
        telemetry.addData("Tuning P", "%.4f (D-Pad I/D)", P);
        telemetry.addData("Tuning F", "%.4f (D-Pad :/R)", F);
        telemetry.addData("Step Size", "%.4f (B Button)", stepSizes[stepIndex]);
    }
}

