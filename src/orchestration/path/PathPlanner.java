package orchestration.path;

import lejos.geom.Point;

public interface PathPlanner
{
	Route requestRoute(String id, Point destination);

	Route requestRoute(String id, Point destination, float shortAmount);

	void discardRoute(Route route);
}
