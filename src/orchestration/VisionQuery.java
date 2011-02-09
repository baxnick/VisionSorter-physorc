package orchestration;

import lejos.geom.Point;

/**
 * Provides a separate interface for querying vision. Not strictly necessary,
 * but I wrote it anyway.
 * 
 * @author baxnick
 * 
 */
public interface VisionQuery
{
	// Return whether or not vision is needed at this time
	boolean needsVision();

	// Returns the location that should be visited if vision is needed
	Point visionPoint();
}
