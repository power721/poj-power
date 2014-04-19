package com.pku.judgeonline.mail;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ShowMail extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowMail()
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
		if (!UserModel.isLoginned(request))
		{
			ErrorProcess.Error("Please login first .", out);
			return;
		}
		long l = 0L;
		long reply = 0L;
		try
		{
			l = Integer.parseInt(request.getParameter("mail_id"));
		} catch (Exception exception)
		{
			ErrorProcess.Error("No such mail", out);
			return;
		}
		Connection connection;
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, "Detail of mail");
			preparedstatement = connection.prepareStatement("select * from mail where mail_id=? and UPPER(defunct)='N'");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error("No such mail", out);
				connection.close();
				connection = null;
				return;
			}
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
		try
		{
			if (!UserModel.isUser(request, resultset.getString("to_user")))
			{
				ErrorProcess.Error("Sorry,invalid access", out);
				connection.close();
				return;
			}
			out.println("<table class=mail align=center width=99% border=0>");
			out.println("<tr class=\"mail-head\" bgcolor=#c0c0c0><td>");
			out.println((new StringBuilder()).append("From:<a href=\"userstatus?user_id=").append(resultset.getString("from_user")).append("\"><font size=4>").append(resultset.getString("from_user")).append("</font></a><br>").toString());
			out.println((new StringBuilder()).append("Title:<font color=blue>").append(Tool.titleEncode(resultset.getString("title"))).append("</font><br>").toString());
			out.println((new StringBuilder()).append("Send Time:").append(resultset.getTimestamp("in_date")).append("<br>").toString());
			out.println("</td></tr>");
			out.println("<tr class=\"mail-content\" bgcolor=#78c8ff><td>");
			out.println("<hr><pre>");
			out.println(Tool.htmlEncode(resultset.getString("content")));
			out.println("</pre><hr>");
			out.println("</td></tr>");
			
			while((reply=resultset.getLong("reply")) > 0)
			{
				preparedstatement = connection.prepareStatement("select * from mail where mail_id=? and UPPER(defunct)='N'");
				preparedstatement.setLong(1, reply);
				resultset = preparedstatement.executeQuery();
				if(resultset.next())
				{
					out.println("<tr class=\"reply-mail reply-mail-head\"><td>");
					out.println((new StringBuilder()).append("From:<a href=\"userstatus?user_id=").append(resultset.getString("from_user")).append("\"><font size=4>").append(resultset.getString("from_user")).append("</font></a><br>").toString());
					out.println((new StringBuilder()).append("Title:<font color=blue>").append(Tool.titleEncode(resultset.getString("title"))).append("</font><br>").toString());
					out.println((new StringBuilder()).append("Send Time:").append(resultset.getTimestamp("in_date")).append("<br>").toString());
					out.println("</td></tr>");
					out.println("<tr class=\"reply-mail reply-mail-content\"><td>");
					out.println("<pre>");
					out.println(Tool.htmlEncode(resultset.getString("content")));
					out.println("</pre>");
					out.println("</td></tr>");
				}
			}
			out.println("</table>");
			
			out.println("<center>[<a href=mail><font color=blue>Return</font></a>]");
			out.println((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;[<a href=sendpage?reply=").append(l).append("><font color=blue>Reply</font></a>]").toString());
			out.println((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;[<a href=deletemail?mail_id=").append(l).append("><font color=blue>Delete</font></a>]</center><br>").toString());
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("update mail set new_mail=0 where mail_id=?");
			preparedstatement.setLong(1, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			connection.close();
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
		FormattedOut.printBottom(request, out);
	}

	public void destroy()
	{
	}
}
