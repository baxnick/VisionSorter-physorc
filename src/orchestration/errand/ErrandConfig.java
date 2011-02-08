package orchestration.errand;

public class ErrandConfig
{

	// How long can the targetted ball be out of sight before the task
	// to fetch it is abandoned?
	public long expiryAllowance = 20000; // ms

	// How long should the bot wait in each orientation while it is waiting
	// to get a vision fix?
	public long visionWaitTime = 4000; // ms

	// How much should the bot should rotate in place each time it fails to get
	// a fix whilst waiting for vision?
	public int visionRotationAmount = 15; // ms

	// How far away should the bot should stop from the targetted ball
	// before the fetch strategy takes over?
	public float fetchShortDistance = 160; // mm
}
