package physical.navigation.commands.nav;

import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;
import lejos.robotics.Pose;

public class CmdPose extends NavigatorCommand
{
	private Pose pose = null;
	
	public CmdPose()
	{
		this.setProperties(CommandPriority.READ, false, true);
	}
	
	public Pose getPose()
	{
		return pose;
	}

	@Override
	public void execute()
	{
		pose = nav.getPose();
	}
}
