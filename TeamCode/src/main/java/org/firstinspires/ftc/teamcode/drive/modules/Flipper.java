package org.firstinspires.ftc.teamcode.drive.modules;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Flipper extends AbstractModule
{
    private Servo twistServo = null;
    private DcMotor extensionMotor = null;

    public Flipper( HardwareMap hardwareMap,
                    Telemetry telemetry ) {
        super( telemetry );
        initObjects( hardwareMap );
        initState();
    }

    private void initObjects( HardwareMap hardwareMap )
    {
        twistServo = hardwareMap.get( Servo.class, "twistServo" );
        extensionMotor = hardwareMap.get( DcMotor.class, "extensionMotor" );
    }

    private void initState()
    {
        twistServo.setDirection( Servo.Direction.REVERSE );
        setTwistPosition( TwistPosition.LEFT );

        extensionMotor.setDirection( DcMotorSimple.Direction.FORWARD );
        extensionMotor.setMode( DcMotor.RunMode.RUN_USING_ENCODER );
        extensionMotor.setZeroPowerBehavior( DcMotor.ZeroPowerBehavior.FLOAT );
        setExtensionMotorSpeed( MotorPower.ZERO );
    }

    public enum MotorPower {
        HIGH( 100 ),
        MEDIUM( 50 ),
        LOW( 10 ),
        ZERO( 0 );

        MotorPower( int value )
        { this.value = value; }

        public final int value;
    }

    public void stop()
    { extensionMotor.setPower( 0 ); }

    public void printTelemetry() {}

    public void setExtensionMotorSpeed( MotorPower power )
    {
        if (extensionMotor == null)
        { return; }
        extensionMotor.setPower(power.value);
    }

    public enum TwistPosition {
        LEFT( -100 ),
        RIGHT( 200 );

        TwistPosition( double value )
        { this.value = value; }

        public final double value;
    }

    public void setTwistPosition( TwistPosition position )
    {
        if (twistServo == null)
        { return; }

        twistServo.setPosition(position.value);
    }
}
