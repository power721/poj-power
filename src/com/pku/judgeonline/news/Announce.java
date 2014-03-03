package com.pku.judgeonline.news;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Announce extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Announce()
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
			FormattedOut.printHead(out, request, connection, "Announcement");
			boolean flags = UserModel.isAdminLoginned(request);
			PreparedStatement preparedstatement;
			if (flags)
			{
				preparedstatement = connection.prepareStatement("select * from announce order by id desc");
			} else
			{
				preparedstatement = connection.prepareStatement("select * from announce where start_time<? and UPPER(defunct)='N' order by id desc");
				preparedstatement.setTimestamp(1, ServerConfig.getSystemTime());
			}
			ResultSet resultset = preparedstatement.executeQuery();
			out.println("<center>");
			if (flags)
			{
				out.println("<br><center><a href=admin.addannouncepage>Add a Announcement</a></center><br>");
			}
			out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=80% border=1 background=images/table_back.jpg>");
			out.println("<tr><td width=10% align=center>ID.</td><td width=60% align=center>Title</td>");
			out.println("<td width=20% align=center>Publisher</td>");

			if (flags)
				out.println("<td width=10% align=center>Admin</td>");
			out.println("</tr>");
			for (; resultset.next(); out.println("</tr>"))
			{
				String s = resultset.getString("id");
				String user = resultset.getString("user_id");
				out.println("<tr><td width=10% align=center>" + s + "</td>");
				out.println((new StringBuilder()).append("<td><a href=showannounce?announce_id=").append(s).append(">").append(resultset.getString("title")).append("</a></td>").toString());
				out.println("<td align=center><a href=sendpage?to=" + user + ">" + user + "</a></td>");
				if (flags)
					out.println("<td width=10% align=center><a href='admin.editannouncepage?announce_id=" + s + "'>Edit</a></td>");
			}

			out.println("</table></center>");
			if (flags)
			{
				out.println("<br><center><a href=admin.addannouncepage>Add a Announcement</a></center><br>");
			}
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
