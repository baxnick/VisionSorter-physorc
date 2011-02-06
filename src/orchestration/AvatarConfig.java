package orchestration;

import lejos.geom.Point;

public class AvatarConfig
{
	// How long should the robot be able to continue without having a position
	// update from the vision system?
	public int acceptableReckoningTime = 10000; // ms
	
	// How often should position be updated from the vision system,
	// when it is available?
	public int updateFreq = 3000; // ms
	
	// How long must the robot have been still for before a position update
	// will be accepted?
	public long acceptableStillTime = 3500; // ms
	
	// Where is the best position for the robot to be in order to
	// receive information from the vision system?
	public Point visionZone = new Point(600, 900);
}
