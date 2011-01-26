package orchestration.object;

import lejos.geom.Point;

public class FixedLocationProvider implements LocationProvider
{
	private Point location;
	
	public FixedLocationProvider(Point location)
	{
		this.location = location;
	}
	@Override
	public Point location()
	{
		return location;
	}

	@Override
	public Point location(Point fromPoint)
	{
		return location();
	}

}
