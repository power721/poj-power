package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class OnlineUsers extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OnlineUsers()
	{
	}

	public void init() throws ServletException
	{
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Connection connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		int count = 0;
		int logined = 0;
		int idx = 0;
		String s = "";
		FormattedOut.printHead(out, request, connection, "Online Users");
		if (ServerConfig.startTimestamp % 100 < 30)
		{
			try
			{
				preparedstatement = connection.prepareStatement("delete from sessions where session_expires <= UNIX_TIMESTAMP()");
				preparedstatement.executeUpdate();
				preparedstatement.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		try
		{
			preparedstatement = connection.prepareStatement("SELECT COUNT(*) AS count, COUNT(user_id) AS logined FROM sessions");
			resultset = preparedstatement.executeQuery();
			if(resultset.next())
			{
				count = resultset.getInt("count");
			}
			preparedstatement = connection.prepareStatement("SELECT COUNT(user_id) AS logined FROM sessions WHERE user_id IS NOT NULL AND user_id!=\"\"");
			resultset = preparedstatement.executeQuery();
			if(resultset.next())
			{
				logined = resultset.getInt("logined");
			}
			
			boolean flag = UserModel.isAdminLoginned(request);
			out.println("<font color=blue><h2>" + (flag ? "<a href=onlineuserslist>" : "") + count + " User(s) Online" + (flag ? "</a>" : "") + "</h2></font>");
			out.println("<div>"+logined+" logined and "+(count-logined)+" guests.</div>");
			out.println("<p>\n<table class=online_user align=center width=90%>\n<tr align=center>");
			preparedstatement = connection.prepareStatement("SELECT user_id FROM sessions WHERE user_id IS NOT NULL AND user_id!=\"\"");
			resultset = preparedstatement.executeQuery();
			for(;resultset.next();++idx)
			{
				s = resultset.getString("user_id");
				out.println("<td width=10%><a href=\"userstatus?user_id=" + s + "\">" + s + "</a></td>");
				if(idx>0 && idx%7 == 0)
					out.println("</tr>\n<tr align=center>");
			}
			if(idx>8 && idx%8>0)
			{
				for (int i=0;i<8-idx%8;++i)
					out.println("<td width=10%></td>");
			}
			
			out.println("</tr>\n</table>\n</p>\n");
			FormattedOut.printBottom(request, out);
			out.close();
		
			connection.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void destroy()
	{
	}
}
