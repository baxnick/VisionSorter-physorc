package physical.navigation.commands;

import physical.navigation.BetterNavigator;

public class NavigatorCommand extends Command
{
	protected BetterNavigator nav;
	
	public NavigatorCommand(BetterNavigator nav, Callback caller)
	{
		super(caller);
		this.nav = nav;
	}
}
