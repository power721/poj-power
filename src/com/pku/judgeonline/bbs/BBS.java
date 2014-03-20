package com.pku.judgeonline.bbs;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class BBS extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BBS()
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
		long l = 0L;
		long l1 = 0x5f5e0ffL;
		long l2 = 50L;
		long l3 = 0L;
		long pid = 0L, cid = 0L;
		String s = null;
		boolean flag = UserModel.isAdminLoginned(request);
		try
		{
			String s1 = request.getParameter("problem_id");
			if (s1 != null)
				pid = l = Integer.parseInt(s1);
			if (request.getParameter("contest_id") != null)
				cid = Integer.parseInt(request.getParameter("contest_id"));
			s = request.getParameter("search");
			if (s == null)
				s = "";
			s = s.trim();
			String s2 = request.getParameter("manner");
			if (s2 != null)
				l3 = Integer.parseInt(s2);
			String s3 = request.getParameter("top");
			if (s3 != null)
				l1 = Integer.parseInt(s3);
			String s4 = request.getParameter("size");
			if (s4 != null)
				l2 = Integer.parseInt(s4);
		} catch (NumberFormatException numberformatexception)
		{
		}
		long l4 = 0L;
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement;
			ResultSet resultset;
			//PreparedStatement preparedstatement1;
			//ResultSet resultset1;
			if (cid != 0 && l <= 26)
			{
				preparedstatement = connection.prepareStatement("select problem_id from contest_problem where contest_id=? and num=?");
				preparedstatement.setLong(1, cid);
				preparedstatement.setLong(2, l);
				resultset = preparedstatement.executeQuery();
				if (!resultset.next())
				{
					ErrorProcess.Error((new StringBuilder()).append("Can not find contest problem (ID:").append(l).append(")<br><br>").toString(), out);
					preparedstatement.close();
					connection.close();
					return;
				}
				l = resultset.getInt("problem_id");
			}
			if (cid == 0L)
				FormattedOut.printHead(out, request, connection, "Messages");
			else
				FormattedOut.printContestHead(out, cid, "Messages", request);
			out.print("<script language=\"javascript\" type=\"text/javascript\">");
			out.print("$(document).ready(function(){");
			out.print("$(\"img[src$='comments.gif']\").click(function(){");
			out.print("if($(this).parent().children('ul').size()){if($(this).attr(\"src\")==\"./images/comments.gif\")");
			out.print("{");
			out.print("$(this).attr(\"src\",\"./images/op-comments.gif\")");
			out.print("}");
			out.print("else");
			out.print("{");
			out.print("$(this).attr(\"src\",\"./images/comments.gif\")");
			out.print("}};");
			out.print("$(this).parent().children(\"ul\").toggle(\"slow\");");
			out.print("});");
			out.print("});");
			out.print("</script>");
			String s5 = "select thread_id from message WHERE thread_id<?";
			if (l != 0L)
				s5 = (new StringBuilder()).append(s5).append(" and problem_id=? ").toString();
			if (!s.trim().equals(""))
				if (l3 == 0L)
					s5 = (new StringBuilder()).append(s5).append(" and (user_id=? or title like ? or content like ?) ").toString();
				else if (l3 == 1L)
					s5 = (new StringBuilder()).append(s5).append(" and user_id=? ").toString();
				else
					s5 = (new StringBuilder()).append(s5).append(" and title like ? ").toString();
			if (flag)
				s5 = (new StringBuilder()).append(s5).append(" order by thread_id desc limit ").append(l2).toString();
			else
				s5 = (new StringBuilder()).append(s5).append(" and UPPER(defunct) = 'N' order by thread_id desc limit ").append(l2).toString();
			preparedstatement = connection.prepareStatement((new StringBuilder()).append("select min(thread_id) as mint from (").append(s5).append(") as temp").toString());
			int i = 0;
			preparedstatement.setLong(++i, l1);
			if (l != 0L)
				preparedstatement.setLong(++i, l);
			if (!s.equals(""))
			{
				preparedstatement.setString(++i, s);
				if (l3 == 0L)
				{
					preparedstatement.setString(++i, (new StringBuilder()).append("%").append(s).append("%").toString());
					preparedstatement.setString(++i, (new StringBuilder()).append("%").append(s).append("%").toString());
				}
			}
			resultset = preparedstatement.executeQuery();
			if (resultset.next())
				l4 = resultset.getLong("mint");
			preparedstatement.close();
			s5 = "select title,depth,user_id,message_id,in_date,thread_id,problem_id,defunct from message WHERE thread_id<? and thread_id>=? ";
			if (l != 0L)
				s5 = (new StringBuilder()).append(s5).append(" and problem_id=? ").toString();
			if (!s.trim().equals(""))
				if (l3 == 0L)
					s5 = (new StringBuilder()).append(s5).append(" and (user_id=? or title like ? or content like ?) ").toString();
				else if (l3 == 1L)
					s5 = (new StringBuilder()).append(s5).append(" and user_id=? ").toString();
				else
					s5 = (new StringBuilder()).append(s5).append(" and title like ? ").toString();
			if (flag)
				s5 = (new StringBuilder()).append(s5).append(" order by thread_id desc,orderNum").toString();
			else
				s5 = (new StringBuilder()).append(s5).append(" and UPPER(defunct) = 'N' order by thread_id desc,orderNum").toString();
			preparedstatement = connection.prepareStatement(s5);
			i = 0;
			preparedstatement.setLong(++i, l1);
			preparedstatement.setLong(++i, l4);
			if (l != 0L)
				preparedstatement.setLong(++i, l);
			if (!s.equals(""))
			{
				if (l3 == 2L)
					preparedstatement.setString(++i, (new StringBuilder()).append("%").append(s).append("%").toString());
				else
					preparedstatement.setString(++i, s);
				if (l3 == 0L)
				{
					preparedstatement.setString(++i, (new StringBuilder()).append("%").append(s).append("%").toString());
					preparedstatement.setString(++i, (new StringBuilder()).append("%").append(s).append("%").toString());
				}
			}
			resultset = preparedstatement.executeQuery();
			long l5 = 0L;
			long l6 = 0L;
			out.print((new StringBuilder()).append("<form method=get action=bbs><input type=text name=search size=25 value=").append(s).append("><select size=1 name=manner><option value=0 ").append(l3 != 0L ? "" : " selected").append(">All</option><option value=1 ").append(l3 != 1L ? "" : " selected").append(">User</option><option value=2 ").append(l3 != 2L ? "" : " selected").append(">Title</option></select><input type=submit value=Search></p></form><a href=announcement><font size=4 color=red>Announcement</font></a>").toString());
			out.print("<table border=0 align=center width=99% background=images/table_back.jpg><tr><td><ul>");
			l1 = 0L;
			long l7 = 0L;
			while (resultset.next())
			{
				l6 = resultset.getLong("depth");
				String s6 = resultset.getString("title");
				String s7 = resultset.getString("user_id");
				String s8 = resultset.getString("defunct");
				long l10 = resultset.getLong("message_id");
				Timestamp timestamp = resultset.getTimestamp("in_date");
				long l11 = resultset.getLong("thread_id");
				if (l11 > l1)
					l1 = l11;
				long l12 = resultset.getLong("problem_id");
				if ("".equals(s))
				{
					for (long l13 = l5; l13 < l6; l13++)
						out.print("<ul>");

				}
				if ("".equals(s))
				{
					for (long l14 = l6; l14 < l5; l14++)
						out.print("</ul>");

				}
				if (l7 != 0L && l11 != l7 && l6 == 0L)
					out.print("<hr>");
				l7 = l11;
				out.print((new StringBuilder()).append("<li><img src=./images/comments.gif><a href=showmessage?message_id=").append(l10).append((new StringBuilder()).append("><font color=blue>").append(s8.equals("N") ? "" : "<del>").toString()).append(Tool.titleEncode(s6)).append((new StringBuilder()).append(s8.equals("N") ? "" : "</del>").append("</font></a> <b><a href=userstatus?user_id=").toString()).append(s7).append("><font color=black>").append(s7).append("</font></a></b> ").append(timestamp).toString());
				if (flag)
				{
					out.println((new StringBuilder()).append((new StringBuilder()).append("<a href=javascript:cfdel(").append(l10).append(",").append(s8.equals("N") ? 0 : 1).append(")>").append(s8.equals("N") ? "Hide" : "Resume").append("</a>").toString()).toString());
					if (l10 == l11)
						out.println((new StringBuilder()).append((new StringBuilder()).append("<a href=javascript:cfdel(").append(l10).append(",2)>Del</a>").toString()).toString());
				}
				if (l12 != 0L && l6 == 0L)
				{
					cid = Tool.problemInRunningContest(connection, l12);
					if (cid != 0l)
					{
						pid = Tool.getContestPid(connection, cid, l12);
						out.print((new StringBuilder()).append(" <b><a href=showproblem?problem_id=").append(pid).append("&contest_id=").append(cid).append("><font color=black>Problem ").append(cid).append(":").append((char) (pid + 65)).append("</font></a></b>").toString());
					} else
						out.print((new StringBuilder()).append(" <b><a href=showproblem?problem_id=").append(l12).append("><font color=black>Problem ").append(l12).append("</font></a></b>").toString());
				}
				l5 = l6;
			}
			preparedstatement.close();
			for (long l8 = 0L; l8 < l6; l8++)
				out.print("</ul>");

			out.print("</ul></td></tr></table><center>");
			s5 = "select thread_id from message WHERE thread_id>=? ";
			if (l != 0L)
				s5 = (new StringBuilder()).append(s5).append(" and problem_id=? ").toString();
			if (!s.trim().equals(""))
				if (l3 == 0L)
					s5 = (new StringBuilder()).append(s5).append(" and (user_id=? or title like ? or content like ?) ").toString();
				else if (l3 == 1L)
					s5 = (new StringBuilder()).append(s5).append(" and user_id=? ").toString();
				else
					s5 = (new StringBuilder()).append(s5).append(" and title like ? ").toString();
			if (flag)
				s5 = (new StringBuilder()).append(s5).append(" order by thread_id desc limit ").append(l2).toString();
			else
				s5 = (new StringBuilder()).append(s5).append(" and UPPER(defunct) = 'N' order by thread_id limit ").append(l2).toString();
			preparedstatement = connection.prepareStatement((new StringBuilder()).append("select max(thread_id) as maxt from (").append(s5).append(") as temp").toString());
			i = 0;
			preparedstatement.setLong(++i, l1);
			if (l != 0L)
				preparedstatement.setLong(++i, l);
			if (!s.equals(""))
			{
				preparedstatement.setString(++i, s);
				if (l3 == 0L)
				{
					preparedstatement.setString(++i, (new StringBuilder()).append("%").append(s).append("%").toString());
					preparedstatement.setString(++i, (new StringBuilder()).append("%").append(s).append("%").toString());
				}
			}
			resultset = preparedstatement.executeQuery();
			long l9 = 0x98967fL;
			if (resultset.next())
				l9 = resultset.getLong("maxt") + 1L;
			preparedstatement.close();
			String s9 = "";
			if (l != 0L)
				s9 = (new StringBuilder()).append(s9).append("?problem_id=").append(l).toString();
			out.print((new StringBuilder()).append("<hr>[<a href=bbs").append(s9).append(">Top</a>]").toString());
			s9 = (new StringBuilder()).append("?top=").append(l9).toString();
			if (l != 0L)
				s9 = (new StringBuilder()).append(s9).append("&problem_id=").append(l).toString();
			out.print((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;[<a href=bbs").append(s9).append(">Previous</a>]").toString());
			s9 = (new StringBuilder()).append("?top=").append(l4).toString();
			if (l != 0L)
				s9 = (new StringBuilder()).append(s9).append("&problem_id=").append(l).toString();
			out.print((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;[<a href=bbs").append(s9).append(">Next</a>]<br></center><form method=post action=postpage><input type=hidden name=problem_id value=").append(pid).append(">").append("<input type=hidden name=contest_id value=").append(cid).append("><input type=submit value='Post new message' name=B1></form>").toString());
			connection.close();

			FormattedOut.printBottom(request, out);
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
		}
	}

	public void destroy()
	{
	}
}
