package physical;

import lejos.nxt.TouchSensor;
import lejos.nxt.remote.NXTCommand;


public class Helper {
	public static void PauseBeeper(GripperBot bot, int buttonPort)
	{
		NXTCommand comm = bot.getCommand();
		if (!comm.isOpen()) return;
		
		BeepRunner beeper = new BeepRunner(comm, 9000, 1000, 3000);
		Thread beepThread = new Thread(beeper);
		beepThread.start();
		
		TouchSensor button = new TouchSensor(new RemoteSensorPort(comm, buttonPort));
		
		while(beeper.active && !button.isPressed())
		{
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
		beeper.deactivate();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static class BeepRunner implements Runnable
	{
		public boolean active = true;
		private int tone;
		private int toneDuration;
		private int pauseDuration;
		
		private NXTCommand comm;
		
		public BeepRunner(NXTCommand comm, int tone, int toneDuration, int pauseDuration)
		{
			this.tone = tone;
			this.toneDuration = toneDuration;
			this.pauseDuration = pauseDuration;
			this.comm = comm;
		}
		
		public void run()
		{
			while (active)
			{
				try {
					if (!comm.isOpen()) return;
					comm.playTone(tone, toneDuration);
					Thread.sleep(pauseDuration);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					active = false;
				}
			}
		}
		
		public void deactivate()
		{
			active = false;
		}
	}
}

