package orchestration.path;

import lejos.geom.Point;
import physical.BetterNavigator;
import physical.GripperBot;

public class RouteMaker
{
	private PathPlanner planner;
	private BetterNavigator nav;
	private GripperBot bot;
	private String name;
	
	public RouteMaker(PathPlanner planner, GripperBot bot, String name)
	{
		this.planner = planner;
		this.name = name;
		this.bot = bot;
		this.nav = bot.getNav();
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
				nav.setTurnSpeed(bot.getConfig().rotationSpeed);
				nav.setMoveSpeed(bot.getConfig().operatingSpeed);
				nav.goTo(next.x, next.y);
				Thread.yield();
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
