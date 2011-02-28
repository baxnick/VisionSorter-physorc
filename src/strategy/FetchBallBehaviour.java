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

package strategy;

import physical.GripperBot;
import lejos.geom.Point;
import lejos.robotics.Pose;
import physical.navigation.NavControl;
import physical.navigation.commands.BlockingCallback;
import physical.navigation.commands.nav.*;

public class FetchBallBehaviour implements BotStrategy
{
	private FetchBallConfig cfg = new FetchBallConfig();

	private Point target;

	public FetchBallBehaviour(Point target)
	{
		this.target = target;
	}

	public void execute(GripperBot bot) throws InterruptedException
	{
		NavControl navCon = bot.getNav();
		float operatingSpeed = bot.getConfig().operatingSpeed;

		Pose botPose;
		float ballHeading;

		bot.getGrip().release();
		
		do
		{
			botPose = navCon.getPose();
			CmdAngleTo cAngle = new CmdAngleTo(target);
			navCon.BExecute(cAngle);
			ballHeading = cAngle.getAngle();

			System.out.println("(PRE) " + bot.getConfig().getName() + " @ " + botPose.getX() + ", " + botPose.getY()
					+ " mh: " + botPose.getHeading() + " bh: " + ballHeading);

			navCon.BExecute(new CmdRotateTo(target));

			botPose = navCon.getPose();
			System.out.println("(POST) " + bot.getConfig().getName() + " @ " + botPose.getX() + ", " + botPose.getY()
					+ " FETCHING ball @ " + target.x + ", " + target.y + " mh: " + botPose.getHeading());
			
			Thread.sleep(cfg.visionWaitDelay);
		}
		while (Math.abs(botPose.getHeading() - ballHeading) > cfg.allowedHeadingError);

		CmdDistanceTo cDist = new CmdDistanceTo(target);
		navCon.BExecute(cDist);
		float distance = cDist.getDistance() * cfg.overshoot;

		CmdTravel cTravel = new CmdTravel(distance);
		BlockingCallback bc = new BlockingCallback();
		cTravel.setCaller(bc);
		navCon.Execute(cTravel);

		try
		{
			Thread.sleep((int) Math.max(0, (int) (1000. * distance / operatingSpeed)));
		}
		catch (InterruptedException e)
		{
			return;
		}

		bot.getGrip().grip();
		
		while (!bc.isExecuted()) Thread.yield();

		botPose = navCon.getPose();
		System.out.println("(FINAL) " + bot.getConfig().getName() + " @ " + botPose.getX() + ", " + botPose.getY() + " mh: " + botPose.getHeading());
	
	}

	public void reconfigure(FetchBallConfig config)
	{
		this.cfg = config;
	}
}
