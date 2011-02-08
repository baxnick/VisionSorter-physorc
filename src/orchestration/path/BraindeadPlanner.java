package orchestration.path;

import java.util.HashMap;
import java.util.Map;

import orchestration.Coordinator;
import lejos.geom.Point;

// Behaves exactly as though there were absolutely no path planning in place.
public class BraindeadPlanner implements PathPlanner
{
	private Coordinator parent;
	private Map<String, PlannerShape> obstacles = new HashMap<String, PlannerShape>();

	public BraindeadPlanner(Coordinator parent)
	{
		this.parent = parent;
	}

	private void updateObstacles()
	{
		obstacles = new HashMap<String, PlannerShape>();
		for (Plannable p : parent.avatars)
		{
			obstacles.put(p.getPlanningName(), p.getPlannerShape());
		}
	}

	public Route requestRoute(String id, Point destination)
	{
		updateObstacles();
		return new SimpleRoute(this, RouteMode.DIRECT, destination);
	}

	@Override
	public Route requestRoute(String id, Point destination, float shortAmount)
	{
		updateObstacles();
		PlannerShape from = obstacles.get(id);
		java.awt.Point awtPt = from.center();
		Point fromPt = new Point(awtPt.x, awtPt.y);

		double angle = angleBetween(fromPt, destination);

		float shortX = (float) Math.cos(angle) * shortAmount;
		float shortY = (float) Math.sin(angle) * shortAmount;

		Point modifiedDestination = new Point(destination.x - shortX, destination.y - shortY);

		return requestRoute(id, modifiedDestination);
	}

	@Override
	public void discardRoute(Route route)
	{
		// TODO Auto-generated method stub

	}

	private static double angleBetween(Point a, Point b)
	{
		float opp = b.x - a.x;
		float adj = b.y - a.y;

		return Math.atan2(adj, opp);
	}

}
