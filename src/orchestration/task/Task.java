package orchestration.task;

import orchestration.Avatar;
import orchestration.goal.Goal;
import orchestration.object.Ball;
import orchestration.object.BotLocationProvider;
import orchestration.path.PathPlanner;
import orchestration.path.RouteMaker;
import physical.GripperBot;
import lejos.geom.Point;

/**
 * A Task represents the intent of an avatar to deliver a particular ball to a particular goal. Furthermore, it keeps an
 * internal state machine of the task progress and manages each stage of the task on behalf of the avatar.
 * 
 * Once the task has been finished, the Task will be discarded, and the avatar will request a new one from the
 * TaskOverlord.
 * 
 * @author baxnick
 * 
 */
public class Task
{
	private PathPlanner planner;
	private Avatar avatar;
	private Ball ball;
	private Goal goal;
	private GripperBot bot;
	private TaskOverlord overlord;
	private TaskState state;
	private RouteMaker router;
	private TaskConfig cfg;

	private boolean taskActive = true;
	private boolean hasBall = false;
	private long firstExpired = 0;

	public Task(TaskOverlord overlord, PathPlanner planner, Avatar avatar, Ball ball, Goal goal)
	{
		this.planner = planner;
		this.overlord = overlord;
		this.avatar = avatar;
		this.ball = ball;
		this.goal = goal;
		updateState(TaskState.FETCHING);
	}

	public void assignBot(GripperBot bot)
	{
		this.bot = bot;
		router = new RouteMaker(planner, bot, avatar.getName());
	}

	private TaskState nextState;
	private TaskState continuationState;

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
				updateState(TaskState.DELAYED);
			}

			if (state == TaskState.FETCHING)
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
						continuationState = TaskState.RETURNING;
						nextState = TaskState.VISION;
					}
					else
						nextState = TaskState.RETURNING;
				}
				catch (InterruptedException e)
				{
				}
			}
			else if (state == TaskState.RETURNING)
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
						continuationState = TaskState.COMPLETED;
						nextState = TaskState.VISION;
					}
					else
						nextState = TaskState.COMPLETED;
				}
				catch (InterruptedException e)
				{
				}
			}
			else if (state == TaskState.VISION)
			{
				try
				{
					Point target = avatar.getVision().visionPoint();
					router.follow(router.create(target));

					Thread.sleep(cfg.visionWaitTime);
					while (avatar.getVision().needsVision())
					{
						bot.getNav().rotate(cfg.visionRotationAmount);
						Thread.sleep(cfg.visionWaitTime);
					}

					nextState = continuationState;
				}
				catch (InterruptedException e)
				{
				}
			}
			else if (state == TaskState.COMPLETED)
			{
				System.out.println(ball);
				taskActive = false;
				overlord.completeTask(this);
			}
			else if (state == TaskState.ABANDONED)
			{
				taskActive = false;
				bot.getGrip().release();

				if (avatar.getVision().needsVision())
				{
					continuationState = TaskState.ABANDONED;
					nextState = TaskState.VISION;
				}
				else
				{
					overlord.abortTask(this);
				}
			}
			else if (state == TaskState.DELAYED)
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
		updateState(TaskState.ABANDONED);
	}

	public String toString()
	{
		return ball.toString() + " to " + goal.toString();
	}

	public boolean isCompleted()
	{
		return state == TaskState.ABANDONED || state == TaskState.COMPLETED;
	}

	public boolean hasBall()
	{
		return hasBall;
	}

	private boolean halted = false;
	private boolean firstHaltFlag = false;
	private TaskState savedState;

	public synchronized void halt()
	{
		halted = true;
		firstHaltFlag = true;
		bot.getNav().softInterrupt();
	}

	public synchronized void resume()
	{
		halted = false;
	}

	private synchronized void updateState(TaskState state)
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
		return state == TaskState.DELAYED;
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

	public void reconfigure(TaskConfig config)
	{
		this.cfg = config;
	}
}
