package physical.navigation.commands.nav;

import lejos.geom.Point;
import lejos.robotics.Pose;
import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;

public class CmdAngleTo extends NavigatorCommand
{
	private Point target;
	private float angle;
	
	public CmdAngleTo(Point target)
	{
		this.setProperties(CommandPriority.READ, false, true);
		this.target = target;
	}
	
	public float getAngle()
	{
		return angle;
	}

	@Override
	public void execute()
	{
		angle = nav.angleTo(target.x, target.y);
	}
}
