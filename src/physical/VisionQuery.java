package physical;

import lejos.geom.Point;

public interface VisionQuery
{
	boolean needsVision();
	Point visionPoint();
}
