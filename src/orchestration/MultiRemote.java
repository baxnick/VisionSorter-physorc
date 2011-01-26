package orchestration;

import java.util.ArrayList;
import java.util.List;

import orchestration.goal.Goal;
import orchestration.goal.LineGoal;
import orchestration.object.Ball;
import orchestration.object.BallColor;


import lejos.geom.Line;
import lejos.geom.Point;
import physical.*;

public class MultiRemote {
	public static void main(String[] args)
	{
		try
		{
			Goal southGoal = new LineGoal("SG", BallColor.BLUE, new Line(200, 30, 1000, 30), -90);
			Goal westGoal = new LineGoal("WG", BallColor.BLUE, new Line(30, 200, 30, 1600), 180);
			Goal northGoal = new LineGoal("NG", BallColor.RED, new Line(200, 1770, 1000, 1770), 90);
			Goal eastGoal = new LineGoal("EG", BallColor.RED, new Line(1170, 200, 1170, 1600), 0);
			
			List<Ball> balls = new ArrayList<Ball>();
			balls.add(new Ball(new Point(196, 1383), BallColor.RED));
			balls.add(new Ball(new Point(611, 972), BallColor.RED));
			balls.add(new Ball(new Point(310, 487), BallColor.BLUE));
			balls.add(new Ball(new Point(889, 546), BallColor.BLUE));
			LordSupreme hisHoliness = new LordSupreme();
			hisHoliness.overlord.announceGoal(northGoal);
			hisHoliness.overlord.announceGoal(southGoal);
			hisHoliness.overlord.announceGoal(westGoal);
			hisHoliness.overlord.announceGoal(eastGoal);
			hisHoliness.overlord.ballsUpdate(balls);
			
			hisHoliness.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
