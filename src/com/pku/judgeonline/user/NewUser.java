package com.pku.judgeonline.user;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class NewUser extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NewUser()
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
		if (!Guard.Guarder(request, response, out))
			return;
		Connection connection = DBConfig.getConn();
		long l = 20L;
		try
		{
			l = Long.parseLong(request.getParameter("count"));
		} catch (Exception exception)
		{
			l = 20L;
		}
		if (l > 200L || l < 0L)
			l = 200L;
		try
		{
			String s1 = null;
			s1 = request.getParameter("str");
			if (s1 == null || s1.trim().equals(""))
				s1 = "reg_time";
			try
			{
				FormattedOut.printHead(out, request, connection, "New User");
				out.print("<div align=center><form action=newuser method=get>");
				out.print((new StringBuilder()).append("<font color=blue size=5>New user of last <input type=text name=count size=5 value=").append(l).append("> registered.</font>").toString());
				out.print("<input type=hidden name=str size=10 value=" + s1 + "><input type=submit value=Go>");
				out.print("</form></div>");
				PreparedStatement preparedstatement = connection.prepareStatement((new StringBuilder()).append("select * from (SELECT * FROM users WHERE UPPER(defunct) = 'N' ORDER BY reg_time desc limit ? )u ORDER BY " + s1 + " desc").toString());
				preparedstatement.setLong(1, l);
				ResultSet resultset = preparedstatement.executeQuery();
				out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=80% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF >");
				out.print("<tr><th style='color:white'>No.</th><th style='color:white'>User Id</th><th style='color:white'>Nick</th><th style='color:white'><a href='newuser?count=" + l + "&str=reg_time'>Reg Time</a></th><th style='color:white'><a href='newuser?count=" + l + "&str=solved'>Solved</a></th><th style='color:white'>Submit</th></tr>");
				int i = 1;
				for (; resultset.next(); out.println((new StringBuilder()).append("<td>").append(resultset.getLong("submit")).append("</td></tr>").toString()))
				{
					String s = resultset.getString("user_id");
					out.println((new StringBuilder()).append("<tr align=center><td>").append(i++).append("</td>").toString());
					out.print((new StringBuilder()).append("<td><a href=userstatus?user_id=").append(s).append(">").append(s).append("</a></td>").toString());
					out.print((new StringBuilder()).append("<td><font color=green>").append(resultset.getString("nick")).append("</font></td>").toString());
					out.print((new StringBuilder()).append("<td>").append(resultset.getString("reg_time")).append("</td>").toString());
					out.print((new StringBuilder()).append("<td>").append(resultset.getLong("solved")).append("</td>").toString());
				}

				preparedstatement.close();
				connection.close();
			} catch (SQLException sqlexception1)
			{
				sqlexception1.printStackTrace(System.out);
			}
			out.println("</table>");
			FormattedOut.printBottom(request, out);
		} catch (Exception exception1)
		{
			try
			{
				connection.close();
			} catch (Exception exception2)
			{
			}
			ErrorProcess.ExceptionHandle(exception1, out);
		}
		out.close();
	}

	public void destroy()
	{
	}
}
