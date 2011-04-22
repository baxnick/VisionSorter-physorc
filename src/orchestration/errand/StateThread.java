package orchestration.errand;

public class StateThread extends Thread
{
	ErrandState stateToThread;
	
	public StateThread(ErrandState stateToThread)
	{
		this.stateToThread = stateToThread;
	}
	
	@Override
	public void run()
	{
		try
		{
			stateToThread.handle();
		}
		catch (InterruptedException ex) { } // Mission accomplished
		// The exception just needs to escape the handle() method
	}
}
