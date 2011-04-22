package physical.navigation.commands.nav;

import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;

public class CmdRotate extends NavigatorCommand
{
	float amount;
	
	public CmdRotate(float amount)
	{
		setProperties(CommandPriority.MEDIUM, false, true);
		this.amount = amount;
	}

	@Override
	public void execute() throws InterruptedException
	{
		nav.rotate(amount, true);
		waitForMovementEnd();
	}
}
