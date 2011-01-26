package orchestration.goal;

import orchestration.object.Ball;
import strategy.BotStrategy;
import lejos.geom.Point;


public interface Goal {
	public Point dropPoint(Point currentLocation);
	public float minimumSafeDistance();
	public BotStrategy approachStrategy(Point point);
	public BotStrategy disengageStrategy(Point point);
	public String id();
	public boolean accepts(Ball ball);
}
