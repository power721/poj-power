package com.pku.judgeonline.contest;

import java.io.Serializable;
import java.util.HashMap;

public class ContestData implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public long top;
	public boolean top_enable;
	public HashMap<String, Object> hm;

	public ContestData()
	{
		top = 0L;
		hm = null;
		top_enable = false;
	}
}
