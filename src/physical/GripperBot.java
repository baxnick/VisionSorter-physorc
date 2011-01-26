package physical;


import orchestration.VisionQuery;
import lejos.geom.Point;
import lejos.nxt.remote.NXTCommand;

public interface GripperBot {

	public abstract void recalibrate();

	public abstract void finished();

	public abstract Gripper getGrip();

	public abstract BetterNavigator getNav();
	
	public abstract NXTCommand getCommand();

	public abstract GripperBotConfiguration getConfig();
	
	public abstract float safeDistance(float minDistance);
	
	public abstract Point location();

	public abstract void notifyError();
	
	public abstract void setErrorHandler(SimpleCallback onConnectionError);

	public abstract void setVisionQuery(VisionQuery visionSource);
	
	public abstract VisionQuery getVision();
}