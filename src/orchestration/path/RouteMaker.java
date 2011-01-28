package orchestration.path;

import lejos.geom.Point;
import physical.BetterNavigator;

public class RouteMaker
{
	private PathPlanner planner;
	private BetterNavigator nav;
	private String name;
	
	public RouteMaker(PathPlanner planner, BetterNavigator nav, String name)
	{
		this.planner = planner;
		this.name = name;
		this.nav = nav;
	}
	
	public Route create(Point pt)
	{
		return planner.requestRoute(name, pt);
	}
	
	public Route create(Point pt, float shortAmount)
	{
		return planner.requestRoute(name, pt, shortAmount);
	}
	
	public void follow(Route route) throws InterruptedException
	{
		while (!route.areWeThereYet())
		{
			try
			{
				Point next = route.next();
				nav.goTo(next.x, next.y);
			}
			catch (InterruptedException e)
			{
				route.discard();
				throw e;
			}
		}
		
		route.discard();
	}
}
