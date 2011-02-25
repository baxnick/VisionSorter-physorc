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

package physical;

public class OverheadGripperConfig
{
	public boolean reverseDirection;
	public float operatingSpeedMulti;
	public int grippedTach = 205;
	public int releasedTach = 130;
	public float calibrationArc = 250.0f;
	public int calibrationSpeed = 60;
	public int motorId = 1;

	public OverheadGripperConfig()
	{
		reverseDirection = true;
		operatingSpeedMulti = 0.2f;

		grippedTach *= directionMultiplier();
		releasedTach *= directionMultiplier();
	}

	public int directionMultiplier()
	{
		return (reverseDirection == true) ? -1 : 1;
	}

	public int operatingSpeed()
	{
		return (int) (800 * operatingSpeedMulti);
	}
}
