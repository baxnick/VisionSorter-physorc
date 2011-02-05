package physical;


import physical.comms.SimpleCallback;
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
	
	public abstract float heading();
	
	public abstract Point location();

	public abstract void notifyError();
	
	public abstract void setErrorHandler(SimpleCallback onConnectionError);
}