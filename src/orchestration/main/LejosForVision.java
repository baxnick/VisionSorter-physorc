package orchestration.main;

import orchestration.LordSupreme;
import orchestration.goal.Goal;
import orchestration.goal.LineGoal;
import orchestration.object.BallColor;


import lejos.geom.Line;

public class LejosForVision {
	public static void main(String[] args)
	{
		try
		{
			Goal eastGoal = new LineGoal("EG", BallColor.BLUE, new Line(1130, 920, 1130, 980), 0);
			Goal westGoal = new LineGoal("WG", BallColor.RED, new Line(70, 990, 70, 1030), 180);
			
			LordSupreme hisHoliness = new LordSupreme();
			
			hisHoliness.overlord.announceGoal(westGoal);
			hisHoliness.overlord.announceGoal(eastGoal);
			
			hisHoliness.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
