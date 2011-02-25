package orchestration.main;

import orchestration.Coordinator;

/**
 * 
 * @author baxnick
 *
 */
public class VisionSorter
{
	public static void main(String[] args)
	{
		try
		{
			Coordinator hisHoliness = new Coordinator();
			hisHoliness.start();
		}
		catch (Exception e)
		{
			System.out.println("TOP LEVEL EXCEPTION");
			e.printStackTrace();
		}

	}
}
