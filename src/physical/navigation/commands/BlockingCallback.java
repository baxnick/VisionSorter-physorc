package physical.navigation.commands;

public class BlockingCallback implements Callback
{	
	private boolean commandExecuted = false;
	@Override
	public void callback(Command cmd)
	{
		commandExecuted = true;
	}
	
	public boolean isExecuted()
	{
		return commandExecuted;
	}
}