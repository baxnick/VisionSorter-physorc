package physical.navigation;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lejos.geom.Point;
import lejos.robotics.Pose;
import lejos.robotics.navigation.Pilot;

public class BetterNavigatorMach2 implements BetterNavigator
{
	private Lock updateLock = new ReentrantLock();
	private ReadWriteLock poseLock = new ReentrantReadWriteLock();
	private Lock poseRead = poseLock.readLock();
	private Lock poseWrite = poseLock.writeLock();

	// orientation and co-ordinate data
	private Pose _pose = new Pose();
	private Pose _estimatedPose = new Pose();

	private double _distance0 = 0;
	private double _angle0 = 0;
	private boolean _interrupted = false;

	private Pilot pilot;

	private Point tracked_point = new Point(0, 0);
	private Point adjustedPoint(float targetX, float targetY, float atAngle, int multiplier)
	{
		// x is ignored totally for now
		float heading = (float) (Math.toRadians(atAngle));
		multiplier = Math.min(1, multiplier);
		multiplier = Math.max(-1, multiplier);

		float offX = tracked_point.x * (float)Math.cos(heading) + tracked_point.y * (float)Math.sin(heading);
		float offY = tracked_point.y * (float)Math.cos(heading) - tracked_point.x * (float)Math.sin(heading);
		
		return new Point(targetX + offX * multiplier, targetY + offY * multiplier);
	}
	
	@Override
	public void setTrackingOffset(Point point)
	{
		tracked_point.setLocation(point);
	}
	
	/**
	 * Allocates a BetterNavigator with a Pilot that you supply.
	 * 
	 * @param pilot
	 *           can be any class that implements the pilot interface
	 */
	public BetterNavigatorMach2(Pilot pilot)
	{
		this.pilot = pilot;
	}

	@Override
	public void setTurnSpeed(float rotationSpeed)
	{
		pilot.setTurnSpeed(rotationSpeed);
	}

	@Override
	public void setMoveSpeed(float operatingSpeed)
	{
		pilot.setMoveSpeed(operatingSpeed);
	}

	@Override
	public float angleTo(float x, float y)
	{
		updatePose();

		poseRead.lock();
		float ret = _estimatedPose.angleTo(new Point(x, y));
		poseRead.unlock();
		
		return ret;
	}


	public void updatePose()
	{
		boolean canUpdate = updateLock.tryLock();
		if (!canUpdate) return;

		double pilotDistance = pilot.getTravelDistance();
		double pilotAngle = pilot.getAngle();

		double distance = pilotDistance - _distance0;
		double turnAngle = pilotAngle - _angle0;
		double dx = 0;
		double dy = 0;
		double headingRad = (Math.toRadians(_pose.getHeading()));

		if (Math.abs(turnAngle) > .5)
		{
			double turnRad = Math.toRadians(turnAngle);
			double radius = distance / turnRad;
			dy = radius * (Math.cos(headingRad) - Math.cos(headingRad + turnRad));
			dx = radius * (Math.sin(headingRad + turnRad) - Math.sin(headingRad));
		}
		else
		{
			dx = distance * Math.cos(headingRad);
			dy = distance * Math.sin(headingRad);
		}

		poseWrite.lock();
		if (!isMoving())
		{
			_pose.translate((float) dx, (float) dy);
			_pose.rotateUpdate((float) turnAngle);
			_angle0 = pilotAngle;
			_distance0 = pilotDistance;

			_estimatedPose = copyPose(_pose);
		}
		else
		{
			_estimatedPose = copyPose(_pose);
			_estimatedPose.translate((float) dx, (float) dy);
			_estimatedPose.rotateUpdate((float) turnAngle);
		}
		poseWrite.unlock();

		updateLock.unlock();
	}

	// not threadsafe, lock first
	private Pose copyPose(Pose copyMe)
	{
		Pose newPose = new Pose(copyMe.getX(), copyMe.getY(), copyMe.getHeading());
		return newPose;
	}
	
	@Override
	public float distanceTo(float x, float y)
	{
		updatePose();

		poseRead.lock();
		float ret = _estimatedPose.distanceTo(new Point(x, y));
		poseRead.unlock();

		return ret;
	}

	@Override
	public Pose getPose()
	{
		updatePose();

		poseRead.lock();
		Pose copy = copyPose(_estimatedPose);
		poseRead.unlock();

		return copy;
	}

	@Override
	public void setPose(Pose newPose)
	{
		updateLock.lock();
		poseWrite.lock();
		pilot.reset();
		_angle0 = 0;
		_distance0 = 0;
		_pose = newPose;
		_estimatedPose = copyPose(newPose);
		poseWrite.unlock();
		updateLock.unlock();
	}

	@Override
	public boolean isMoving()
	{
		return pilot.isMoving();
	}


	private void interruptibleMoveWait(boolean returnImmediately) throws InterruptedException
	{
		if (returnImmediately) return;
		
		while (isMoving())
		{
			if (_interrupted)
			{
				_interrupted = false;
				throw new InterruptedException();
			}
			Thread.yield();
		}
	}
	
	@Override
	public void rotate(float angle, boolean returnImmediately) throws InterruptedException
	{
		updatePose();

		pilot.rotate(Math.round(angle), true);
		
		interruptibleMoveWait(returnImmediately);
	}

	@Override
	public void rotateTo(float angle, boolean returnImmediately) throws InterruptedException
	{
		updatePose();
		
		float turnAngle = angle - getHeading();
		while (turnAngle < -180)
			turnAngle += 360;
		while (turnAngle > 180)
			turnAngle -= 360;
		rotate(turnAngle, true);
		
		interruptibleMoveWait(returnImmediately);
	}
	
	@Override
	public void goTo(float x, float y, boolean returnImmediately) throws InterruptedException
	{
		updatePose();
		
		float angleToTarget = angleTo(x, y);
		Point adjustedTarget = adjustedPoint(x, y, angleToTarget, -1);
		
		rotateTo(angleToTarget, false);
		travel(distanceTo(adjustedTarget.x, adjustedTarget.y), true);

		interruptibleMoveWait(returnImmediately);
	}

	@Override
	public void travel(float distance, boolean returnImmediately) throws InterruptedException
	{
		updatePose();

		pilot.travel(distance, true);
		interruptibleMoveWait(returnImmediately);
	}
	
	/**
	 * gets the current value of the robot heading
	 * 
	 * @return current heading
	 */
	public float getHeading()
	{
		updatePose();

		poseRead.lock();
		float ret = _estimatedPose.getHeading();
		poseRead.unlock();

		return ret;
	}
}
