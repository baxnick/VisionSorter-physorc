package orchestration;

import java.io.IOException;

import physical.GripperBot;
import physical.GripperBotImpl;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * The HotBotWatch watches the bluetooth network for robots it can connect to. 
 * Whenever it establishes a connection with a new robot, it creates an 
 * implementation of GripperBot, passing it on to Avatar.spawn, which will 
 * begin the Avatar thread after constructing it.
 * 
 * It accesses the Lejos NXT functionality quite directly for an "orchestration" 
 * layer class, it might possibly do to split it into physical/orchestration.
 * 
 * @author baxnick
 * 
 */
public class LiveBotFinder implements Runnable
{
	private static final int RETRY_INTERVAL = 3000; // ms, how long to wait in between bluetooth pings

	private Coordinator parent;

	public LiveBotFinder(Coordinator parent)
	{
		this.parent = parent;
	}

	@Override
	public void run()
	{
		NXTComm searchComm = null;
		try
		{
			searchComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		}
		catch (NXTCommException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true)
		{
			NXTInfo[] matches = null;

			try
			{
				matches = searchComm.search(null, NXTCommFactory.BLUETOOTH);

				for (NXTInfo match : matches)
				{
					try
					{
						if (!parent.isActive(match.name))
						{
							NXTComm recruitComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
							recruitComm.open(match);
							GripperBot recruitBot = GripperBotImpl.standardGripper(match.name, recruitComm);
							Avatar.spawn(parent, recruitBot);
						}
					}
					catch (NXTCommException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			catch (NXTCommException e)
			{
				e.printStackTrace();
			}

			try
			{
				Thread.sleep(RETRY_INTERVAL);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}