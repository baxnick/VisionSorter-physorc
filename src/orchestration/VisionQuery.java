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

/**
 * Provides a separate interface for querying vision. Not strictly necessary,
 * but I wrote it anyway.
 * 
 * @author baxnick
 * 
 */
public interface VisionQuery
{
	// Return whether or not vision is needed at this time
	boolean needsVision();

	// Returns the location that should be visited if vision is needed
	Point visionPoint();
}
