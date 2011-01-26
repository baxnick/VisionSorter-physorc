package orchestration;

import java.util.ArrayList;
import java.util.List;

import orchestration.goal.Goal;
import orchestration.object.Ball;


import physical.GripperBot;

import lejos.geom.Line;
import lejos.geom.Point;

public class TaskOverlord {
	private List<Task> tasks = new ArrayList<Task>(20);
	private List<Ball> balls = new ArrayList<Ball>(20);
	private List<Goal> goals = new ArrayList<Goal>(20);
	
	private LordSupreme parent;
	
	public TaskOverlord(LordSupreme parent)
	{
		this.parent = parent;
	}
	
	private int current = 0;
	public synchronized Task requestDuty(Avatar soldier)
	{
		while (balls.size() == 0) Thread.yield();
		Ball nearestBall = balls.get(0);
		
		Point avatarLoc = soldier.location();
		
		for (Ball ball : balls)
		{
			if (
					ball.getLocation(avatarLoc).distance(avatarLoc) < 
					nearestBall.getLocation(avatarLoc).distance(avatarLoc))
			{
				nearestBall = ball;
			}
		}

		Point nearestBallLoc = nearestBall.getLocation(avatarLoc);
		
		List<Goal> suitableGoals = new ArrayList<Goal>();
		
		while (suitableGoals.size() == 0)
		{
			for (Goal goal : goals)
			{
				if (goal.accepts(nearestBall)) suitableGoals.add(goal);
			}
			
			if (suitableGoals.size() == 0) Thread.yield();
		}
		
		Goal nearestGoal = suitableGoals.get(0);
		
		for (Goal goal : suitableGoals)
		{
			if (
					goal.dropPoint(avatarLoc).distance(nearestBallLoc) < 
					nearestGoal.dropPoint(avatarLoc).distance(nearestBallLoc))
			{
				nearestGoal = goal;
			}
		}
		
		Task newTask = new Task(this, nearestBall, nearestGoal);
		tasks.add(newTask);
		balls.remove(nearestBall);
		
		return newTask;
	}
	
	public void announceGoal(Goal goal)
	{
		goals.add(goal);
	}
	
	public Goal getGoal(String id)
	{
		for(Goal goal : goals)
		{
			if (goal.id().equals(id)) return goal;
		}
		
		return null;
	}
	
	public void ballsUpdate(List<Ball> detectedBalls)
	{
		balls = detectedBalls;
	}
	
	private static final float threshold = 0.01f;
	
	public Ball getBall(float x, float y)
	{
		for (Ball ball : balls)
		{
			Point ballLoc = ball.getLocation(new Point(0, 0));
			double x_diff = Math.abs(x - ballLoc.x) / x;
			double y_diff = Math.abs(y - ballLoc.y) / y;
			
			if (x_diff <= threshold && y_diff <= threshold) return ball;
		}
		
		return null;
	}

	public void abortTask(Task task)
	{
		if (tasks.contains(task))
		{
			if (!task.hasBall())
			{
				balls.add(task.getBall());
			}
			tasks.remove(task);
		}
	}

	public void completeTask(Task task)
	{
		if (tasks.contains(task))
		{
			tasks.remove(task);
		}
		
	}
}
