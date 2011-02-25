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

import orchestration.object.Ball;
import strategy.BotStrategy;
import lejos.geom.Point;

/**
 * A Goal is an abstract entity representing some sort of location a gripper
 * bot can release it's object. It must provide a strategy for providing access
 * to the goal in the event that it requires some sort of special maneuver.
 * Otherwise an instance of ApproachGoalBehaviour and DisengageGoalBehaviour
 * should be returned.
 * 
 * @author baxnick
 *
 */
public interface Goal
{
	public Point dropPoint(Point currentLocation);

	/**
	 * How close the bot can get before allowing the approach strategy to take over.
	 */
	public float minimumSafeDistance();

	/**
	 *  A set of instructions to the bot for approaching the goal.
	 *  
	 * @param point The position at which the bot is currently located
	 * @see strategy.ApproachGoalBehaviour
	 */
	public BotStrategy approachStrategy(Point point);

	/**
	 *  A set of instructions to the bot for disengaging the goal.
	 *  
	 * @param point The position at which the bot is currently located
	 * @see strategy.DisengageGoalBehaviour
	 */
	public BotStrategy disengageStrategy(Point point);

	/**
	 * The identifier of the goal for logging, console display etc.
	 */
	public String id();

	/**
	 * Let the caller know if the goal is appropriate for the passed Ball.
	 * 
	 * This is currently used to distinguish balls on the basis of colour.
	 * 
	 * @param ball An abstract ball entity
	 */
	public boolean accepts(Ball ball);
}
