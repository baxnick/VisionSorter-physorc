package physical;


public class GripperBotConfiguration {
	private Gripper grip = null;
	private String name = null;
	
	public float gripDisplacement = 50.0f; // how far from the center of movement the grip area is located.
	public float rearDisplacement = 145.0f; // how far from the center of movement the rear extends
	public float wheelDiameter = 56.0f; // diameter in mm, is usually written on "official" tyres.
	public float trackWidth = 122.0f; // mm, how wide across the wheels are spaced
	
	public float operatingSpeed = 180.0f; // mm / sec for movement
	public float rotationSpeed = 60.0f; // deg / sec for turning in place
	
	public int leftMotor = 0; // id corresponding to port on the NXT brick
	public int rightMotor = 2; // likewise
	
	public GripperBotConfiguration(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Gripper getGrip()
	{
		return grip;
	}
	
	public void setGrip(Gripper grip)
	{
		this.grip = grip;
	}
}
