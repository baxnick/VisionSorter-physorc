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

package orchestration.goal;

import java.awt.geom.Point2D;

import orchestration.object.Ball;
import orchestration.object.BallColor;

import strategy.ApproachGoalBehaviour;
import strategy.BotStrategy;
import strategy.DisengageGoalBehaviour;

import lejos.geom.Line;
import lejos.geom.Point;

public class LineGoal implements Goal
{
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
	public Point dropPoint(Point currentLocation)
	{
		Point2D dp = DistancePoint.closePoint(line.getP1(), line.getP2(), currentLocation);
		return new Point((float) dp.getX(), (float) dp.getY());
	}

	@Override
	public float minimumSafeDistance()
	{
		return 0;
	}

	@Override
	public BotStrategy approachStrategy(Point point)
	{
		return new ApproachGoalBehaviour(point, preferredOrientation);
	}

	@Override
	public BotStrategy disengageStrategy(Point point)
	{
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
