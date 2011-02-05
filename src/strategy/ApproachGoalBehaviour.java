package strategy;

import physical.GripperBot;
import lejos.geom.Point;

public class ApproachGoalBehaviour implements BotStrategy {
	private static final int APPROACH_SPEED = 100;
	private Point goalLocation;
	private float preferredHeading;
	
	public ApproachGoalBehaviour(Point location, float preferredHeading)
	{
		this.goalLocation = location;
		this.preferredHeading = preferredHeading;
	}
	
	public void execute(GripperBot bot) throws InterruptedException
	{			
		bot.getNav().setMoveSpeed(APPROACH_SPEED);
		bot.getNav().stop();
		bot.getNav().goTo(goalLocation.x, goalLocation.y);
		bot.getNav().rotateTo(preferredHeading);
		bot.getGrip().release();
		bot.getNav().setMoveSpeed(bot.getConfig().operatingSpeed);
	}
}
