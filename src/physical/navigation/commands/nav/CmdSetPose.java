package physical.navigation.commands.nav;

import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;
import lejos.robotics.Pose;

public class CmdSetPose extends NavigatorCommand
{
	private static final long acceptableDelay = 10; //ms
	
	private Pose newPose;
	private long timeReceived;
	private boolean success = false;
	
	public CmdSetPose(Pose newPose)
	{
		this.timeReceived = System.currentTimeMillis();
		this.setProperties(CommandPriority.LOW, true, false);
		this.newPose = newPose;
	}

	public Pose getPose()
	{
		return newPose;
	}
	
	@Override
	public void execute()
	{
		long now = System.currentTimeMillis();
		
		if (now - timeReceived < acceptableDelay)
		{
			nav.setPose(newPose);
			success = true;
		}
	}
	
	public boolean wasSuccessful()
	{
		return success;
	}
}
