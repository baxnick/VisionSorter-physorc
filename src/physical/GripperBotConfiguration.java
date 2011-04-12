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

public class GripperBotConfiguration
{
	private Gripper grip = null;
	private String name = null;

	public float gripDisplacement = 50.0f; // how far from the center of movement the grip area is located.
	public float rearDisplacement = 145.0f; // how far from the center of movement the rear extends
	public float wheelDiameter = 56.0f; // diameter in mm, is usually written on "official" tyres.
	public float trackWidth = 120.0f; // mm, how wide across the wheels are spaced

	public float operatingSpeed = 120.0f; // mm / sec for movement
	public float rotationSpeed = 40.0f; // deg / sec for turning in place

	public int leftMotor = 0; // id corresponding to port on the NXT brick
	public int rightMotor = 2; // likewise

	public GripperBotConfiguration(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Gripper getGrip()
	{
		return grip;
	}

	public void setGrip(Gripper grip)
	{
		this.grip = grip;
	}
}
