package strategy;

import physical.GripperBot;

public class DisengageGoalBehaviour implements BotStrategy
{
	private DisengageGoalConfig cfg = new DisengageGoalConfig();

	public void execute(GripperBot bot) throws InterruptedException
	{
		bot.getNav().setMoveSpeed(bot.getConfig().operatingSpeed * cfg.speedFactor);
		bot.getGrip().release();
		bot.getNav().travel(-bot.safeDistance(0));
		bot.getNav().setMoveSpeed(bot.getConfig().operatingSpeed);
	}

	public void reconfigure(DisengageGoalConfig config)
	{
		this.cfg = config;
	}
}
