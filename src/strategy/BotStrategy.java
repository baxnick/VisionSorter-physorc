package strategy;

import physical.GripperBot;

public interface BotStrategy<T> {
	public void reconfigure(T config);
	public void execute(GripperBot bot) throws InterruptedException;
}
