package com.pku.judgeonline.admin.problemset;

import com.pku.judgeonline.common.UserModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Rejudge extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Rejudge()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!UserModel.isAdminLoginned(request))
			return;
		String s = null;
		String s1 = null;
		String s2 = null;
		s = request.getParameter("problem_id");
		s1 = request.getParameter("solution_id");
		s2 = request.getParameter("contest_id");
		if (s == null && s1 == null)
			return;
		long l = 0L;
		long l1 = 0L;
		long l2 = 0L;
		try
		{
			if (s != null && !s.trim().equals(""))
				l = Integer.parseInt(s);
			if (s1 != null && !s1.trim().equals(""))
				l1 = Integer.parseInt(s1);
			if (s2 != null && !s2.trim().equals(""))
				l2 = Integer.parseInt(s2);
			if (l != 0L)
				new com.pku.judgeonline.servlet.Rejudge(l, l2);
			else if (l1 != 0L)
				com.pku.judgeonline.servlet.Rejudge.ReJudgeSol(l1, l2);
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
		}
		out.println("<html>");
		out.println("<head><title>Rejudge</title></head>");
		out.println("<body bgcolor=\"#ffffff\">");
		out.println("<p>The servlet has received a GET. This is the reply.</p>");
		out.println((new StringBuilder()).append("time:").append(new Timestamp(System.currentTimeMillis())).toString());
		out.println("<font  size=\"3\"><a href=\"javascript:history.go(-1)\">Go Back</a>");
		out.println("</body></html>");
	}

	public void destroy()
	{
	}
}
