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

import orchestration.Avatar;
import orchestration.goal.Goal;
import orchestration.object.Ball;
import orchestration.object.BotLocationProvider;
import orchestration.path.PathPlanner;
import orchestration.path.RouteMaker;
import physical.GripperBot;
import physical.navigation.commands.nav.CmdRotate;
import lejos.geom.Point;

/**
 * An Errand represents the intent of an avatar to deliver a particular ball 
 * to a particular goal. Furthermore, it keeps an internal state machine of the 
 * task progress and manages each stage of the task on behalf of the avatar.
 * 
 * Once the task has been finished, the Task will be discarded, and the avatar 
 * will request a new one from the ErrandOverlord.
 * 
 * @author baxnick
 * 
 */
public class Errand
{
	private PathPlanner planner;
	private Avatar avatar;
	private Ball ball;
	private Goal goal;
	private GripperBot bot;
	private ErrandOverlord overlord;
	private ErrandState state;
	private RouteMaker router;
	private ErrandConfig cfg = new ErrandConfig();

	private boolean taskActive = true;
	private boolean hasBall = false;
	private long firstExpired = 0;

	public Errand(ErrandOverlord overlord, PathPlanner planner, Avatar avatar, Ball ball, Goal goal)
	{
		this.planner = planner;
		this.overlord = overlord;
		this.avatar = avatar;
		this.ball = ball;
		this.goal = goal;
		updateState(ErrandState.FETCHING);
	}

	public void assignBot(GripperBot bot)
	{
		this.bot = bot;
		router = new RouteMaker(planner, bot.getNav(), avatar.getName());
	}

	private ErrandState nextState;
	private ErrandState continuationState;

	public void fulfil()
	{
		while (taskActive)
		{
			Thread.yield();
			updateState(nextState);

			if (firstHaltFlag && halted)
			{
				firstHaltFlag = false;
				savedState = state;
				updateState(ErrandState.DELAYED);
			}

			if (state == ErrandState.FETCHING)
			{
				try
				{
					Point ballLoc = ball.getLocation();
					router.follow(router.create(ballLoc, cfg.fetchShortDistance));
					if (halted) continue;
					ball.fetch().execute(bot);
					hasBall = true;
					unExpire();
					if (halted) continue;

					if (avatar.getVision().needsVision())
					{
						continuationState = ErrandState.RETURNING;
						nextState = ErrandState.VISION;
					}
					else
						nextState = ErrandState.RETURNING;
				}
				catch (InterruptedException e)
				{
				}
			}
			else if (state == ErrandState.RETURNING)
			{
				try
				{
					BotLocationProvider mobileProvider = new BotLocationProvider(bot);
					ball.updateLocation(mobileProvider);

					Point dropLoc = goal.dropPoint(bot.location());

					router.follow(router.create(dropLoc, bot.safeDistance(goal.minimumSafeDistance())));
					if (halted) continue;
					goal.approachStrategy(dropLoc).execute(bot);
					if (halted) continue;
					ball.updateLocation(mobileProvider.fixLocation());
					goal.disengageStrategy(dropLoc).execute(bot);
					if (halted) continue;

					if (avatar.getVision().needsVision())
					{
						continuationState = ErrandState.COMPLETED;
						nextState = ErrandState.VISION;
					}
					else
						nextState = ErrandState.COMPLETED;
				}
				catch (InterruptedException e)
				{
				}
			}
			else if (state == ErrandState.VISION)
			{
				try
				{
					Point target = avatar.getVision().visionPoint();
					router.follow(router.create(target));

					Thread.sleep(cfg.visionWaitTime);
					while (avatar.getVision().needsVision())
					{
						bot.getNav().BExecute(new CmdRotate(cfg.visionRotationAmount));
						Thread.sleep(cfg.visionWaitTime);
					}

					nextState = continuationState;
				}
				catch (InterruptedException e)
				{
				}
			}
			else if (state == ErrandState.COMPLETED)
			{
				System.out.println(ball);
				taskActive = false;
				overlord.completeTask(this);
			}
			else if (state == ErrandState.ABANDONED)
			{
				taskActive = false;
				bot.getGrip().release();

				if (avatar.getVision().needsVision())
				{
					continuationState = ErrandState.ABANDONED;
					nextState = ErrandState.VISION;
				}
				else
				{
					overlord.abortTask(this);
				}
			}
			else if (state == ErrandState.DELAYED)
			{
				if (!halted)
				{
					updateState(savedState);
				}
			}
		}
	}

	public Ball getBall()
	{
		return ball;
	}

	public Goal getGoal()
	{
		return goal;
	}

	public void abort()
	{
		updateState(ErrandState.ABANDONED);
	}

	public String toString()
	{
		return ball.toString() + " to " + goal.toString();
	}

	public boolean isCompleted()
	{
		return state == ErrandState.ABANDONED || state == ErrandState.COMPLETED;
	}

	public boolean hasBall()
	{
		return hasBall;
	}

	private boolean halted = false;
	private boolean firstHaltFlag = false;
	private ErrandState savedState;

	private synchronized void updateState(ErrandState state)
	{
		if (isCompleted()) return;
		if (this.state == state) return;
		this.state = state;
		nextState = state;
		String name = "Unknown";
		if (bot != null) name = bot.getConfig().getName();
		System.out.println(name + " state: " + state.toString());
	}

	public synchronized boolean isHalted()
	{
		return state == ErrandState.DELAYED;
	}

	public void unExpire()
	{
		firstExpired = 0;
	}

	public void attemptExpire()
	{
		long now = System.currentTimeMillis();

		if (firstExpired == 0)
			firstExpired = now;
		else
		{
			if ((now - firstExpired) > cfg.expiryAllowance) abort();
		}
	}

	public void reconfigure(ErrandConfig config)
	{
		this.cfg = config;
	}
}
