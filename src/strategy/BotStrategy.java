package strategy;

import physical.GripperBot;

public interface BotStrategy {
	public void execute(GripperBot bot) throws InterruptedException;
}
