package physical.navigation.commands.nav;

import lejos.geom.Point;
import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;

public class CmdDistanceTo extends NavigatorCommand
{
	private Point target;
	private float distance;
	
	public CmdDistanceTo(Point target)
	{
		this.setProperties(CommandPriority.READ, false, true);
		this.target = target;
	}
	
	public float getDistance()
	{
		return distance;
	}

	@Override
	public void execute()
	{
		distance = nav.distanceTo(target.x, target.y);
	}
}
