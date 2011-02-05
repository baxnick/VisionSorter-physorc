package strategy;

public class FetchBallConfig
{
	public float moveSpeedFactor = 0.5f; // multiplier to base speed
	public float turnSpeedFactor = 0.5f; // multiplier to base turn speed
	public float overshoot = 1.2f; // multiplies distance to ball from current location
	public int preEmptiveGripTime = 1000; // ms
	public float allowedHeadingError = 1.5f; // degrees to either side
}
