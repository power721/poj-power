package com.pku.judgeonline.admin.contest;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ContestList extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContestList()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		try
		{
			Connection connection = DBConfig.getConn();
			FormattedOut.printHead(out, "Contests");
			PreparedStatement preparedstatement = connection.prepareStatement("select * from contest where end_time>? and UPPER(defunct)='N' order by contest_id");
			preparedstatement.setTimestamp(1, ServerConfig.getSystemTime());
			ResultSet resultset = preparedstatement.executeQuery();
			out.println("<center>");
			out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=700 border=1 background=images/table_back.jpg>");
			out.println("<tr><td width=10% align=center>ID.</td>");
			out.println("<td width=40% align=center>Name</td>");
			out.println("<td width=25% align=center>Status</td>");
			out.println("<td width=25% align=center>Manage</td></tr>");
			for (; resultset.next(); out.println("</tr>"))
			{
				String s = resultset.getString("contest_id");
				Timestamp timestamp = resultset.getTimestamp("start_time");
				boolean flag1 = timestamp.getTime() < System.currentTimeMillis();
				out.println("<tr><td align=center>" + s + "</td>");
				out.println((new StringBuilder()).append("<td align=center><a href=admin.contestmanagerpage?contest_id=").append(s).append(">").append(resultset.getString("title")).append("</a></td>").toString());
				if (flag1)
					out.println("<td align=center align=center><font color=blue >RUNNING</font></td>");
				else
					out.println((new StringBuilder()).append("<td align=center><font color=red>Start at ").append(timestamp).append("</font></td>").toString());
				out.println((new StringBuilder()).append("<td align=center><a href=admin.editcontestpage?contest_id=").append(s).append(">Edit</a></td>").toString());
			}

			out.println("</table></center>");
			preparedstatement.close();
			connection.close();
			FormattedOut.printBottom(out);
			out.close();
		} catch (Exception exception)
		{
			exception.printStackTrace(out);
		}
	}

	public void destroy()
	{
	}
}
