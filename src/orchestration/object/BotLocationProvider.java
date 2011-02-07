package orchestration.object;

import lejos.geom.Point;
import physical.GripperBot;

public class BotLocationProvider implements LocationProvider
{
	private GripperBot bot;

	public BotLocationProvider(GripperBot bot)
	{
		this.bot = bot;
	}

	@Override
	public Point location()
	{
		return bot.location();
	}

	@Override
	public Point location(Point fromPoint)
	{
		return location();
	}

	public LocationProvider fixLocation()
	{
		return new FixedLocationProvider(location());
	}
}
