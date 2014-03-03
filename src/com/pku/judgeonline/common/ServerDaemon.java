package com.pku.judgeonline.common;

public class ServerDaemon extends Thread
{
	public ServerDaemon()
	{
		setDaemon(true);
		start();
	}

	public void run()
	{
		while (true)
			try
			{
				sleep(5000L);
				continue;
			} catch (Exception localException)
			{
				localException.printStackTrace(System.err);
			}
	}
}
