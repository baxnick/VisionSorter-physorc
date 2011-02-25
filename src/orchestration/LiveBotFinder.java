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

import physical.GripperBot;
import physical.GripperBotImpl;

import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 * The HotBotWatch watches the bluetooth network for robots it can connect to. 
 * Whenever it establishes a connection with a new robot, it creates an 
 * implementation of GripperBot, passing it on to Avatar.spawn, which will 
 * begin the Avatar thread after constructing it.
 * 
 * It accesses the Lejos NXT functionality quite directly for an "orchestration" 
 * layer class, it might possibly do to split it into physical/orchestration.
 * 
 * @author baxnick
 * 
 */
public class LiveBotFinder implements Runnable
{
	private static final int RETRY_INTERVAL = 3000; // ms, how long to wait in between bluetooth pings

	private Coordinator parent;

	public LiveBotFinder(Coordinator parent)
	{
		this.parent = parent;
	}

	@Override
	public void run()
	{
		NXTComm searchComm = null;
		try
		{
			searchComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
		}
		catch (NXTCommException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		while (true)
		{
			NXTInfo[] matches = null;

			try
			{
				matches = searchComm.search(null, NXTCommFactory.BLUETOOTH);

				for (NXTInfo match : matches)
				{
					try
					{
						if (!parent.isActive(match.name))
						{
							NXTComm recruitComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
							recruitComm.open(match);
							GripperBot recruitBot = GripperBotImpl.standardGripper(match.name, recruitComm);
							Avatar.spawn(parent, recruitBot);
						}
					}
					catch (NXTCommException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
			catch (NXTCommException e)
			{
				e.printStackTrace();
			}

			try
			{
				Thread.sleep(RETRY_INTERVAL);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
