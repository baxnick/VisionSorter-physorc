package orchestration;

import java.io.IOException;

import physical.GripperBot;
import physical.GripperBotImpl;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

public class HotBotWatch implements Runnable {
	private LordSupreme parent;
	
	public HotBotWatch(LordSupreme parent)
	{
		this.parent = parent;
	}
	
	@Override
	public void run() {
		NXTComm searchComm = null;
		try {
			searchComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		} catch (NXTCommException e1) {
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
							NXTComm recruitComm = NXTCommFactory
									.createNXTComm(NXTCommFactory.BLUETOOTH);
							recruitComm.open(match);
							GripperBot recruitBot = GripperBotImpl
									.standardGripper(match.name, recruitComm);
							Avatar newRecruit = new Avatar(parent, recruitBot);
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
				Thread.sleep(3000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
