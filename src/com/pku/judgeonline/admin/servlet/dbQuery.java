package com.pku.judgeonline.admin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.error.ErrorProcess;

public class dbQuery extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Object prob_mute = new Object();

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
		String str1 = paramHttpServletRequest.getParameter("sql");

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

		localPrintWriter.println("</ul>");
		localPrintWriter.println("<a href=showproblem?problem_id=1000>Start Guide</a><br/>");
		localPrintWriter.println("<a href=/oj/faq.htm>F.A.Q</a><br/><br/>");
		FormattedOut.printBottom(paramHttpServletRequest, localPrintWriter);
	}

	public void destroy()
	{
	}
}
