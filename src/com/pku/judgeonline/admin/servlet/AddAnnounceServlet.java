package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AddAnnounceServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddAnnounceServlet()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		String s = request.getParameter("title");
		if (s == null)
			s = "";
		String s1 = request.getParameter("content");
		if (s1 == null)
			s1 = "";
		String s2 = UserModel.getCurrentUser(request).getUser_id();
		int i;
		int j;
		int k;
		int l;
		int i1;
		int j1;
		int k1;
		int l1;
		int i2;
		int j2;
		try
		{
			i = Integer.parseInt(request.getParameter("syear"));
			j = Integer.parseInt(request.getParameter("smonth")) - 1;
			k = Integer.parseInt(request.getParameter("sday"));
			l = Integer.parseInt(request.getParameter("shour"));
			i1 = Integer.parseInt(request.getParameter("sminute"));
			j1 = Integer.parseInt(request.getParameter("eyear"));
			k1 = Integer.parseInt(request.getParameter("emonth")) - 1;
			l1 = Integer.parseInt(request.getParameter("eday"));
			i2 = Integer.parseInt(request.getParameter("ehour"));
			j2 = Integer.parseInt(request.getParameter("eminute"));
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(i, j, k, l, i1, 0);
		Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
		calendar.set(j1, k1, l1, i2, j2, 0);
		Timestamp timestamp1 = new Timestamp(calendar.getTimeInMillis());
		try
		{
			long l2 = ServerConfig.getNextAnnounceId();

			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("INSERT INTO announce (id,title,content,start_time,end_time,user_id) VALUES (?,?,?,?,?,?)");
			preparedstatement.setLong(1, l2);
			preparedstatement.setString(2, s);
			preparedstatement.setString(3, s1);
			preparedstatement.setTimestamp(4, timestamp);
			preparedstatement.setTimestamp(5, timestamp1);
			preparedstatement.setString(6, s2);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			connection.close();
			FormattedOut.printHead(out, "Congratulations");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
			out.println("<font size=\"4\">Congratulations</font></p>");
			out.println("<ul>");
			out.println((new StringBuilder()).append("<li>You have added a announce, the id is <font color=\"#CC9900\">").append(l2).append("</font></li>").toString());
			out.println((new StringBuilder()).append("<li>Start time:<font color=\"#CC9900\">").append(timestamp).append("</font></li>").toString());
			out.println((new StringBuilder()).append("<li>End time:<font color=\"#CC9900\">").append(timestamp1).append("</font></li>").toString());
			out.println("</ul>");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.addannouncepage\">Add Another Announce</a>").toString());
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"showannounce?announce_id=").append(l2).append("\">See This Announce</a>").toString());
			FormattedOut.printBottom(out);
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
	}

	public void destroy()
	{
	}
}
