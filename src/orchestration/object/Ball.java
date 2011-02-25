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

package orchestration.object;

import strategy.FetchBallBehaviour;
import lejos.geom.Point;

public class Ball
{
	private BallColor color;
	private LocationProvider location;
	private boolean taken = false;

	public Ball(Point location, BallColor color)
	{
		this.color = color;
		this.location = new FixedLocationProvider(location);
	}

	public void updateLocation(LocationProvider provider)
	{
		this.location = provider;
	}

	public FetchBallBehaviour fetch()
	{
		return new FetchBallBehaviour(location.location());
	}

	public Point getLocation()
	{
		return location.location();
	}

	public BallColor getColor()
	{
		return color;
	}

	public String toString()
	{
		Point loc = location.location();
		return color.toString() + " ball (" + loc.x + ", " + loc.y + ")";
	}

	public void setTaken(boolean newStatus)
	{
		taken = newStatus;
	}

	public boolean isTaken()
	{
		return taken;
	}
}
