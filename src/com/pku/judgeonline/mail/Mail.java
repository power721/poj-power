package com.pku.judgeonline.mail;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Mail extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Mail()
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
		if (!Guard.Guarder(request, response, out))
			return;
		int i = 20;
		int j = 1;
		try
		{
			i = Integer.parseInt(request.getParameter("size"));
		} catch (NumberFormatException numberformatexception)
		{
			i = 20;
		}
		try
		{
			j = Integer.parseInt(request.getParameter("start"));
		} catch (NumberFormatException numberformatexception1)
		{
			j = 1;
		}
		if (j < 0)
			j = 1;
		String s = UserModel.getCurrentUser(request).getUser_id();
		try
		{
			Connection connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, "Mail List");
			PreparedStatement preparedstatement = connection.prepareStatement("select title,new_mail,mail_id,from_user,in_date from mail where to_user=? and UPPER(defunct)='N' order by mail_id desc");
			preparedstatement.setString(1, s);
			ResultSet resultset = preparedstatement.executeQuery();
			out.println("<center><img src='./images/email.gif' alt='mail'></center><br>");
			out.println((new StringBuilder()).append("<center><h2><font color=blue>Mail of ").append(s).append("</font></h2></center>").toString());
			out.println("<table align=center border=0>");
			out.println("<tr bgcolor=#78C8FF><td width=5%>No.</td>");
			out.println("<td width=15%>From</td>");
			out.println("<td width=60%>Title</td>");
			out.println("<td width=10%>Date</td></tr>");
			int k = 0;
			if (resultset.absolute(j))
				do
				{
					String s1;
					if (++k % 2 == 0)
						s1 = "#78c8ff";
					else
						s1 = "#c0c0c0";
					out.println((new StringBuilder()).append("<tr bgcolor=").append(s1).append("><td>").append(k).append("</td>").toString());
					out.println((new StringBuilder()).append("<td>").append(resultset.getString("from_user")).append("</td>").toString());
					int l = resultset.getInt("new_mail");
					out.println("<td>");
					if (l == 1)
					{
						out.println("<img src=./images/notread.gif>");
						out.println("<b>");
					} else
					{
						out.println("<img src=./images/read.gif>");
					}
					out.println((new StringBuilder()).append("<a href=showmail?mail_id=").append(resultset.getLong("mail_id")).append("><font color=blue>").append(Tool.titleEncode(resultset.getString("title"))).append("</font></a>").toString());
					if (l == 1)
						out.println("</b>");
					out.println("</td>");
					out.println((new StringBuilder()).append("<td>").append(resultset.getDate("in_date")).append("</td>").toString());
					out.println("</tr>");
				} while (resultset.next() && k < i);
			preparedstatement.close();
			connection.close();
			out.println("</table>");
			out.println("<center>");
			out.println((new StringBuilder()).append("[<a href=mail?size=").append(i).append("><font color=blue>Top</font></a>]").toString());
			out.println((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;[<a href=mail?start=").append(j - i).append("&size=").append(i).append("><font color=blue>Previous</font></a>]").toString());
			out.println((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;[<a href=mail?start=").append(j + i).append("&size=").append(i).append("><font color=blue>Next</font></a>]").toString());
			out.println("&nbsp;&nbsp;&nbsp;[<a href=sendpage><font color=blue>Send Mail</font></a>]");
			out.println("</center>");
			FormattedOut.printBottom(request, out);
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
	}

	public void destroy()
	{
	}
}
