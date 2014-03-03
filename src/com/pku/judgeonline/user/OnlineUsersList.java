package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.security.Guard;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class OnlineUsersList extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OnlineUsersList()
	{
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL("", response);
		}
		Connection connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		FormattedOut.printHead(out, request, connection, "Online Users");
		//Set set = (Set) getServletContext().getAttribute("online");
		int count = 0;
		String s = null;
		String url = null;
		// users=Integer.parseInt((String)servletcontext.getAttribute(
		// "userCounter"));
		//Iterator iterator = set.iterator();
		// out.println("Total:"+users);
		try
		{
			preparedstatement = connection.prepareStatement("SELECT COUNT(*) AS count, COUNT(user_id) AS logined FROM sessions");
			resultset = preparedstatement.executeQuery();
			if(resultset.next())
			{
				count = resultset.getInt("count");
			}
			out.println((new StringBuilder()).append(" <font color=blue><h2><a href=onlineusers>").append(count).append(" User(s) Online</a></h2></font>").toString());
			out.println("<p><table class=online_userlist align=center width=99%><tr align=center>");
		
			preparedstatement = connection.prepareStatement("SELECT user_id,ip_address,user_agent,FROM_UNIXTIME(last_activity, '%Y-%m-%d %H:%i:%s') AS time,uri FROM sessions ORDER BY last_activity DESC");
			resultset = preparedstatement.executeQuery();
			for(;resultset.next();)
			{
				s = resultset.getString("user_id");
				url = resultset.getString("uri");
				if(s == null || s.equals(""))
				{
					s = new StringBuilder().append("Guest(").append(resultset.getString("ip_address")).append(")").toString();
					out.println(new StringBuilder().append("<tr><td width=8%>").append(s).append("</td>").toString());
				}
				else
					out.println(new StringBuilder().append("<tr><td width=8%><a href=userstatus?user_id=").append(s).append(">").append(s).append("</a></td>").toString());
				out.println(new StringBuilder().append("<td width=9%>").append(resultset.getString("time")).append("</td><td width=35%>").append(resultset.getString("user_agent")).append("</td><td><a href=").append(url).append(">").append(url).append("</td></tr>").toString());
			}
			out.println("</table></p>");
			connection.close();
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
		}
		FormattedOut.printBottom(request, out);
		out.close();
	}
}
