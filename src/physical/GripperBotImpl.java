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

package physical;

import java.io.IOException;

import physical.comms.FaultFilter;
import physical.comms.SimpleCallback;
import physical.navigation.BetterNavigator;
import physical.navigation.BetterNavigatorMach2;
import physical.navigation.NavControl;

import lejos.geom.Point;
import lejos.nxt.remote.*;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.robotics.Pose;
import lejos.robotics.TachoMotor;
import lejos.robotics.navigation.TachoPilot;

public class GripperBotImpl implements GripperBot
{

	private NXTCommand command;

	private GripperBotConfiguration config;

	private final NavControl navCon;
	private final TachoPilot pilot;
	private SimpleCallback errorCallback = null;
	private boolean isConnected = true;
	private FaultFilter connectionFilter;

	public static GripperBot standardGripper(String name, NXTComm comms) throws IOException, NXTCommException
	{
		GripperBotConfiguration standardConfig = new GripperBotConfiguration(name);
		GripperBotImpl bot = new GripperBotImpl(standardConfig, comms);

		OverheadGripperConfig gripperConfig = new OverheadGripperConfig();
		OverheadGripper overheadGrip = new OverheadGripper(gripperConfig);
		overheadGrip.setMotor(bot.getMotor(gripperConfig.motorId));

		standardConfig.setGrip(overheadGrip);

		return bot;
	}

	private TachoMotor getMotor(int id)
	{
		return new RemoteMotor(command, id);
	}

	public GripperBotImpl(GripperBotConfiguration config, NXTComm comms)
	{
		this.config = config;

		command = new NXTCommand();
		connectionFilter = new FaultFilter(comms);
		connectionFilter.setCallback(new FaultedCallback());

		command.setNXTComm(connectionFilter);
		command.setVerify(true);

		RemoteMotor left = new RemoteMotor(command, config.leftMotor);
		RemoteMotor right = new RemoteMotor(command, config.rightMotor);

		pilot = new TachoPilot(config.wheelDiameter, config.trackWidth, left, right);
		pilot.reset();

		BetterNavigator nav = new BetterNavigatorMach2(pilot);
		nav.setTrackingOffset(new Point(0, config.gripDisplacement));
		nav.setTurnSpeed(config.rotationSpeed);
		nav.setMoveSpeed(config.operatingSpeed);

		navCon = new NavControl(nav);
		new Thread(new HeartBeat()).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physical.GripperBot#recalibrate()
	 */
	@Override
	public void recalibrate()
	{
		getGrip().calibrate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physical.GripperBot#finished()
	 */
	@Override
	public void finished()
	{
		try
		{
			command.close();
		}
		catch (IOException e)
		{
			// We're finished, don't worry if an exception pops up.
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physical.GripperBot#getGrip()
	 */
	@Override
	public Gripper getGrip()
	{
		return config.getGrip();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see physical.GripperBot#getNav()
	 */
	@Override
	public NavControl getNav()
	{
		return navCon;
	}

	@Override
	public NXTCommand getCommand()
	{
		return command;
	}

	@Override
	public GripperBotConfiguration getConfig()
	{
		return config;
	}

	@Override
	public float safeDistance(float minDistance)
	{
		float safeDistance = config.rearDisplacement + config.gripDisplacement;

		return Math.max(safeDistance, minDistance);
	}

	@Override
	public Point location()
	{
		Pose navPose = navCon.getPose();
		return new Point(navPose.getX(), navPose.getY());
	}

	@Override
	public void setErrorHandler(SimpleCallback onConnectionError)
	{
		this.errorCallback = onConnectionError;
	}

	public void notifyError()
	{
		connectionErrored();
	}

	private void connectionErrored()
	{
		if (!isConnected) return;

		isConnected = false;
		if (errorCallback != null)
		{
			errorCallback.callback();
		}
		finished();
	}

	private class HeartBeat implements Runnable
	{
		@Override
		public void run()
		{
			while (isConnected)
			{
				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}

				if (!command.isOpen())
				{
					connectionErrored();
				}

				if (connectionFilter.isFaulted())
				{
					connectionErrored();
				}
			}
		}
	}

	private class FaultedCallback implements SimpleCallback
	{
		@Override
		public void callback()
		{
			if (connectionFilter.isFaulted()) connectionErrored();
		}

	}
}
