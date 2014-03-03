package com.pku.judgeonline.contest;

import com.pku.judgeonline.common.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class PastContests extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PastContests()
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
		try
		{
			Connection connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, "Contests");
			PreparedStatement preparedstatement = connection.prepareStatement("select * from contest where end_time<? and UPPER(defunct)='N' order by contest_id desc");
			preparedstatement.setTimestamp(1, ServerConfig.getSystemTime());
			ResultSet resultset = preparedstatement.executeQuery();
			out.println("<center>");
			out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=75% border=1 background=images/table_back.jpg>");
			out.println("<tr>");
			out.println("<td width=10% align=center><b>ID</b></td>");
			out.println("<td width=40% align=center><b>Title</b></td>");
			out.println("<td width=20% align=center><b>Start Time</b></td>");
			out.println("<td width=20% align=center><b>End Time</b></td>");
			out.println("<td width=10% align=center><b>Status</b></td></tr>");
			for (; resultset.next(); out.println("</tr>"))
			{
				String s = resultset.getString("contest_id");
				Timestamp timestamp = resultset.getTimestamp("start_time");
				Timestamp timestamp1 = resultset.getTimestamp("end_time");
				boolean flag1 = timestamp.getTime() < System.currentTimeMillis();
				boolean flag3 = timestamp1.getTime() < System.currentTimeMillis();
				out.println("<tr>");
				out.println((new StringBuilder()).append("<td align=center>").append(s).append("</td>").toString());
				out.println((new StringBuilder()).append("<td><a href=showcontest?contest_id=").append(s).append(">").append(resultset.getString("title")).append("</a></td>").toString());
				out.println((new StringBuilder()).append("<td align=center>").append(timestamp).append("</td>").toString());
				out.println((new StringBuilder()).append("<td align=center>").append(timestamp1).append("</td>").toString());
				if (flag3)
				{
					out.println("<td align=center><font color=green >ended</font></td>");
					continue;
				}
				if (flag1)
					out.println("<td align=center><font color=blue >RUNNING</font></td>");
				else
					out.println((new StringBuilder()).append("<td align=center><font color=red>Start at ").append(timestamp).append("</font></td>").toString());
			}

			out.println("</table></center>");
			preparedstatement.close();
			connection.close();
			FormattedOut.printBottom(request, out);
		} catch (Exception exception)
		{
			exception.printStackTrace(out);
		}
	}

	public void destroy()
	{
	}
}
