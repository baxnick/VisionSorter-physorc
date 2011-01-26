package physical;

import lejos.nxt.remote.RemoteMotor;


public class OverheadGripperConfig {
	public boolean reverseDirection;
	public float operatingSpeedMulti;
	public int grippedTach = 210;
	public int releasedTach = 130;
	public float calibrationArc = 250.0f;
	public int calibrationSpeed = 60;
	public int motorId = 1;
	
	public OverheadGripperConfig() {
		reverseDirection = true;
		operatingSpeedMulti = 0.2f;
		
		grippedTach *= directionMultiplier();
		releasedTach *= directionMultiplier();
	}
	
	public int directionMultiplier() {
		return (reverseDirection == true) ? -1 : 1;
	}
	
	public int operatingSpeed()
	{
		return (int) (800 * operatingSpeedMulti);
	}
}
