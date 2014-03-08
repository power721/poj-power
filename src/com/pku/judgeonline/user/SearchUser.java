package com.pku.judgeonline.user;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class SearchUser extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SearchUser()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		String s;
		String s1;
		String s2;
		String s3;
		long l;
		int i;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		s = request.getParameter("user_id");
		s3 = s;
		s1 = request.getParameter("orderby");
		s2 = request.getParameter("position");
		String s4 = request.getParameter("manner");
		i = 0;
		i = Integer.parseInt(s4);
		l = 0L;
		try
		{
			l = Long.parseLong(request.getParameter("length"));
		} catch (Exception exception)
		{
			l = 0L;
		}
		if (s1 != null && s1.equals("user_id"))
			s1 = " order by user_id";
		else
			s1 = " ORDER BY solved DESC, submit ASC";
		try
		{
			if (!Guard.Guarder(request, response, out))
				return;
			Connection connection;
			connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, (new StringBuilder()).append("Search ").append(s).toString());
			if (s == null || s.trim().length() < 2)
			{
				connection.close();
				connection = null;
				FormattedOut.printBottom(request, out);
				return;
			}
			PreparedStatement preparedstatement;
			ResultSet resultset;
			String s5;
			if (i == 0)
				s5 = "select * from users WHERE (user_id like ? or nick like ? or email like ? or school like ?) and UPPER(defunct) = 'N' ";
			else if (i == 1)
				s5 = "select * from users WHERE (user_id like ?) and UPPER(defunct) = 'N' ";
			else if (i == 2)
				s5 = "select * from users WHERE (nick like ? ) and UPPER(defunct) = 'N' ";
			else if (i == 3)
				s5 = "select * from users WHERE (email like ?) and UPPER(defunct) = 'N' ";
			else
				s5 = "select * from users WHERE (school like ?) and UPPER(defunct) = 'N' ";
			if (l > 0L)
				s5 = (new StringBuilder()).append(s5).append(" and length(user_id)=").append(l).append(" ").toString();
			if (s2 == null)
				s = (new StringBuilder()).append("%").append(s).append("%").toString();
			else if (s2.equalsIgnoreCase("begin"))
				s = (new StringBuilder()).append(s).append("%").toString();
			else
				s = (new StringBuilder()).append("%").append(s).toString();
			preparedstatement = connection.prepareStatement((new StringBuilder()).append(s5).append(s1).toString());
			preparedstatement.setString(1, s);
			if (i == 0)
			{
				preparedstatement.setString(2, s);
				preparedstatement.setString(3, s);
				preparedstatement.setString(4, s);
			}
			resultset = preparedstatement.executeQuery();
			/*if (!resultset.next())
			{
				out.println("<div id=search class=error align=center>Sorry,the user doesn't exist.</div>");
				resultset.close();
				preparedstatement.close();
				connection.close();
				FormattedOut.printBottom(request, out);
				return;
			}*/

			try
			{
				out.println("<center>");
				out.println("<font size=5 color=blue>Search Result</font><br>");
				out.print((new StringBuilder()).append("<form method=GET action=searchuser><input type=text name=user_id size=20 value=\"").append(s3).append((new StringBuilder()).append("\"><select size=1 name=manner><option value=0 ").append(i != 0 ? "" : " selected").append(">All</option><option value=1 ").append(i != 1 ? "" : " selected").append(">User</option><option value=2 ").append(i != 2 ? "" : " selected").append(">Nick</option><option value=3 ").append(i != 3 ? "" : " selected").append(">Email</option><option value=4 ").append(i != 4 ? "" : " selected").append(">School</option></select><input type=Submit value=Search ></form>").toString()).toString());
				out.println("<table border=1 width=88%>");
				out.println("<tr BGCOLOR=#78C8FF><td width=10%>No.</td><td width=20%>User</td><td width=25%>Nick Name</td><td width=15%>School</td><td width=15%>Solved</td>");
				out.println("<td width=15%>Submissions</td></tr>");
				int j = 1;
				while (resultset.next())
				{
					String s6;
					if (j % 2 == 1)
						s6 = "#C0C0C0";
					else
						s6 = "#78C8FF";
					out.println((new StringBuilder()).append("<tr bgcolor=").append(s6).append("><td width=10%>").append(j).append("</td>").toString());
					out.println((new StringBuilder()).append("<td><a href=userstatus?user_id=").append(resultset.getString("user_id")).append(">").append(resultset.getString("user_id")).append("</a></td>").toString());
					out.println((new StringBuilder()).append("<td>").append(resultset.getString("nick")).append("</td>").toString());
					out.println((new StringBuilder()).append("<td>").append(resultset.getString("school")).append("</td>").toString());
					out.println((new StringBuilder()).append("<td>").append(resultset.getInt("solved")).append("</td>").toString());
					out.println((new StringBuilder()).append("<td>").append(resultset.getInt("submit")).append("</td></tr>").toString());
					j++;
				}
				out.println("</table>");
				out.println("</center>");
				resultset.close();
				preparedstatement.close();
				connection.close();
			} catch (SQLException sqlexception1)
			{
				sqlexception1.printStackTrace(out);
			}
		} catch (SQLException sqlexception)
		{
			sqlexception.printStackTrace(out);
		}
		FormattedOut.printBottom(request, out);
		return;
	}

	public void destroy()
	{
	}
}
