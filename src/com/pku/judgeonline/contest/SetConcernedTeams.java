package com.pku.judgeonline.contest;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class SetConcernedTeams extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String SESSION_CONCERNEDTEAMS_TAG = "SESSION_CONCERNEDTEAMS_TAG";
	public static int MAX_CONCERNEDTEAMS = 100;

	public SetConcernedTeams()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		java.io.PrintWriter out;
		String as[];
		String s;
		Connection connection;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		as = request.getParameterValues("uid");
		s = request.getParameter("cid");
		connection = DBConfig.getConn();
		try
		{
			if (s == null || !Tool.isContestStarted(s, connection))
			{
				ErrorProcess.Error("No such contest", response.getWriter());
				connection.close();
				return;
			}
			connection.close();
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
		}
		HttpSession httpsession = request.getSession();
		httpsession.removeAttribute(s);
		if (as == null)
		{
			Tool.GoToURL((new StringBuilder()).append("conteststanding?contest_id=").append(s).toString(), response);
			return;
		}
		long l = 0L;
		boolean flag = false;
		if (request.getParameter("top_check") != null)
			flag = true;
		try
		{
			l = Integer.parseInt(request.getParameter("top"));
		} catch (Exception exception1)
		{
			l = 0L;
		}
		if (as.length > MAX_CONCERNEDTEAMS)
			ErrorProcess.Error((new StringBuilder()).append("Sorry,you can set at most ").append(MAX_CONCERNEDTEAMS).append(" teams").toString(), out);
		ContestData contestdata = new ContestData();
		contestdata.top = l;
		contestdata.top_enable = flag;
		contestdata.hm = new HashMap<String, Object>(as.length * 2);
		for (int i = 0; i < as.length; i++)
			if (as[i] != null && as[i].length() <= 20)
				contestdata.hm.put(as[i], null);

		httpsession.setAttribute(s, contestdata);
		Tool.GoToURL((new StringBuilder()).append("conteststanding?contest_id=").append(s).toString(), response);
		return;
	}

	public void destroy()
	{
	}

}
