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

package orchestration;

import java.io.IOException;

import orchestration.errand.Errand;
import orchestration.errand.ErrandOverlord;
import orchestration.path.Plannable;
import orchestration.path.PlannerShape;
import orchestration.path.RectShape;

import lcm.lcm.*;
import lcmtypes.cube_t;
import lejos.geom.Point;
import lejos.robotics.Pose;
import physical.GripperBot;
import physical.comms.SimpleCallback;
import physical.navigation.commands.Callback;
import physical.navigation.commands.Command;
import physical.navigation.commands.nav.CmdSetPose;

/**
 * The Avatar class works in concert with the Task and strategy.* classes in 
 * order to control a robot. It runs within it's own thread, and is responsible 
 * only for it's particular underlying physical robot. There is one avatar 
 * spawned each time a new robot is detected.
 * 
 * @author baxnick
 * 
 */
public class Avatar implements Runnable, Plannable
{
	private AvatarConfig cfg = new AvatarConfig();
	private Coordinator parent;
	private ErrandOverlord overlord;
	private GripperBot bot;
	private Errand task;
	private String name;
	private Thread myThread;
	private Thread collisionThread;
	private VisionQuery vision;
	private CubeSubscriber cubeSubscriber = null;
	private long lastVision = 0;

	private boolean isActive = false;
	private boolean connectionUp = true;

	public Avatar(Coordinator parent, GripperBot bot)
	{
		parent.reportForDuty(this);

		this.parent = parent;
		this.overlord = parent.getOverlord();
		this.bot = bot;
		this.vision = new BotVisionSource();
		bot.setErrorHandler(new OnConnectionError());
		this.name = bot.getConfig().getName();
	}

	public void start()
	{
		myThread = new Thread(this);
		myThread.start();
	}

	public boolean needsVision()
	{
		return lastVision < System.currentTimeMillis() - cfg.acceptableReckoningTime;
	}

	@Override
	public void run()
	{
		bot.recalibrate();

		cubeSubscriber = new CubeSubscriber();
		this.parent.lcm.subscribe("CUBE", cubeSubscriber);

		while (needsVision())
		{
			Thread.yield();
		}

		isActive = true;

		while (connectionUp)
		{
			overlord.requestDuty(this);

			while (task == null)
				Thread.yield();
			
			task.assignBot(bot);

			System.out.println(getName() + " is taking task: " + task.toString());
			task.fulfil();
			task = null;
		}

		isActive = false;
		parent.removeFromDuty(this);
		bot.finished();
	}

	public void assignTask(Errand assignment)
	{
		this.task = assignment;
	}

	public String getName()
	{
		return name;
	}

	private void dropConn()
	{
		connectionUp = false;
		if (cubeSubscriber != null) parent.lcm.unsubscribe("CUBE", cubeSubscriber);
		bot.getNav().shutdown();
		myThread.interrupt();
		collisionThread.interrupt();
		if (task != null) task.halt();
		parent.removeFromDuty(Avatar.this);
	}

	private class BotVisionSource implements VisionQuery
	{

		@Override
		public boolean needsVision()
		{
			return Avatar.this.needsVision();
		}

		@Override
		public Point visionPoint()
		{
			return cfg.visionZone;
		}

	}

	private class OnConnectionError implements SimpleCallback
	{
		@Override
		public void callback()
		{
			if (connectionUp == false) return;
			System.err.println("Connection dropped, removing " + getName());
			dropConn();
		}

	}

	public VisionQuery getVision()
	{
		return vision;
	}

	public Point location()
	{
		return bot.location();
	}

	public boolean isActive()
	{
		return isActive;
	}

	/**
	 * CubeSubscriber acts as the communication point for bot positioning. It enforces the additional constraint that the
	 * bot has been still for so many seconds, and a separate timing for updates.
	 * 
	 * It currently does not check the latency of the message, but this should probably be checked at a later point, as a
	 * late message at the wrong time could mess up the robot's positioning until the next update.
	 * 
	 * @author baxnick
	 * 
	 */
	private class CubeSubscriber implements LCMSubscriber
	{
		public void messageReceived(LCM lcm, String channel, LCMDataInputStream ins)
		{
			try
			{
				cube_t cube = new cube_t(ins);

				if (!getName().equals(cube.id)) return;

				CmdSetPose cSetPose = new CmdSetPose(
						new Pose((float) cube.position[0], (float) cube.position[1], (float) cube.orientation)
				);
				
				cSetPose.setCaller(new PoseCallback());
				Avatar.this.bot.getNav().Execute(cSetPose);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				dropConn();
			}
		}
	}

	private class PoseCallback implements Callback
	{
		@Override
		public void callback(Command cmd)
		{
			CmdSetPose set = (CmdSetPose)cmd;
			if (!set.wasSuccessful()) return;
			
			lastVision = System.currentTimeMillis();
			System.out.println(getName() + " @ " + set.getPose().getX() + ", " + set.getPose().getY() + ": "
					+ set.getPose().getHeading());
		}
		
	}
	
	@Override
	public String getPlanningName()
	{
		return getName();
	}

	@Override
	public PlannerShape getPlannerShape()
	{
		Pose pose = bot.getNav().getPose();
		return RectShape.easy(bot.getConfig().trackWidth, bot.getConfig().gripDisplacement
				+ bot.getConfig().rearDisplacement, pose.getLocation(), pose.getHeading());
	}

	public void reconfigure(AvatarConfig config)
	{
		this.cfg = config;
	}

	public static Avatar spawn(Coordinator lord, GripperBot recruitBot)
	{
		Avatar newbie = new Avatar(lord, recruitBot);
		newbie.start();
		return newbie;
	}
}
