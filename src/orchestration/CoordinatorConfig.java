package orchestration;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

import orchestration.goal.Goal;
import orchestration.goal.LineGoal;
import orchestration.object.BallColor;

import lejos.geom.Line;
import lejos.geom.Point;

public class CoordinatorConfig
{
	public Point strictBoundary;
	public Point[] playfield;
	public List<Goal> goals;
	
	public CoordinatorConfig()
	{
		strictBoundary = new Point(1200, 1800);
		playfield = new Point[]{
				new Point(60, 60),
				new Point(1140, 1740)};
		
		goals = Arrays.asList(
				(Goal) new LineGoal("EG", BallColor.BLUE, new Line(1130, 920, 1130, 980), 0),
				(Goal) new LineGoal("WG", BallColor.RED, new Line(70, 990, 70, 1030), 180)
				);
	}
}
