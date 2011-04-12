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

package orchestration;

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
				(Goal) new LineGoal("EG", BallColor.RED, new Line(1100, 0, 1100, 900),0)
				);
	}
}
