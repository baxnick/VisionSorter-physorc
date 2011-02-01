package strategy;

import physical.GripperBot;
import physical.Helper;
import lejos.geom.Point;


public class FetchBallBehaviour implements BotStrategy{
	private Point target;
	
	private final float APPROACH_SPEED = 50.0f; // speed in mm/s
	private final float TURN_SPEED = 20.0f; // speed in deg/s
	private final int PREEMPTIVE_GRIP_TIME = 2000;
	
	public FetchBallBehaviour(Point target)
	{
		this.target = target;
	}
	
	public void execute(GripperBot bot) throws InterruptedException
	{
		bot.getNav().setMoveSpeed(APPROACH_SPEED);
		bot.getNav().setTurnSpeed(TURN_SPEED);
		
		bot.getNav().stop();
		bot.getNav().rotateTo(bot.getNav().angleTo(target.x, target.y), true);
		bot.getGrip().release();
		
		while (bot.getNav().isMoving())
		{
			Thread.yield();
			if (Thread.interrupted()) throw new InterruptedException();
		}
		
		float distance = bot.getNav().distanceTo(target.x, target.y)* 1.2f;
		
		bot.getNav().travel(distance, true);
		
		try {
			Thread.sleep((int)Math.max(0, (int)(1000. * distance / APPROACH_SPEED) - PREEMPTIVE_GRIP_TIME));
		} catch (InterruptedException e) {
			return;
		}
		
		bot.getGrip().grip();
		
		while (bot.getNav().isMoving())
		{
			Thread.yield();
			if (Thread.interrupted()) throw new InterruptedException();
		}	
		bot.getNav().setMoveSpeed(bot.getConfig().operatingSpeed);
	}
}
