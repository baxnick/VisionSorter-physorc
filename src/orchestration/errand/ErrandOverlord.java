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

import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import orchestration.Avatar;
import orchestration.Coordinator;
import orchestration.goal.Goal;
import orchestration.object.Ball;
import orchestration.object.BallColor;

import lcm.lcm.LCM;
import lcm.lcm.LCMDataInputStream;
import lcm.lcm.LCMSubscriber;
import lcmtypes.balls_t;
import lcmtypes.ball_t;
import lejos.geom.Point;

/**
 * ErrandOverlord monitors the positioning of balls and goals placed on the
 * table, fulfilling requests by avatars by instantiating Task objects in
 * response whenever new balls become available.
 * 
 * @author baxnick
 * 
 */
public class ErrandOverlord
{
	private Lock supplicantLock = new ReentrantLock();
	private Lock compTaskLock = new ReentrantLock();

	private List<Avatar> supplicants = new Vector<Avatar>();
	private List<Errand> completedTasks = new Vector<Errand>();
	private List<Errand> tasks = new Vector<Errand>();
	private List<Ball> freeBalls = new Vector<Ball>();
	private List<Goal> goals = new Vector<Goal>();

	private Coordinator parent;

	public ErrandOverlord(Coordinator parent)
	{
		this.parent = parent;

		parent.lcm.subscribe("BALL", new BallSubscriber());
	}

	public void requestDuty(Avatar soldier)
	{
		System.out.println(soldier.getName() + " is waiting for a task.");
		System.out.print(tasks.size() + " active tasks. ");
		System.out.println(freeBalls.size() + " free balls. ");

		supplicantLock.lock();
		supplicants.add(soldier);
		supplicantLock.unlock();
	}

	public Errand createDuty(Avatar soldier)
	{
		System.out.println(soldier.getName() + " is receiving a task.");
		System.out.print(tasks.size() + " active tasks. ");
		System.out.println(freeBalls.size() + " free balls. ");

		Point avatarLoc = soldier.location();

		Ball nearestBall = findNearestBall(avatarLoc);
		Goal nearestGoal = findBestGoal(avatarLoc, nearestBall);

		Errand newTask = new Errand(this, parent.planner, soldier, nearestBall, nearestGoal);
		takeBall(nearestBall);
		tasks.add(newTask);

		return newTask;
	}

	public void takeBall(Ball ball)
	{

		freeBalls.remove(ball);

	}

	public Ball findNearestBall(Point avatarLoc)
	{

		if (freeBalls.size() == 0) return null; // DANGER

		Ball nearestBall = freeBalls.get(0);
		for (Ball ball : freeBalls)
		{
			if (ball.getLocation().distance(avatarLoc) < nearestBall.getLocation().distance(avatarLoc))
			{
				nearestBall = ball;
			}
		}

		return nearestBall;
	}

	public Goal findBestGoal(Point avatarLoc, Ball ball)
	{

		Point nearestBallLoc = ball.getLocation();
		List<Goal> suitableGoals = new Vector<Goal>();

		while (suitableGoals.size() == 0)
		{
			for (Goal goal : goals)
			{
				if (goal.accepts(ball)) suitableGoals.add(goal);
			}

			if (suitableGoals.size() == 0) Thread.yield();
		}

		if (suitableGoals.size() == 0) return null; // DANGER

		Goal nearestGoal = suitableGoals.get(0);

		for (Goal goal : suitableGoals)
		{
			if (goal.dropPoint(avatarLoc).distance(nearestBallLoc) < nearestGoal.dropPoint(avatarLoc).distance(
					nearestBallLoc))
			{
				nearestGoal = goal;
			}
		}

		return nearestGoal;
	}

	public void announceGoal(Goal goal)
	{
		goals.add(goal);

	}

	public Goal getGoal(String id)
	{

		for (Goal goal : goals)
		{
			if (goal.id().equals(id))
			{

				return goal;
			}
		}

		return null;
	}

	public void ballsUpdate(List<Ball> detectedBalls)
	{
		compTaskLock.lock();
		for (Errand task : completedTasks)
		{
			tasks.remove(task);
		}
		completedTasks.clear();
		compTaskLock.unlock();

		// Any ball detected and not active is a safe candidate for future tasks
		List<Ball> detectedAndFree = new Vector<Ball>();
		List<Ball> activeBalls = new Vector<Ball>();

		for (Errand task : tasks)
			activeBalls.add(task.getBall());

		for (Ball incoming : detectedBalls)
		{
			Point incomingLoc = incoming.getLocation();
			if (getBall(activeBalls, incomingLoc.x, incomingLoc.y) == null)
			{
				detectedAndFree.add(incoming);
			}
		}

		freeBalls.clear();
		freeBalls.addAll(detectedAndFree);

		// Any ball not detected but active (without being gripped) is problematic.
		// It's possible it was just obscured by the robot picking it up though, so
		// give it a very generous time limit to get it's act together

		List<Errand> expiredTasks = new Vector<Errand>();

		for (Errand task : tasks)
		{
			if (task.hasBall()) continue;

			Ball taskBall = task.getBall();

			boolean foundMatch = false;
			for (Ball incoming : detectedBalls)
			{
				if (areLocationsClose(taskBall.getLocation(), incoming.getLocation()))
				{
					foundMatch = true;
					task.unExpire();
					break;
				}
			}

			if (!foundMatch) expiredTasks.add(task);
		}

		// Need to process expirations from a separate collection, as the
		// invocation can cause it's removal from the task list..
		for (Errand task : expiredTasks)
		{
			task.attemptExpire();
		}

		supplicantLock.lock();
		List<Avatar> unassigned = new Vector<Avatar>();
		for (Avatar avatar : supplicants)
		{
			if (freeBalls.size() > 0)
			{
				avatar.assignTask(createDuty(avatar));
			}
			else
			{
				unassigned.add(avatar);
			}
		}
		supplicants.clear();
		supplicants.addAll(unassigned);
		supplicantLock.unlock();
	}

	private static final float threshold = 10f;

	private boolean areLocationsClose(Point loc1, Point loc2)
	{
		double x_diff = Math.abs(loc1.x - loc2.x);
		double y_diff = Math.abs(loc1.y - loc2.y);

		if (Math.sqrt(x_diff * x_diff + y_diff * y_diff) <= threshold)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public Ball getBall(List<Ball> balls, float x, float y)
	{

		Point loc = new Point(x, y);
		for (Ball ball : balls)
		{
			if (areLocationsClose(ball.getLocation(), loc))
			{

				return ball;
			}
		}

		return null;
	}

	public void abortTask(Errand task)
	{
		compTaskLock.lock();
		completedTasks.add(task);
		compTaskLock.unlock();
		System.out.println("Aborted task!: " + task);

	}

	public void completeTask(Errand task)
	{
		compTaskLock.lock();
		completedTasks.add(task);
		compTaskLock.unlock();
		System.out.println("Completed task!: " + task);

	}

	public static final long updateRate = 1 * 1000;
	private long lastUpdate = 0;

	/**
	 * BallSubscriber is a receiving point for network communications related to balls, and determines the pace of
	 * internal updates within the TaskOverlord.
	 */
	String firstSource = null;

	private class BallSubscriber implements LCMSubscriber
	{
		private Lock messageLock = new ReentrantLock();

		public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins)
		{
			balls_t detected = null;
			try
			{
				detected = new balls_t(ins);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				return;
			}

			// Just writing code to handle one source for now.
			// I'll make it multi source compliant later.
			if (firstSource == null)
			{
				firstSource = detected.info.source;
				System.out.println("Using " + firstSource + " as sole ball source.");
			}
			else if (!detected.info.source.equals(firstSource))
			{
				return;
			}

			// Only update every [updateRate] seconds
			long now = System.currentTimeMillis();
			if (now - lastUpdate > updateRate)
			{
				boolean locked = messageLock.tryLock();
				if (!locked) return;

				lastUpdate = now;

				List<Ball> javinatedBalls = new Vector<Ball>();
				for (ball_t ball : detected.balls)
				{
					Ball javaBall = new Ball(new Point((float) ball.position[0], (float) ball.position[1]),
							BallColor.values()[ball.colour]);
					javinatedBalls.add(javaBall);
				}

				ballsUpdate(javinatedBalls);
				messageLock.unlock();
			}
		}
	}
}
