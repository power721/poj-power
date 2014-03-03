package com.pku.judgeonline.news;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ShowAnnounce extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowAnnounce()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		long l;
		Connection connection;
		boolean flag2;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		String s = request.getParameter("announce_id");
		if (s == null || s.trim().equals(""))
		{
			ErrorProcess.Error("No such announcement", out);
			return;
		}
		l = 0L;
		try
		{
			l = Integer.parseInt(s);
		} catch (Exception exception)
		{
			ErrorProcess.Error("No such announcement", response.getWriter());
			return;
		}
		connection = DBConfig.getConn();
		flag2 = UserModel.isAdminLoginned(request);
		ResultSet resultset;
		PreparedStatement preparedstatement;
		try
		{
			if (flag2)
				preparedstatement = connection.prepareStatement("select * from announce where id=?");
			else
				preparedstatement = connection.prepareStatement("select * from announce where id=? and UPPER(defunct) = 'N'");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error("No such announcement", out);
				preparedstatement.close();
				preparedstatement = null;
				connection.close();
				connection = null;
				return;
			}

			preparedstatement = connection.prepareStatement("update announce set view=view+1 where id=?");
			preparedstatement.setLong(1, l);
			preparedstatement.executeUpdate();
			FormattedOut.printHead(out, request, connection, "Announcement");
			String s1 = resultset.getString("title");
			String s2 = resultset.getString("user_id");
			String s3 = resultset.getString("content");
			String s4 = resultset.getString("editor");
			out.println("<table align=center width=96% border=0 cellSpacing=0 cellPadding=30  background=images/table_back.jpg><tr><td align=center><font size=5 color=#333399>" + s1 + "</font></p>");
			if (flag2)
				out.println("<center><a href=admin.editannouncepage?announce_id=" + resultset.getString("id") + ">Edit</a></center>");
			out.println("<font face=Arial><p align=\"center\">Start time:&nbsp;&nbsp;<font color=\"#993399\">" + resultset.getTimestamp("start_time") + "</font>&nbsp;&nbsp;End time:&nbsp;&nbsp;<font color=\"#993399\">" + resultset.getTimestamp("end_time") + "</font>");
			out.println("<br>Publisher:<a href=sendpage?to=" + s2 + ">" + s2 + "</a>");
			if (s4 != null && !s4.equals(""))
				out.println("&nbsp;&nbsp;&nbsp;&nbsp;Editor:<a href=sendpage?to=" + s4 + ">" + s4 + "</a>");
			// if (UserModel.isAdminLoginned(request))
			out.println("View:" + resultset.getString("view"));
			out.println("<br><hr>");
			out.println("</td></tr><tr><td margin=50px>" + s3 + "<br><br><br><br>");
			String str = "<p align=right>----信息工程学院ACM集训队</p>";
			out.println(str);
			out.println("</td></tr></table>");
			connection.close();
			FormattedOut.printBottom(request, out);
		} catch (SQLException sqlexception)
		{
			ErrorProcess.ExceptionHandle(sqlexception, out);
		}
		return;
	}

	public void destroy()
	{
	}
}
