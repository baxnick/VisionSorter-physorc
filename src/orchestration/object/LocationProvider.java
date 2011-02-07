package orchestration.object;

import lejos.geom.Point;

public interface LocationProvider
{
	public Point location();

	public Point location(Point fromPoint);
}
