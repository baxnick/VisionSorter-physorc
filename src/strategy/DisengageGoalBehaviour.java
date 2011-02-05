package strategy;

import physical.GripperBot;
import lejos.geom.Point;


public class DisengageGoalBehaviour implements BotStrategy<DisengageGoalConfig> {
	private DisengageGoalConfig cfg = new DisengageGoalConfig();
	
	public void execute(GripperBot bot) throws InterruptedException
	{
		bot.getNav().setMoveSpeed(bot.getConfig().operatingSpeed * cfg.speedFactor);
		bot.getGrip().release();
		bot.getNav().travel(-bot.safeDistance(0));
		bot.getNav().setMoveSpeed(bot.getConfig().operatingSpeed);
	}

	@Override
	public void reconfigure(DisengageGoalConfig config)
	{
		this.cfg = config;
	}
}
