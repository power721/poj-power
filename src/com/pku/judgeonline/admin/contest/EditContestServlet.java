﻿package com.pku.judgeonline.admin.contest;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class EditContestServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EditContestServlet()
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
		String s1 = request.getParameter("description");
		if (s1 == null)
			s1 = "";
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
		int c1;
		int Private = 0;
		int freeze = 0;
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
			c1 = Integer.parseInt(request.getParameter("contest_id"));
			Private = Integer.parseInt(request.getParameter("private"));
			
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		try
		{
			freeze = Integer.parseInt(request.getParameter("freeze"));
		} catch (Exception e)
		{
			freeze = 0;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(i, j, k, l, i1, 0);
		Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
		calendar.set(j1, k1, l1, i2, j2, 0);
		Timestamp timestamp1 = new Timestamp(calendar.getTimeInMillis());
		try
		{
			// long l2 = ServerConfig.getNextContestId();

			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("select end_time from contest where contest_id=?");
			preparedstatement.setLong(1, c1);
			ResultSet resultset = preparedstatement.executeQuery();
			boolean flag = false;
			if(resultset.next())
			{
				flag = resultset.getTimestamp("end_time").getTime() < System.currentTimeMillis() && System.currentTimeMillis() < timestamp1.getTime();
			}
			preparedstatement = connection.prepareStatement("update contest set title=?,description=?,start_time=?,end_time=?,private=?,freeze=? where contest_id=?");
			preparedstatement.setString(1, s);
			preparedstatement.setString(2, s1);
			preparedstatement.setTimestamp(3, timestamp);
			preparedstatement.setTimestamp(4, timestamp1);
			preparedstatement.setInt(5, Private);
			preparedstatement.setInt(6, freeze);
			preparedstatement.setLong(7, c1);
			preparedstatement.executeUpdate();
			
			if(flag)
			{
				preparedstatement = connection.prepareStatement("select problem_id from contest_problem where contest_id=?");
				preparedstatement.setLong(1, c1);
				resultset = preparedstatement.executeQuery();
				for(;resultset.next();)
				{
					int pid = resultset.getInt("problem_id");
					preparedstatement = connection.prepareStatement("update problem set contest_id=? where problem_id=?");
					preparedstatement.setLong(1, c1);
					preparedstatement.setInt(2, pid);
					preparedstatement.executeUpdate();
				}
			}
			preparedstatement.close();
			connection.close();
			FormattedOut.printHead(out, "Congratulations");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
			out.println("<font size=\"4\">Congratulations</font></p>");
			out.println("<ul>");
			out.println((new StringBuilder()).append("<li>You have modify a contest, the id is <font color=\"#CC9900\">").append(c1).append("</font></li>").toString());
			out.println((new StringBuilder()).append("<li>Start time:<font color=\"#CC9900\">").append(timestamp).append("</font></li>").toString());
			out.println((new StringBuilder()).append("<li>End time:<font color=\"#CC9900\">").append(timestamp1).append("</font></li>").toString());
			out.println("</ul>");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.addcontestpage\">Add Another Contest</a>").toString());
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.contestmanagerpage?contest_id=").append(c1).append("\">See This Contest</a>").toString());
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
