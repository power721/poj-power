package com.pku.judgeonline.bbs;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ShowMessage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowMessage()
	{
	}

	public void init() throws ServletException
	{
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		long l = 0L;
		long cid = 0l;
		long pid = 0l;
		boolean flag = UserModel.isLoginned(request);
		boolean flag1 = UserModel.isAdminLoginned(request);
		try
		{
			l = Integer.parseInt(request.getParameter("message_id"));
		} catch (Exception exception)
		{
			ErrorProcess.Error("No such message", out);
			return;
		}
		Connection connection = null;
		PreparedStatement preparedstatement = null;
		PreparedStatement preparedstatement1 = null;
		ResultSet resultset = null;
		String s = "";
		String s1 = "";
		long l1 = 0L;
		try
		{
			connection = DBConfig.getConn();
			String s9 = "select * from message where message_id=?";
			if (!flag1)
				s9 = (new StringBuilder()).append(s9).append(" and UPPER(defunct) = 'N'").toString();
			preparedstatement = connection.prepareStatement(s9);
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				preparedstatement.close();
				connection.close();
				ErrorProcess.Error("No such message", out);
				return;
			}
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
		try
		{
			Timestamp timestamp = resultset.getTimestamp("in_date");
			long l3 = resultset.getLong("parent_id");
			String s3 = resultset.getString("user_id");
			s1 = resultset.getString("content");
			s = resultset.getString("title");
			if (Tool.isAdmin(connection, s3) == false)
			{
				s1 = Tool.htmlEncode(s1);
			}
			pid = l1 = resultset.getLong("problem_id");
			long l4 = resultset.getLong("depth") + 1L;
			long l5 = resultset.getLong("thread_id");
			long l6 = resultset.getLong("orderNum");
			preparedstatement.close();
			if (l1 != 0L)
				cid = Tool.problemInRunningContest(connection, l1);
			if (cid == 0L)
				FormattedOut.printHead(out, request, connection, "Detail of message");
			else
				FormattedOut.printContestHead(out, cid, "Detail of message", request);
			out.println("<table align=center border=0 width=99% background=images/table_back.jpg><tr><td>");
			out.println((new StringBuilder()).append("<center><h2><font color=blue>").append(Tool.titleEncode(s)).append("</font></h2></center>").toString());
			out.println((new StringBuilder()).append("Posted by <b><a href=userstatus?user_id=").append(s3).append("><font color=blue>").append(s3).append("</font></a></b>").toString());
			out.println((new StringBuilder()).append("at ").append(timestamp).toString());
			if (l1 != 0L)
			{
				if (cid != 0l)
				{
					pid = Tool.getContestPid(connection, cid, l1);
					out.print((new StringBuilder()).append("on <b><a href=showproblem?problem_id=").append(pid).append("&contest_id=").append(cid).append("><font color=black>Problem ").append(cid).append(":").append((char) (pid + 65)).append("</font></a></b>").toString());
				} else
					out.println((new StringBuilder()).append("on <b><a href=showproblem?problem_id=").append(l1).append("><font color=black>Problem ").append(l1).append("</font></a></b>").toString());
			}
			if (l3 != 0L)
			{
				preparedstatement = connection.prepareStatement("select * from message where message_id=?");
				preparedstatement.setLong(1, l3);
				resultset = preparedstatement.executeQuery();
				if (resultset.next())
				{
					String s4 = resultset.getString("title");
					String s5 = resultset.getString("user_id");
					Timestamp timestamp1 = resultset.getTimestamp("in_date");
					out.println((new StringBuilder()).append("<br>In Reply To:<a href=showmessage?message_id=").append(l3).append("><font color=blue>").append(Tool.titleEncode(s4)).append("</font></a>").toString());
					out.println((new StringBuilder()).append("Posted by:<b><a href=userstatus?user_id=").append(s5).append("><font color=black>").append(s5).append("</font></a></b>").toString());
					out.println((new StringBuilder()).append("at ").append(timestamp1).toString());
				}
			}
			preparedstatement.close();
			out.println("<HR noshade color=#FFFFFF><pre>");
			out.println(s1);
			out.println("</pre><HR noshade color=#FFFFFF>");
			out.println("<b>Followed by:</b><br>");
			long l9 = l4;
			out.println("<ul>");
			String s8 = "select * from message where thread_id=? and orderNum>?";
			if (!flag1)
				s8 = (new StringBuilder()).append(s8).append(" and UPPER(defunct) = 'N'").toString();
			s8 = (new StringBuilder()).append(s8).append("  order by orderNum").toString();
			preparedstatement = connection.prepareStatement(s8);
			preparedstatement.setLong(1, l5);
			preparedstatement.setLong(2, l6);
			resultset = preparedstatement.executeQuery();
			do
			{
				if (!resultset.next())
					break;
				String s6 = resultset.getString("user_id");
				long l11 = resultset.getLong("message_id");
				String s7 = resultset.getString("title");
				Timestamp timestamp2 = resultset.getTimestamp("in_date");
				long l8 = resultset.getLong("depth");
				preparedstatement1 = connection.prepareStatement("UPDATE message SET view = view+1 where message_id =" + l11);
				preparedstatement1.executeUpdate();
				if (l8 < l9)
					break;
				for (long l12 = l4; l12 < l8; l12++)
					out.print("<ul>");

				for (long l13 = l8; l13 < l4; l13++)
					out.print("</ul>");

				out.println((new StringBuilder()).append("<li><a href=showmessage?message_id=").append(l11).append("><font color=blue>").append(Tool.titleEncode(s7)).append("</font></a>").toString());
				out.println((new StringBuilder()).append(" -- <b><a href=userstatus?user_id=").append(s6).append("><font color=black>").append(s6).append("</font></a></b>").toString());
				out.println(timestamp2);
				l4 = l8;

			} while (true);
			for (long l10 = l9; l10 < l4; l10++)
				out.print("</ul>");

			out.print("</ul>");
			out.println("<HR noshade color=#FFFFFF>");
			preparedstatement.close();
			connection.close();
		} catch (Exception exception2)
		{
			ErrorProcess.ExceptionHandle(exception2, out);
			return;
		}
		out.println("<font color=blue>Post your reply here:</font><br>");
		out.println("<form method=POST action=post>");
		out.println((new StringBuilder()).append("<input type=hidden name=problem_id value=").append(pid).append(">").toString());
		out.println((new StringBuilder()).append("<input type=hidden name=contest_id value=").append(cid).append(">").toString());
		out.println((new StringBuilder()).append("<input type=hidden name=parent_id value=").append(l).append(">").toString());
		if (!flag)
		{
			out.println("User ID: <input type=text name=user_id size=22><br>");
			out.println("Password:<input type=password name=password size=22><br>");
		}
		if (s.length() < 3 || !s.substring(0, 3).toLowerCase().equals("re:"))
			s = (new StringBuilder()).append("Re:").append(s).toString();
		out.println((new StringBuilder()).append("Title:<br><input type=text name=title value=\"").append(Tool.titleEncode(s)).append("\" size=75><br>").toString());
		out.println("Content:<br>");
		out.println((new StringBuilder()).append("<textarea rows=15 name=content cols=75>").append(Tool.getReplyString(Tool.htmlEncode(s1))).append("</textarea><br>").toString());
		out.println("<input type=Submit value=reply name=B1>");
		out.println("</td></tr></table>");
		FormattedOut.printBottom(request, out);
	}

	public void destroy()
	{
	}
}
