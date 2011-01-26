package orchestration.goal;

import java.awt.geom.Point2D;

import orchestration.object.Ball;
import orchestration.object.BallColor;


import physical.GripperBot;
import strategy.ApproachGoalBehaviour;
import strategy.BotStrategy;
import strategy.DisengageGoalBehaviour;


import lejos.geom.Line;
import lejos.geom.Point;
import lejos.robotics.Pose;


public class LineGoal implements Goal {
	private Line line;	
	private float preferredOrientation = 0;
	private String id;
	private BallColor color;
	
	public LineGoal(String id, BallColor color, Line line, float preferredOrientation)
	{
		this.color = color;
		this.id = id;
		this.line = line;
		this.preferredOrientation = preferredOrientation;
	}

	@Override
	public Point dropPoint(Point currentLocation) {
		Point2D dp = DistancePoint.closePoint(line.getP1(), line.getP2(), currentLocation);
		return new Point((float)dp.getX(), (float)dp.getY());
	}

	@Override
	public float minimumSafeDistance() {
		return 0;
	}

	@Override
	public BotStrategy approachStrategy(Point point) {
		return new ApproachGoalBehaviour(point, preferredOrientation);
	}

	@Override
	public BotStrategy disengageStrategy(Point point) {
		return new DisengageGoalBehaviour();
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public boolean accepts(Ball ball)
	{
		return this.color.equals(ball.getColor());
	}
	
	public String toString()
	{
		return id() + "(" + color.toString() + " line goal)";
	}
}
