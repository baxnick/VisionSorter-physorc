package physical;


public class GripperBotConfiguration {
	private Gripper grip = null;
	private String name = null;
	public float gripDisplacement = 50.0f;
	public float wheelDiameter = 56.0f;
	public float trackWidth = 122.0f;
	public float rearDisplacement = 145.0f;
	public float calibrationOffY = 35.0f;
	public float calibrationOffX = 45.0f;
	public float operatingSpeed = 80.0f;
	public float rotationSpeed = 30.0f; // deg / sec for turning in place
	public int leftMotor = 0;
	public int rightMotor = 2;
	
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
