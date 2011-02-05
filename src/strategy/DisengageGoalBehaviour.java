package strategy;

import physical.GripperBot;
import lejos.geom.Point;


public class DisengageGoalBehaviour implements BotStrategy {
	
	private final float DISENGAGE_SPEED = 150.0f; // speed in mm/s
	
	public void execute(GripperBot bot) throws InterruptedException
	{
		bot.getNav().setMoveSpeed(DISENGAGE_SPEED);
		bot.getGrip().release();
		bot.getNav().travel(-bot.safeDistance(0));
		bot.getNav().setMoveSpeed(bot.getConfig().operatingSpeed);
	}
}
