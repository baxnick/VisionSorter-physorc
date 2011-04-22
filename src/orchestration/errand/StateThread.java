package orchestration.errand;

public class StateThread extends Thread
{
	ErrandState stateToThread;
	private boolean finished = false;
	
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
		catch (InterruptedException ex) { }
		finally {finished = true;} // Mission accomplished
		// The exception just needs to escape the handle() method
	}
	
	public boolean isFinished()
	{
		return finished;
	}
}
