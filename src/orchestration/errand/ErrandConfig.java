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

package orchestration.errand;

public class ErrandConfig
{

	// How long can the targetted ball be out of sight before the task
	// to fetch it is abandoned?
	public long expiryAllowance = 30000; // ms

	// How long should the bot wait in each orientation while it is waiting
	// to get a vision fix?
	public long visionWaitTime = 4000; // ms

	// How much should the bot should rotate in place each time it fails to get
	// a fix whilst waiting for vision?
	public int visionRotationAmount = 15; // ms

	// How far away should the bot should stop from the targetted ball
	// before the fetch strategy takes over?
	public float fetchShortDistance = 160; // mm
}
