package orchestration.path;

import lejos.geom.Point;

public interface Route
{
	RouteMode mode();
	boolean areWeThereYet();
	Point next();
	Point backTrack();
	void discard();
}
