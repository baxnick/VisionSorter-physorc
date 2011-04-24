package physical.navigation.commands.nav;

import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;

public class CmdStop extends NavigatorCommand
{
	public CmdStop()
	{
		setProperties(CommandPriority.HIGH, true, false);
	}
	
	@Override
	public void execute() throws InterruptedException
	{
			nav.stop();
			waitForMovementEnd();
	}
	
}
