package orchestration;

import orchestration.goal.Goal;
import orchestration.object.Ball;
import orchestration.object.BotLocationProvider;
import physical.GripperBot;
import lejos.geom.Point;

public class Task {
	private Ball ball;
	private Goal goal;
	private GripperBot bot;
	private TaskOverlord overlord;
	private TaskState state;
	private boolean taskActive = true;
	
	public Task (TaskOverlord overlord, Ball ball, Goal goal)
	{
		this.overlord = overlord;
		this.ball = ball;
		this.goal = goal;
		updateState(TaskState.FETCHING);
	}
	
	public void assignBot(GripperBot bot)
	{
		this.bot = bot;
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
					bot.getNav().goToShort(ballLoc.x, ballLoc.y, 120, false);
					if (halted) continue;
					ball.fetch().execute(bot);
					unExpire();
					if (halted) continue;
					
					if (bot.getVision().needsVision())
					{
						continuationState = TaskState.RETURNING;
						nextState = TaskState.VISION;
					}
					else
						nextState = TaskState.RETURNING;
				}
				catch (InterruptedException e)
				{}
			}
			else if (state == TaskState.RETURNING)
			{
				try
					{
					BotLocationProvider mobileProvider = new BotLocationProvider(bot);
					ball.updateLocation(mobileProvider);
					
					Point dropLoc = goal.dropPoint(bot.location());
					
					bot.getNav().goToShort(dropLoc.x, dropLoc.y, 
							bot.safeDistance(goal.minimumSafeDistance()), false);
					if (halted) continue;
					goal.approachStrategy(dropLoc).execute(bot);
					if (halted) continue;
					ball.updateLocation(mobileProvider.fixLocation());
					goal.disengageStrategy(dropLoc).execute(bot);
					if (halted) continue;
					
					if (bot.getVision().needsVision())
					{
						continuationState = TaskState.COMPLETED;
						nextState = TaskState.VISION;
					}
					else
						nextState = TaskState.COMPLETED;
				}
				catch (InterruptedException e)
				{}
			}
			else if (state == TaskState.VISION)
			{
				try
				{
				Point target = bot.getVision().visionPoint();
				bot.getNav().goTo(target.x, target.y);
				Thread.sleep(800);
				nextState = continuationState;
				}
				catch (InterruptedException e)
				{}
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
				overlord.abortTask(this);
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
		return state == TaskState.RETURNING;
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

	private long firstExpired = 0;
	private static final long expiryAllowance = 10 * 1000;
	
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
			if ((now - firstExpired) > expiryAllowance) abort();
		}
	}
}
