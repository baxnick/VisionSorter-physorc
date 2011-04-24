package physical.navigation;

import lejos.geom.Point;
import lejos.robotics.Pose;

public interface BetterNavigator
{
	void setTrackingOffset(Point point);

	void setTurnSpeed(float rotationSpeed);
	void setMoveSpeed(float operatingSpeed);

	float angleTo(float x, float y);

	float distanceTo(float x, float y);

	Pose getPose();
	void setPose(Pose newPose);

	boolean isMoving();
	
	void goTo(float x, float y, boolean returnImmediately) throws InterruptedException;
	void rotate(float angle,    boolean returnImmediately) throws InterruptedException;
	void rotateTo(float angle,  boolean returnImmediately) throws InterruptedException;
	void travel(float distance, boolean returnImmediately) throws InterruptedException;
	void stop() throws InterruptedException;
}
