package com.pku.judgeonline.user;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.common.ValueCheck;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
		String str1 = paramHttpServletRequest.getParameter("user_id");
		String str2 = paramHttpServletRequest.getParameter("nick");
		String str3 = paramHttpServletRequest.getParameter("password");
		String str4 = paramHttpServletRequest.getParameter("email");
		String str5 = paramHttpServletRequest.getParameter("rptPassword");
		String str6 = paramHttpServletRequest.getParameter("school");
		int qq = 0;
		try
		{
			qq = Integer.parseInt(paramHttpServletRequest.getParameter("qq"));
		} catch (Exception exception)
		{
			qq = 0;
		}
		if ((str6 == null) || (str6.trim().equals("")))
			str6 = "";
		Timestamp localTimestamp = new Timestamp(System.currentTimeMillis());
		if (!ValueCheck.checkId(str1, localPrintWriter))
			return;
		if ((str1.toUpperCase()).startsWith("TEAM"))
		{
			ErrorProcess.Error("You cann't register this ID!", localPrintWriter);
			return;
		}
		if (!ValueCheck.checkPassword(str3, localPrintWriter))
			return;
		if (!str3.equals(str5))
		{
			ErrorProcess.Error("Passwords are not match", localPrintWriter);
			return;
		}
		if ((str2 == null) || (str2.trim().equals("")))
			str2 = str1;
		if (!ValueCheck.checkNick(str2, localPrintWriter))
			return;
		if (str2.length() > 100)
			str2 = str2.substring(0, 98);
		synchronized (this)
		{
			try
			{
				Connection localConnection = DBConfig.getConn();
				PreparedStatement localPreparedStatement = localConnection.prepareStatement("SELECT * FROM users WHERE UPPER(user_id) = UPPER(?)");
				localPreparedStatement.setString(1, str1);
				ResultSet localResultSet = localPreparedStatement.executeQuery();
				if (localResultSet.first())
				{
					ErrorProcess.Error("The ID( " + str1 + ") existed", localPrintWriter);
					localPreparedStatement.close();
					localConnection.close();
					return;
				}
				localPreparedStatement.close();
				localPreparedStatement = localConnection.prepareStatement("INSERT INTO users (user_id,password,email,reg_time,nick,school,qq) values (?,encode(?,?),?,?,?,?,?)");
				localPreparedStatement.setString(1, str1);
				localPreparedStatement.setString(2, str3);
				localPreparedStatement.setString(3, ServerConfig.ENCODE_STRING);
				localPreparedStatement.setString(4, str4);
				localPreparedStatement.setTimestamp(5, localTimestamp);
				localPreparedStatement.setString(6, str2);
				localPreparedStatement.setString(7, str6);
				localPreparedStatement.setInt(8, qq);
				localPreparedStatement.executeUpdate();
				localPreparedStatement.close();
				FormattedOut.printHead(localPrintWriter, paramHttpServletRequest, null, "Congratulations");
				localConnection.close();
			} catch (Exception localException)
			{
				ErrorProcess.ExceptionHandle(localException, localPrintWriter);
				localException.printStackTrace(System.err);
			}
		}
		localPrintWriter.println("<p>");
		localPrintWriter.println("<img border=\"0\" src=\"" + ServerConfig.getValue("RootPathOJ") + "images/j0293240.wmf\" width=\"50\" height=\"36\">");
		localPrintWriter.println("<font size=\"4\">Congratulations</font></p>");
		localPrintWriter.println("<ul>");
		localPrintWriter.println("  <li>ID:\t\t" + str1 + "</li>");
		localPrintWriter.println("  <li>Password:\t*********</li>");
		localPrintWriter.println("  <li>Email:\t\t" + str4 + "</li>");
		localPrintWriter.println("</ul>");
		localPrintWriter.println("<a href=showproblem?problem_id=1000>Start Guide</a><br/>");
		localPrintWriter.println("<a href=/oj/faq.htm>F.A.Q</a><br/><br/>");
		FormattedOut.printBottom(paramHttpServletRequest, localPrintWriter);
	}

	public void destroy()
	{
	}
}
