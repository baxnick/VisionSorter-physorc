package physical.navigation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lejos.robotics.Pose;
import physical.navigation.commands.BlockingCallback;
import physical.navigation.commands.CommandPriority;
import physical.navigation.commands.NavigatorCommand;
import physical.navigation.commands.nav.CmdPose;

public class NavControl
{
	private Thread controlThread;
	private Thread readerThread;
	
	private BetterNavigator nav;
	private CommandQueue<NavigatorCommand> commands = new CommandQueue<NavigatorCommand>();
	private CommandQueue<NavigatorCommand> reads = new CommandQueue<NavigatorCommand>();
	private NavigatorCommand currentCmd = null;
	private boolean active = true;
	
	public NavControl(BetterNavigator nav)
	{
		this.nav = nav;
		controlThread = new Thread(new Control());
		controlThread.start();
		
		readerThread = new Thread(new Reader());
		readerThread.start();
	}
	
	public void BExecute(NavigatorCommand cmd)
	{
		BlockingCallback bc = new BlockingCallback();
		cmd.setCaller(bc);
		
		Execute(cmd);
		
		while (!bc.isExecuted())
		{
			Thread.yield();
		}
	}
	
	public void Execute(NavigatorCommand cmd)
	{
		if (cmd.getPriority() == CommandPriority.READ)
			reads.enqueue(cmd);
		else
			commands.enqueue(cmd);
	}

	public void shutdown()
	{
		this.active = false;
	}
	
	public Pose getPose()
	{
		CmdPose cPose = new CmdPose();
		BExecute(cPose);
		
		return nav.getPose();
	}
	
	private Lock atom = new ReentrantLock();
	
	private class Control implements Runnable
	{
		@Override
		public void run()
		{
			while (active)
			{
				Thread.yield();
				
				atom.lock();
				currentCmd = commands.nextCommand();
				atom.unlock();
				
				if (currentCmd == null) continue;
				
				System.out.println("PROCESSING COMMAND " + currentCmd);
				currentCmd.setNavigator(nav);
				currentCmd.execute();
				currentCmd.finish();
			}
		}
	}
	
	private class Reader implements Runnable
	{
		@Override
		public void run()
		{
			while (active)
			{
				Thread.yield();
				NavigatorCommand cmd = reads.nextCommand();
				if (cmd == null) continue;

				atom.lock();
				if (currentCmd != null && currentCmd.isInterruptibile() == false)
				{
					reads.enqueue(cmd);
					atom.unlock();
					continue;
				}
				
				atom.unlock();

				System.out.println("PROCESSING READ " + currentCmd);
				cmd.setNavigator(nav);
				cmd.execute();
				cmd.finish();
			}
		}
	}
}
