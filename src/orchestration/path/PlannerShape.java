package orchestration.path;

import java.awt.Point;
import java.awt.Polygon;

public interface PlannerShape
{
	PlannerShape rotateToward(Point pt);

	PlannerShape rotateBy(float degrees);

	PlannerShape rotateTo(float degrees);

	PlannerShape moveTo(Point pt);

	Point center();

	Polygon getPolygon();

	boolean collidesWith(PlannerShape shape);
}
