/*
 * Physical & Orchestration Components for VisionSorter
 * Copyright (C) 2011, Ben Axnick
 * Ben Axnick <ben@axnick.com.au>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
