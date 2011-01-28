package orchestration.path;

import java.util.ArrayList;
import java.util.List;

import lejos.geom.Point;

class SimpleRoute implements Route
{
	private List<Point> points = new ArrayList<Point>();
	
	private RouteMode mode;
	private PathPlanner planner;
	
	private int progress = 0;
	
	SimpleRoute(PathPlanner planner, RouteMode mode)
	{
		this.mode = mode;
	}
	
	SimpleRoute(PathPlanner planner, RouteMode mode, Point target)
	{
		this(planner, mode);
	}
	
	@Override
	public RouteMode mode()
	{
		return mode;
	}

	@Override
	public boolean areWeThereYet()
	{
		return (!mode.success() || progress >= points.size()); 
	}

	public Point backTrack()
	{
		progress--;
		if (progress < 0) progress = 0;
		return points.get(progress);
	}
	
	@Override
	public Point next()
	{
		return points.get(progress++);
	}

	@Override
	public void discard()
	{
		planner.discardRoute(this);
	}
}
