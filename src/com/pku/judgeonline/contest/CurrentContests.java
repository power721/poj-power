package com.pku.judgeonline.contest;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class CurrentContests extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CurrentContests()
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
		try
		{
			Connection connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, "CurrentContests");
			PreparedStatement preparedstatement = connection.prepareStatement("select * from contest where start_time<? and end_time>? and UPPER(defunct)='N' order by contest_id");
			preparedstatement.setTimestamp(1, ServerConfig.getSystemTime());
			preparedstatement.setTimestamp(2, ServerConfig.getSystemTime());
			ResultSet resultset = preparedstatement.executeQuery();
			out.println("<center>");
			out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=700 border=1 background=images/table_back.jpg>");
			out.println("<tr><td width=40%>Title</td>");
			out.println("<td width=40% align=center>Status</td><td width=20% align=center>Type</td></tr>");
			for (; resultset.next(); out.println("</tr>"))
			{
				String s = resultset.getString("contest_id");
				Timestamp timestamp = resultset.getTimestamp("start_time");
				// boolean flag1 = timestamp.getTime() <
				// System.currentTimeMillis();
				out.println((new StringBuilder()).append("<tr><td><a href=showcontest?contest_id=").append(s).append(">").append(resultset.getString("title")).append("</a></td>").toString());
				// if (flag1)
				out.println("<td align=center><font color=blue >" + timestamp + "-" + resultset.getTimestamp("end_time") + "</font></td>");
				// else
				// out.println((new
				// StringBuilder()).append("<td align=center><font color=red>Start at ").append(timestamp).append("</font></td>").toString());
				int ii = resultset.getInt("private");
				boolean flagc = (boolean) (ii > 0);
				out.println("<td align=center><font color=blue>" + (flagc ? "Private" : "Public") + "</font></td>");
			}

			out.println("</table></center>");
			preparedstatement.close();
			connection.close();
			FormattedOut.printBottom(request, out);
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
