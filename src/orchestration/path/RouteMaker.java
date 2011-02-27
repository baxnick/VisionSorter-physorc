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

import lejos.geom.Point;
import physical.GripperBot;
import physical.navigation.NavControl;
import physical.navigation.commands.nav.CmdGoTo;

public class RouteMaker
{
	private PathPlanner planner;
	private NavControl nav;
	private String name;

	public RouteMaker(PathPlanner planner, NavControl nav, String name)
	{
		this.planner = planner;
		this.name = name;
		this.nav = nav;
	}

	public Route create(Point pt)
	{
		return planner.requestRoute(name, pt);
	}

	public Route create(Point pt, float shortAmount)
	{
		return planner.requestRoute(name, pt, shortAmount);
	}

	public void follow(Route route) throws InterruptedException
	{
		while (!route.areWeThereYet())
		{
				Point next = route.next();
				nav.BExecute(new CmdGoTo(next));
				Thread.yield();
		}

		route.discard();
	}
}
