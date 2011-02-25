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

import lejos.geom.Point;

public class AvatarConfig
{
	// How long should the robot be able to continue without having a position
	// update from the vision system?
	public int acceptableReckoningTime = 10000; // ms

	// How often should position be updated from the vision system,
	// when it is available?
	public int updateFreq = 3000; // ms

	// How long must the robot have been still for before a position update
	// will be accepted?
	public long acceptableStillTime = 3500; // ms

	// Where is the best position for the robot to be in order to
	// receive information from the vision system?
	public Point visionZone = new Point(600, 900);
}
