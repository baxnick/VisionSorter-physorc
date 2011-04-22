package physical.navigation.commands.nav;

import lejos.geom.Point;
import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;

public class CmdRotateAng extends NavigatorCommand
{
	float angle;
	
	public CmdRotateAng(float angle)
	{
		this.setProperties(CommandPriority.MEDIUM, false, true);
		this.angle = angle;
	}

	@Override
	public void execute() throws InterruptedException
	{
		nav.rotateTo(angle, true);
	}
}
