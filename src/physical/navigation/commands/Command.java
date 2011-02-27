package physical.navigation.commands;

public abstract class Command implements Comparable<Command>
{
	private Callback caller;
	private CommandPriority priority;
	private boolean uniquity;
	private boolean interruptibility;
	
	public Command()
	{
		this.caller = null;
		this.priority = CommandPriority.LOW;
		this.uniquity = false;
		this.interruptibility = false;
	}
	
	public void setCaller(Callback caller)
	{
		this.caller = caller;
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
	
	public boolean isInterruptibile()
	{
		return interruptibility;
	}
	
	protected void setProperties(CommandPriority priority, boolean uniquity, boolean interruptibility)
	{
		this.priority = priority;
		this.uniquity = uniquity;
		this.interruptibility = interruptibility;
	}
	
	public abstract void execute();
	public void finish()
	{
		if (caller != null)
		{
			caller.callback(this);
		}
	}
	
	@Override
	public int compareTo(Command o)
	{
		return priority.compareTo(o.getPriority());
	}
}
