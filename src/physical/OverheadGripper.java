package physical;

import lejos.robotics.TachoMotor;

public class OverheadGripper implements Gripper
{
	private OverheadGripperConfig config;
	private GripperState state = GripperState.INITIAL;
	private TachoMotor motor = null;

	public OverheadGripper(OverheadGripperConfig configuration)
	{
		this.config = configuration;
	}

	public void setMotor(TachoMotor motor)
	{
		this.motor = motor;
	}

	public void calibrate()
	{
		state = GripperState.CALIBRATING;
		motor.setSpeed(config.calibrationSpeed);

		if (config.reverseDirection)
			motor.forward();
		else
			motor.backward();

		// TODO come up with a stall detection method
		// Currently attempts to rotate backward X degrees at speed Y, which
		// should give a known starting point for the motor
		try
		{
			Thread.sleep((int) (1000 * (config.calibrationArc / config.calibrationSpeed)));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		motor.resetTachoCount();
		motor.setSpeed(config.operatingSpeed());
		motor.rotateTo(config.releasedTach, false);
		state = GripperState.RELEASED;
	}

	@Override
	public void grip()
	{
		if (state == GripperState.GRIPPING || state == GripperState.GRIPPED) return;

		if (state == GripperState.RELEASING || state == GripperState.CALIBRATING) return;

		state = GripperState.GRIPPING;
		motor.rotateTo(config.grippedTach, false);
		state = GripperState.GRIPPED;
	}

	private final int SLOW_RELEASE_ARC = -30;

	@Override
	public void release()
	{
		if (state == GripperState.RELEASING || state == GripperState.RELEASED) return;

		if (state == GripperState.GRIPPING || state == GripperState.CALIBRATING) return;

		state = GripperState.RELEASING;

		motor.setSpeed(config.operatingSpeed() / 4);
		motor.rotate(SLOW_RELEASE_ARC * config.directionMultiplier(), false);
		motor.setSpeed(config.operatingSpeed());

		// TODO Auto-generated method stub
		motor.rotateTo(config.releasedTach, false);

		state = GripperState.RELEASED;
	}

	public OverheadGripperConfig getConfig()
	{
		return config;
	}
}
