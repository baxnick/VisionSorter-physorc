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

import orchestration.goal.Goal;
import orchestration.object.Ball;

public class ErrandObjectives
{
	private ErrandOverlord overlord;
	private Ball ball;
	private Goal goal;
	private boolean hasBall = false;
	private long firstExpired = 0;
	private long timeout = 20000;
	
	public ErrandObjectives(ErrandOverlord overlord, Ball ball, Goal goal)
	{
		this.overlord = overlord;
		this.ball = ball;
		this.goal = goal;
	}

	public void unExpire()
	{
		firstExpired = 0;
	}
	
	public void setExpiryTimeout(long millis)
	{
		this.timeout = millis;
	}
	
	public void attemptExpire()
	{
		long now = System.currentTimeMillis();

		if (firstExpired == 0)
		{
			firstExpired = now;
		}
	}
	
	public Goal getGoal()
	{
		return goal;
	}

	public void setHasBall(boolean hasBall)
	{
		this.hasBall = hasBall;
	}
	
	public Ball getBall()
	{
		return ball;
	}
	
	public boolean hasBall()
	{
		return hasBall;
	}
	
	public boolean isExpired()
	{
		if (firstExpired == 0) return false;
		
		long now = System.currentTimeMillis();
		return (now - firstExpired) > timeout;
	}
	
	public String toString()
	{
		return ball.toString() + " to " + goal.toString();
	}
}
