package com.pku.judgeonline.mail;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SendPage extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
		if (!UserModel.isLoginned(paramHttpServletRequest))
		{
			ErrorProcess.Error("Please login first .", localPrintWriter);
			return;
		}
		String str1 = "";
		String str2 = "";
		String str3 = "";
		str1 = paramHttpServletRequest.getParameter("to");
		if (str1 == null)
			str1 = "";
		str1 = str1.trim();
		long l = 0L;
		try
		{
			l = Integer.parseInt(paramHttpServletRequest.getParameter("reply"));
		} catch (NumberFormatException localNumberFormatException)
		{
			l = 0L;
		}
		try
		{
			if (l != 0L)
			{
				Connection localConnection = DBConfig.getConn();
				FormattedOut.printHead(localPrintWriter, paramHttpServletRequest, localConnection, "Send mail");
				PreparedStatement localPreparedStatement = localConnection.prepareStatement("select * from mail where mail_id=?");
				localPreparedStatement.setLong(1, l);
				ResultSet localResultSet = localPreparedStatement.executeQuery();
				if (!localResultSet.next())
				{
					ErrorProcess.Error("No such mail", localPrintWriter);
					localConnection.close();
					localConnection = null;
					return;
				}
				String str4 = localResultSet.getString("to_user");
				if (!UserModel.isUser(paramHttpServletRequest, str4))
				{
					ErrorProcess.Error("invalid access", localPrintWriter);
					localConnection.close();
					return;
				}
				str1 = localResultSet.getString("from_user");
				str2 = localResultSet.getString("title");
				str3 = localResultSet.getString("content");
				if ((str2.length() < 3) || (!str2.substring(0, 3).toLowerCase().equals("re:")))
					str2 = "Re:" + str2;
				/*localPreparedStatement.close();
				localPreparedStatement = localConnection.prepareStatement("update mail set reply=1 where mail_id=?");
				localPreparedStatement.setLong(1, l);
				localPreparedStatement.executeUpdate();
				localPreparedStatement.close();
				localConnection.close();*/
			} else
			{
				FormattedOut.printHead(localPrintWriter, paramHttpServletRequest, null, "Send mail");
			}
			localPrintWriter.println("<center><h2><font color=blue>Write mail</font></h2></center>");
			localPrintWriter.println("<form method=post action=send>");
			localPrintWriter.println("To:&nbsp;&nbsp;&nbsp;<input type=text name=to value='" + str1 + "' size=15>(Write Receiver's  User Id here)<br>");
			localPrintWriter.println("Title:<input type=text name=title value=\"" + Tool.titleEncode(str2) + "\" size=94><br>");
			localPrintWriter.println("<textarea rows=20 name=content cols=100>" + Tool.titleEncode(Tool.getReplyString(str3)) + "</textarea><br>");
			localPrintWriter.println("<input type=hidden name=reply value="+l+">");
			localPrintWriter.println("<input type=submit name=b1 value=Send>");
			FormattedOut.printBottom(paramHttpServletRequest, localPrintWriter);
		} catch (Exception localException)
		{
			ErrorProcess.ExceptionHandle(localException, localPrintWriter);
			return;
		}
	}

	public void destroy()
	{
	}
}
