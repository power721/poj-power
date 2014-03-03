package com.pku.judgeonline.security;

import com.pku.judgeonline.error.ErrorProcess;
import java.io.PrintWriter;
import javax.servlet.http.*;

public class Guard
{

	public Guard()
	{
	}

	public static boolean Guarder(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
	{
		if (!AntiFlush(request))
		{
			ErrorProcess.Error("No Flush please!<br>If you sure this was wrong ,please try again.", out);
			return false;
		} else
		{
			return true;
		}
	}

	public static boolean AntiFlush(HttpServletRequest request)
	{
		HttpSession httpsession = request.getSession();
		SecurityData securitydata = (SecurityData) httpsession.getAttribute(SecurityData.SECURITY_DATA_TAG);
		if (securitydata == null)
		{
			securitydata = new SecurityData();
			httpsession.setAttribute(SecurityData.SECURITY_DATA_TAG, securitydata);
		}
		if (httpsession.getLastAccessedTime() + 100L > System.currentTimeMillis())
			securitydata.flushcount++;
		else
			securitydata.flushcount = 0;
		if (securitydata.flushcount < 0)
			securitydata.flushcount = 0;
		return securitydata.flushcount <= SecurityData.MaxFlushCount;
	}
}
