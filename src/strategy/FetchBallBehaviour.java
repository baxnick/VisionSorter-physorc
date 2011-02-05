package strategy;

import physical.BetterNavigator;
import physical.GripperBot;
import lejos.geom.Point;
import lejos.robotics.Pose;


public class FetchBallBehaviour implements BotStrategy
{
	private FetchBallConfig cfg = new FetchBallConfig();
	
	private Point target;
	
	public FetchBallBehaviour(Point target)
	{
		this.target = target;
	}
	
	public void execute(GripperBot bot) throws InterruptedException
	{
		BetterNavigator botNav = bot.getNav();
		float operatingSpeed = bot.getConfig().operatingSpeed * cfg.moveSpeedFactor;
		float rotationSpeed = bot.getConfig().rotationSpeed * cfg.turnSpeedFactor;
		botNav.setMoveSpeed(operatingSpeed);
		botNav.setTurnSpeed(rotationSpeed);
		
		botNav.stop();
		
		Pose botPose;
		float ballHeading;
		do
		{
		botPose = botNav.getPose();
		ballHeading = botNav.angleTo(target.x, target.y);
		
			System.out.println("(PRE) " + bot.getConfig().getName() + " @ " + botPose.getX() + ", " + botPose.getY() +
					" mh: " + botPose.getHeading() + " bh: " + ballHeading);
			
			
			botNav.rotateTo(ballHeading, false);
			botNav.stop();
			bot.getGrip().release();
	
			
			botPose = botNav.getPose();
			System.out.println("(POST) " + bot.getConfig().getName() + " @ " + botPose.getX() + ", " + botPose.getY() +
					" FETCHING ball @ " + target.x + ", " + target.y + " mh: " + botPose.getHeading());
		} while (Math.abs(botPose.getHeading() - ballHeading) > cfg.allowedHeadingError);
		
		float distance = botNav.distanceTo(target.x, target.y)* 1.2f;
		
		botNav.travel(distance, true);
		
		try {
			Thread.sleep((int)Math.max(0, (int)(1000. * distance / operatingSpeed) - cfg.overshoot));
		} catch (InterruptedException e) {
			return;
		}
		
		bot.getGrip().grip();
		
		while (botNav.isMoving())
		{
			Thread.yield();
			if (Thread.interrupted()) throw new InterruptedException();
		}	
		
		botNav.setMoveSpeed(bot.getConfig().operatingSpeed);
		botNav.setTurnSpeed(bot.getConfig().rotationSpeed);
	}

	public void reconfigure(FetchBallConfig config)
	{
		this.cfg = config;
	}
}
