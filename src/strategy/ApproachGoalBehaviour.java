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
import physical.navigation.commands.nav.*;
import lejos.geom.Point;

public class ApproachGoalBehaviour implements BotStrategy
{
	private ApproachGoalConfig cfg = new ApproachGoalConfig();
	private Point goalLocation;
	private float preferredHeading;

	public ApproachGoalBehaviour(Point location, float preferredHeading)
	{
		this.goalLocation = location;
		this.preferredHeading = preferredHeading;
	}

	public void execute(GripperBot bot) throws InterruptedException
	{
		bot.getNav().BExecute(new CmdGoTo(goalLocation));
		bot.getNav().BExecute(new CmdRotateAng(preferredHeading));
		bot.getGrip().release();
	}

	public void reconfigure(ApproachGoalConfig config)
	{
		this.cfg = config;
	}
}
