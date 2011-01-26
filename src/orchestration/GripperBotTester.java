package orchestration;

import java.io.IOException;
import java.util.List;

import orchestration.goal.Goal;
import orchestration.object.Ball;


import lejos.geom.Point;
import lejos.robotics.Pose;
import physical.GripperBot;
import strategy.FetchBallBehaviour;

public class GripperBotTester {
	private GripperBot bot;
	public List<Point> targets;
	
	public GripperBotTester(GripperBot bot)
	{
		this.bot = bot;
	}
	
	public void reset()
	{
		bot.recalibrate();
	}
	
	public void test(Ball target, Goal goal)
	{
	}
	
	public void goHome()
	{
		try
		{
		bot.getNav().goTo(bot.getConfig().trackWidth / 2, 10);
		}
		catch(InterruptedException e){}
	}
	
	public void victoryChime() throws IOException
	{
		for (int i = 0; i < 20; i++)
		{
			bot.getCommand().playTone(3000 + i * 100, 200);
		}
		
		for (int i = 20; i >= 0; i--)
		{
			bot.getCommand().playTone(3000 + i * 100, 200);
		}
	}
	
	public void showMeWhere(Point p) throws IOException
	{
		try {
			bot.getNav().goToShort(p.x, p.y, 10, false);
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
