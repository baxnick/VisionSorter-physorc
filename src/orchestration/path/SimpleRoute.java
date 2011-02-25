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

import java.util.ArrayList;
import java.util.List;

import lejos.geom.Point;

class SimpleRoute implements Route
{
	private List<Point> points = new ArrayList<Point>();

	private RouteMode mode;
	private PathPlanner planner;

	private int progress = 0;

	SimpleRoute(PathPlanner planner, RouteMode mode)
	{
		this.planner = planner;
		this.mode = mode;
	}

	SimpleRoute(PathPlanner planner, RouteMode mode, Point target)
	{
		this(planner, mode);
		points.add(target);
	}

	@Override
	public RouteMode mode()
	{
		return mode;
	}

	@Override
	public boolean areWeThereYet()
	{
		return (!mode.success() || progress >= points.size());
	}

	public Point backTrack()
	{
		progress--;
		if (progress < 0) progress = 0;
		return points.get(progress);
	}

	@Override
	public Point next()
	{
		return points.get(progress++);
	}

	@Override
	public void discard()
	{
		planner.discardRoute(this);
	}
}
