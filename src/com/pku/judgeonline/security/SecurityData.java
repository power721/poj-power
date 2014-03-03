package com.pku.judgeonline.security;

import java.io.Serializable;

public class SecurityData implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int MaxFlushCount = 4;
	public static String SECURITY_DATA_TAG = "SECURITY_DATA_TAG";
	public int flushcount;

	public SecurityData()
	{
		flushcount = 0;
	}

}
