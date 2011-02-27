package physical.navigation.commands;

public class Command
{
	private final Callback caller;
	private CommandPriority priority;
	private boolean uniquity;
	
	public Command(Callback caller)
	{
		this.caller = caller;
		this.priority = CommandPriority.LOW;
		this.uniquity = false;
	}
	
	public Callback getCaller()
	{
		return caller;
	}
	
	public CommandPriority getPriority()
	{
		return priority;
	}
	
	public boolean isUnique()
	{
		return uniquity;
	}
	
	protected void setProperties(CommandPriority priority, boolean uniquity)
	{
		this.priority = priority;
		this.uniquity = uniquity;
	}
}
