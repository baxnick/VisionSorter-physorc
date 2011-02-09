package orchestration.main;

import orchestration.Coordinator;
import orchestration.goal.Goal;
import orchestration.goal.LineGoal;
import orchestration.object.BallColor;

import lejos.geom.Line;

/**
 * 
 * @author baxnick
 *
 */
public class LejosForVision
{
	public static void main(String[] args)
	{
		try
		{
			Coordinator hisHoliness = new Coordinator();
			hisHoliness.start();
		}
		catch (Exception e)
		{
			System.out.println("TOP LEVEL EXCEPTION");
			e.printStackTrace();
		}

	}
}
