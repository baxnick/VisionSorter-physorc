package physical.navigation.commands;

import lejos.robotics.navigation.Pilot;

public class PilotCommand extends Command
{
	protected Pilot pilot;
	
	public PilotCommand(Pilot pilot, Callback caller)
	{
		super(caller);
		this.pilot = pilot;
	}
}
