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
import orchestration.object.BotLocationProvider;
import orchestration.path.PathPlanner;
import orchestration.path.RouteMaker;
import physical.GripperBot;
import physical.navigation.commands.nav.CmdRotate;
import lejos.geom.Point;

/**
 * An Errand represents the intent of an avatar to deliver a particular ball 
 * to a particular obj.getGoal(). Furthermore, it keeps an internal state machine of the 
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
	private GripperBot bot;
	protected RouteMaker router;
	private ErrandConfig cfg = new ErrandConfig();

	private ErrandObjectives obj;
	private ErrandState state;
	
	private boolean taskActive = true;

	public Errand(PathPlanner planner, Avatar avatar, ErrandObjectives objectives)
	{
		this.planner = planner;
		this.avatar = avatar;

		this.obj = objectives;
		obj.setExpiryTimeout(cfg.expiryAllowance);
		
		this.state = new FetchingState();
	}

	public void assignBot(GripperBot bot)
	{
		this.bot = bot;
		router = new RouteMaker(planner, bot.getNav(), avatar.getName());
	}

	private ErrandState whatToDoNext()
	{
		// STATE DECISION TABLE
		ErrandState nextState = state;
		
		if (!state.isFinished())
		{
			nextState = state;
		}
		else if (state.getClass() == FetchingState.class)
		{
			if (state.isFinished()) nextState = new ReturningState();
		}
		else if (state.getClass() == ReturningState.class)
		{
			if (state.isFinished()) nextState = new EndState();
		}
		
		return nextState;
	}

	private ErrandState stateBlip()
	{
		ErrandState nextState = state;
		
		if (haltFlag)
		{
			if (state.getClass() == DelayState.class)
			{
				System.out.println("WARNING: Delay issued while already in DelayState");
			}
			else
			{
				haltFlag = false;
				nextState = new DelayState(state);
			}
		}
		else if (resumeFlag)
		{
			resumeFlag = false;
			if (state.getClass() != DelayState.class)
			{
				System.out.println("WARNING: Resume issued while not in DelayState");
			}
			else
			{
				DelayState delay = (DelayState) state;
				nextState = delay.resumeState;
			}
		}
		else if (state.getClass() != VisionState.class && avatar.needsVision())
		{
			nextState = new VisionState(state);
		}
		else if (state.getClass() == VisionState.class && !avatar.needsVision())
		{
			VisionState vision = (VisionState) state;
			nextState = vision.resumeState;
		}
		
		return nextState;
	}
	
	private void handleStateInterruptibly(ErrandState theState)
	{
		StateThread stateThread = new StateThread(theState);
		stateThread.run();
		
		while (!theState.isFinished())
		{
			if (haltFlag || resumeFlag)
			{
				stateThread.interrupt();
				stateThread.destroy();
				
				bot.getNav().shutdown();
				break;
			}
		}
	}
	
	public void fulfil()
	{
		while (taskActive)
		{
			handleStateInterruptibly(state);

			ErrandState lastState = state;
			
			state = whatToDoNext();
			state = stateBlip();
			
			if (lastState != state)
			{
				System.out.println(avatar.getName() + " entering state: " + state.getClass().getSimpleName());
			}
			
			Thread.yield();
		}
	}
	
	private boolean haltFlag = false;
	public synchronized void halt()
	{
		haltFlag = true;
	}
	
	private boolean resumeFlag = false;
	public synchronized void resume()
	{
		resumeFlag = true;
	}

	public String toString()
	{
		return obj.toString();
	}

	public void reconfigure(ErrandConfig config)
	{
		this.cfg = config;
	}
	
	public boolean isCompleted()
	{
		return state.getClass() == EndState.class;
	}

	public boolean isHalted()
	{
		return state.getClass() == DelayState.class;
	}

	public ErrandObjectives objective()
	{
		return obj;
	}
	
	/// The bot is heading to the ball and gripping it
	private class FetchingState extends ErrandState
	{
		@Override
		public void handle() throws InterruptedException
		{
			Point ballLoc = obj.getBall().getLocation();
			router.follow(router.create(ballLoc, cfg.fetchShortDistance));
			obj.getBall().fetch().execute(bot);
			obj.setHasBall(true);
			finish();
		}
	}
	
	/// The bot is returning the gripped ball to a goal
	private class ReturningState extends ErrandState
	{

		@Override
		public void handle() throws InterruptedException
		{					
			BotLocationProvider mobileProvider = new BotLocationProvider(bot);
			obj.getBall().updateLocation(mobileProvider);
	
			Point dropLoc = obj.getGoal().dropPoint(bot.location());
	
			router.follow(router.create(dropLoc, bot.safeDistance(obj.getGoal().minimumSafeDistance())));
			obj.getGoal().approachStrategy(dropLoc).execute(bot);
			obj.getBall().updateLocation(mobileProvider.fixLocation());
			obj.getGoal().disengageStrategy(dropLoc).execute(bot);
			finish();
		}
	}
	
	/// The bot is moving to a location where vision is present
	private class VisionState extends ErrandState
	{
		protected ErrandState resumeState;
		
		private VisionState(ErrandState resumeState)
		{
			this.resumeState = resumeState;
		}
		
		@Override
		public void handle() throws InterruptedException
		{
			Point target = avatar.getVision().visionPoint();
			router.follow(router.create(target));

			while (avatar.needsVision())
			{
				Thread.sleep(cfg.visionWaitTime);
				bot.getNav().BExecute(new CmdRotate(cfg.visionRotationAmount));
			}
			
			finish();
		}
	}
	
	/// The bot is avoiding a collision with another bot
	private class DelayState extends ErrandState
	{
		protected ErrandState resumeState;
		
		private DelayState(ErrandState resumeState)
		{
			this.resumeState = resumeState;
		}
		
		private boolean firstRun = true;
		
		@Override
		public void handle() throws InterruptedException
		{
			if (firstRun)
			{
				System.out.println(avatar.getName() + " is delayed");
				firstRun = false;
			}
			
			finish();
		}
	}
	
	private class EndState extends ErrandState
	{
		private boolean firstRun = true;
		
		@Override
		public void handle() throws InterruptedException
		{
			taskActive = false;
			
			if (firstRun)
			{
				System.out.println(avatar.getName() + " has completed it's task.");
				firstRun = false;
			}
			
			finish();
		}
	}
}
