package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class UserListServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserListServlet()
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
		String s = null;
		String s1 = null;
		String s2 = null;
		String s3 = null;
		s = request.getParameter("of1");
		s1 = request.getParameter("od1");
		s2 = request.getParameter("of2");
		s3 = request.getParameter("od2");

		if (s == null || !s.equals("submit") && !s.equals("ratio"))
			s = "solved";
		
		if (s1 == null || !s1.equals("desc") && !s1.equals("asc"))
			s1 = "desc";
		String s4 = s;
		if (s.equals("ratio"))
			s = "solved/submit";
		if (s2 == null || !s2.equals("solved") && !s2.equals("submit") && !s2.equals("ratio"))
			s2 = "submit";
		if (s3 == null || !s3.equals("desc") && !s3.equals("asc"))
			s3 = "asc";
		String s5 = s2;
		if (s2.equals("ratio"))
			s2 = "solved/submit";
		long l;
		try
		{
			l = Integer.parseInt(request.getParameter("start"));
		} catch (NumberFormatException numberformatexception)
		{
			l = 0L;
		}
		if (l < 0L)
			l = 0L;
		long l1;
		try
		{
			l1 = Integer.parseInt(request.getParameter("size"));
		} catch (NumberFormatException numberformatexception1)
		{
			l1 = 50L;
		}
		if (l1 < 1L)
			l1 = 50L;
		if (l1 > 500L)
			l1 = 500L;
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement2;
			ResultSet resultset2;
			FormattedOut.printHead(out, request, connection, "User List");
			PreparedStatement preparedstatement = connection.prepareStatement("SELECT COUNT(*) AS total FROM users WHERE UPPER(defunct) = 'N'");
			ResultSet resultset = preparedstatement.executeQuery();
			resultset.first();
			long l2 = resultset.getLong("total");
			resultset.close();
			preparedstatement.close();
			out.println("<center><img src='./images/user.gif' alt='user'></center><br>");
			out.println("<p align=center><font size=5 color=#333399>User List</font></p>");
			out.print("<div align=center><table width=80% align=center><tr><td width=33%><font size=5><a href=onlineusers>Online Users</a></font></td><td width=34% align=center><font size=5><a href=recentrank>Recent Ranklist</a></font></td><td width=33% align=right><font size=5><a href=newuser>New Users</a></font></td></tr></table></div>");
			out.println("<TABLE cellSpacing=0 cellPadding=0 align=center width=99% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.println("<tr align=center bgcolor=#6589D1>");
			out.println("<th width=\"5%\" ><b>No.</b></th>");
			out.println("<th width=\"20%\" ><b>User ID</b></th>");
			out.println("<th width=\"45%\" ><b>Nick Name</b></th>");
			//out.println("<td width=\"10%\" ><a href=userlist><b><font color=white>Scores</font></b></a></td>");
			out.println("<th width=\"10%\" ><a href=userlist?of1=solved&od1=desc&of2=score&od2=asc><b><font color=white>Solved Problems</font></b></a></th>");
			out.println("<th width=\"10%\" ><a href=userlist?of1=submit&od1=desc&of2=solved&od2=asc><b><font color=white>Submit</font></b></a></th>");
			out.println("<th width=\"10%\"><a href=userlist?of1=ratio&od1=desc&of2=solved&od2=desc><b><font color=white>Ratio (AC/submit)</font></b></a></th>");
			out.println("</tr>");
			try
			{
				int i = 1;
				String sql = (new StringBuilder()).append("SELECT * FROM users WHERE UPPER(defunct) = 'N' ORDER BY ").append(/*s == "score" ? "solved" : */s).append(" ").append(s1).append(",").append(s2).append(" ").append(s3).append(" limit ?,?").toString();
				
				preparedstatement = connection.prepareStatement(sql);
				preparedstatement.setLong(1, l);
				preparedstatement.setLong(2, l1);
				resultset = preparedstatement.executeQuery();
				resultset.close();
				preparedstatement.close();
				
				preparedstatement = connection.prepareStatement((new StringBuilder()).append("SELECT * FROM users WHERE UPPER(defunct) = 'N' ORDER BY ").append(s).append(" ").append(s1).append(",").append(s2).append(" ").append(s3).append(" limit ?,?").toString());
				preparedstatement.setLong(1, l);
				preparedstatement.setLong(2, l1);
				resultset = preparedstatement.executeQuery();

				for (; resultset.next(); out.println("</tr>"))
				{
					out.println("<tr align=center>");
					long l3 = l + (long) (i++);
					String s6 = resultset.getString("user_id");
					int k = resultset.getInt("solved");
					int i1 = resultset.getInt("submit");
					String s7 = resultset.getString("nick");
					//String sco = null;
					int j1 = 0;

					if (i1 != 0)
						j1 = (k * 100) / i1;

					preparedstatement2 = connection.prepareStatement("select * from privilege where user_id=? and (UPPER(rightstr)='ADMINISTRATOR' or rightstr='MEMBER')");
					preparedstatement2.setString(1, s6);
					resultset2 = preparedstatement2.executeQuery();

					out.println((new StringBuilder()).append("<td width=\"5%\" align=\"center\">").append(l3).append("</td>").toString());
					if (resultset2.next())
					{
						if (resultset2.getString("rightstr").equalsIgnoreCase("Administrator"))
							out.println((new StringBuilder()).append("<td class=\"user\"><a href=userstatus?user_id=").append(s6).append("><font color=red>").append(s6).append("</font></a></td>").toString());
						else
							out.println((new StringBuilder()).append("<td class=\"user\"><a href=userstatus?user_id=").append(s6).append("><b>").append(s6).append("</b></a></td>").toString());
					} else
						out.println((new StringBuilder()).append("<td class=\"user\"><a href=userstatus?user_id=").append(s6).append(">").append(s6).append("</a></td>").toString());
					
					out.println((new StringBuilder()).append("<td class=\"nick\"><font color=green>").append(Tool.titleEncode(connection, s6, s7)).append("</font></td>").toString());
					out.println((new StringBuilder()).append("<td><a href=status?result=0&user_id=").append(s6).append(">").append(k).append("</a></td>").toString());
					out.println((new StringBuilder()).append("<td><a href=status?user_id=").append(s6).append(">").append(i1).append("</a></td>").toString());
					out.println((new StringBuilder()).append("<td>").append(j1).append("%</td>").toString());
					resultset2.close();
					preparedstatement2.close();
				}
				resultset.close();
				preparedstatement.close();
				out.println("</table>");
				out.println("<p align=\"center\">");
				long l4 = (l2 - 1L) / l1 + 1L;
				for (int j = 0; (long) j < l4; j++)
					out.println((new StringBuilder()).append("<a href=userlist?start=").append((long) j * l1).append("&size=").append(l1).append("&of1=").append(s4).append("&od1=").append(s1).append("&of2=").append(s5).append("&od2=").append(s3).append(">").append((long) j * l1 + 1L).append("-").append((long) (j + 1) * l1).append("</a>&nbsp;").toString());

				out.println("</p>");
			} catch (Exception exception1)
			{
				exception1.printStackTrace(System.err);
			}
			connection.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
		}
		FormattedOut.printBottom(request, out);
	}

	public void destroy()
	{
	}
}
