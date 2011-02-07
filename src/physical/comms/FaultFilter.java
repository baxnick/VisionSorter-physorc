package physical.comms;

import java.io.IOException;

import lejos.nxt.remote.NXTCommRequest;
import lejos.nxt.remote.NXTProtocol;

/**
 * The lejos suite really provides no centralised way to monitor the state of a connection. By passing a FaultFilter
 * wrapping a NXTCommRequest, you can desginate a callback to be called the moment an error occurs in the connection.
 * 
 * It will throw an exception if a call was made to a faulted connection. It will also pass on the pre-existing
 * exception after calling the callback when a fault first occurs.
 * 
 * @author baxnick
 * 
 */
public class FaultFilter implements NXTCommRequest, NXTProtocol
{
	private NXTCommRequest forwardee;
	private boolean faulted = false;
	private SimpleCallback callback = null;

	public FaultFilter(NXTCommRequest forwardee)
	{
		this.forwardee = forwardee;
	}

	@Override
	public void close() throws IOException
	{
		try
		{
			forwardee.close();
		}
		catch (IOException e)
		{
			fault();
			throw e;
		}
	}

	@Override
	public byte[] sendRequest(byte[] message, int replyLen) throws IOException
	{

		if (faulted && !isCloseRequest(message))
			throw new IOException("CEASE AND DESIST YOUR PETTY WHINING, THIS CONNECTION IS FAULTED.");

		try
		{
			String messageStr = "";
			for (byte num : message)
				messageStr += Integer.toHexString(num) + ".";

			// System.out.println("SENDREQ " + messageStr + Integer.toHexString(replyLen));

			return forwardee.sendRequest(message, replyLen);
		}
		catch (IOException e)
		{
			fault();
			throw e;
		}
	}

	private boolean isCloseRequest(byte[] message)
	{
		if (message.length != 2) return false;

		return message[1] == NXJ_DISCONNECT;
	}

	public boolean isFaulted()
	{
		return faulted;
	}

	public void reset()
	{
		faulted = false;
	}

	public void setCallback(SimpleCallback callback)
	{
		this.callback = callback;
	}

	private void fault()
	{
		faulted = true;

		if (callback != null)
		{
			callback.callback();
		}
	}
}
