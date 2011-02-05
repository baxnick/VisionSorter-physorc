package strategy;

import physical.BetterNavigator;
import physical.GripperBot;
import physical.Helper;
import lejos.geom.Point;
import lejos.robotics.Pose;


public class FetchBallBehaviour implements BotStrategy{
	private Point target;
	
	private final float APPROACH_SPEED = 50.0f; // speed in mm/s
	private final float TURN_SPEED = 20.0f; // speed in deg/s
	private final int PREEMPTIVE_GRIP_TIME = 1000;
	
	public FetchBallBehaviour(Point target)
	{
		this.target = target;
	}
	
	private static final float headingError = 1.5f;
	public void execute(GripperBot bot) throws InterruptedException
	{
		BetterNavigator botNav = bot.getNav();
		botNav.setMoveSpeed(APPROACH_SPEED);
		botNav.setTurnSpeed(TURN_SPEED);
		
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
		} while (Math.abs(botPose.getHeading() - ballHeading) > headingError);
		
		float distance = botNav.distanceTo(target.x, target.y)* 1.2f;
		
		botNav.travel(distance, true);
		
		try {
			Thread.sleep((int)Math.max(0, (int)(1000. * distance / APPROACH_SPEED) - PREEMPTIVE_GRIP_TIME));
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
	}
}
