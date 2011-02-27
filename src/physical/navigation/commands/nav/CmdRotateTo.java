package physical.navigation.commands.nav;

import lejos.geom.Point;
import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;

public class CmdRotateTo extends NavigatorCommand
{
	Point target;
	
	public CmdRotateTo(Point target)
	{
		this.setProperties(CommandPriority.MEDIUM, false, true);
		this.target = target;
	}

	@Override
	public void execute()
	{
		try
		{
			nav.rotateTo(nav.angleTo(target.x, target.y));
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block BAD
			e.printStackTrace();
		}
	}
}
