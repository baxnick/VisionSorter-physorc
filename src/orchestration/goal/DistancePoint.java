package orchestration.goal;

/*
 * DistancePoint, calculate distance to line
 * Copyright (C) 2008 Pieter Iserbyt <pieter.iserbyt@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Example implementation of "Minimum Distance between a Point and a Line" as
 * described by Paul Bourke on
 * See http://local.wasp.uwa.edu.au/~pbourke/geometry/pointline/.
 */
import java.awt.geom.Point2D;

public class DistancePoint
{
	/**
	 * Returns the distance of p3 to the segment defined by p1,p2;
	 * 
	 * @param p1
	 *           First point of the segment
	 * @param p2
	 *           Second point of the segment
	 * @param p3
	 *           Point to which we want to know the distance of the segment defined by p1,p2
	 * @return The distance of p3 to the segment defined by p1,p2
	 */
	public static Point2D closePoint(Point2D p1, Point2D p2, Point2D p3)
	{

		final double xDelta = p2.getX() - p1.getX();
		final double yDelta = p2.getY() - p1.getY();

		if ((xDelta == 0) && (yDelta == 0))
		{
			throw new IllegalArgumentException("p1 and p2 cannot be the same point");
		}

		final double u = ((p3.getX() - p1.getX()) * xDelta + (p3.getY() - p1.getY()) * yDelta)
				/ (xDelta * xDelta + yDelta * yDelta);

		final Point2D closestPoint;
		if (u < 0)
		{
			closestPoint = p1;
		}
		else if (u > 1)
		{
			closestPoint = p2;
		}
		else
		{
			closestPoint = new Point2D.Double(p1.getX() + u * xDelta, p1.getY() + u * yDelta);
		}

		return closestPoint;
	}

}
