package orchestration.object;

import strategy.FetchBallBehaviour;
import lejos.geom.Point;

public class Ball {
	private BallColor color;
	private LocationProvider location;
	public Ball(Point location, BallColor color)
	{
		this.color = color;
		this.location = new FixedLocationProvider(location);
	}
	
	public void updateLocation(LocationProvider provider)
	{
		this.location = provider;
	}
	
	public FetchBallBehaviour fetch()
	{
		return new FetchBallBehaviour(location.location());
	}
	
	public Point getLocation()
	{
		return location.location();
	}
	
	public BallColor getColor()
	{
		return color;
	}
	
	public String toString()
	{
		Point loc = location.location();
		return color.toString() + " ball (" + loc.x + ", " + loc.y + ")";
	}
}
