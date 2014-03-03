package com.pku.judgeonline.bbs;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class SearchMessage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SearchMessage()
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
		@SuppressWarnings("unused") long ma = 0l;
		String str;
		String s = request.getParameter("problem_id");
		try
		{
			if (s != null)
				l = Integer.parseInt(s);
			String mannar = request.getParameter("mannar");
			if (mannar != null)
				ma = Integer.parseInt(mannar);
			String s1 = request.getParameter("top");
			if (s1 != null)
				l1 = Integer.parseInt(s1);
			String s2 = request.getParameter("size");
			if (s2 != null)
				l2 = Integer.parseInt(s2);
		} catch (NumberFormatException numberformatexception)
		{
		}
		str = request.getParameter("search");
		if (str == null)
			str = "";
		str = str.trim();
		long l3 = 0L;
		try
		{
			Connection connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, "Messages");
			String s3 = "select thread_id from message where thread_id<?";
			if (l != 0L)
				s3 = (new StringBuilder()).append(s3).append(" and problem_id=? ").toString();
			s3 = (new StringBuilder()).append(s3).append(" order by thread_id desc limit ").append(l2).toString();
			PreparedStatement preparedstatement = connection.prepareStatement((new StringBuilder()).append("select min(thread_id) as mint from (").append(s3).append(") as temp").toString());
			int pp = 0;
			preparedstatement.setLong(++pp, l1);
			if (l != 0L)
				preparedstatement.setLong(++pp, l);
			ResultSet resultset = preparedstatement.executeQuery();
			if (resultset.next())
				l3 = resultset.getLong("mint");
			preparedstatement.close();
			s3 = "select title,depth,user_id,message_id,in_date,thread_id,problem_id from message where thread_id<? and thread_id>=? ";
			if (l != 0L)
				s3 = (new StringBuilder()).append(s3).append(" and problem_id=? ").toString();
			s3 = (new StringBuilder()).append(s3).append(" order by thread_id desc,orderNum").toString();
			preparedstatement = connection.prepareStatement(s3);
			pp = 0;
			preparedstatement.setLong(++pp, l1);
			preparedstatement.setLong(++pp, l3);
			if (l != 0L)
				preparedstatement.setLong(++pp, l);
			resultset = preparedstatement.executeQuery();
			long l4 = 0L;
			long l5 = 0L;
			out.print("<table border=0 width=99% background=images/table_back.jpg><tr><td><ul>");
			l1 = 0L;
			long l6 = 0L;
			while (resultset.next())
			{
				l5 = resultset.getLong("depth");
				String s4 = resultset.getString("title");
				String s5 = resultset.getString("user_id");
				long l9 = resultset.getLong("message_id");
				Timestamp timestamp = resultset.getTimestamp("in_date");
				long l10 = resultset.getLong("thread_id");
				if (l10 > l1)
					l1 = l10;
				long l11 = resultset.getLong("problem_id");
				for (long l12 = l4; l12 < l5; l12++)
					out.print("<ul>");

				for (long l13 = l5; l13 < l4; l13++)
					out.print("</ul>");

				if (l6 != 0L && l10 != l6 && l5 == 0L)
					out.print("<hr>");
				l6 = l10;
				out.print((new StringBuilder()).append("<li><img src=./images/comments.gif><a href=showmessage?message_id=").append(l9).append("><font color=blue>").append(Tool.titleEncode(s4)).append("</font></a> <b><a href=userstatus?user_id=").append(s5).append("><font color=black>").append(s5).append("</font></a></b> ").append(timestamp).toString());
				boolean flag = UserModel.isAdminLoginned(request);
				if (flag)
					out.println((new StringBuilder()).append("<a href=\"javascript:void(0);\"onclick=\"if(confirm('Are you sure?'))location='delmessage?message_id=").append(l9).append("'\"><b>Del</b></a>\n").toString());
				if (l11 != 0L && l5 == 0L)
					out.print((new StringBuilder()).append(" <b><a href=showproblem?problem_id=").append(l11).append("><font color=black>Problem ").append(l11).append("</font></a></b>").toString());
				l4 = l5;
			}
			preparedstatement.close();
			for (long l7 = 0L; l7 < l5; l7++)
				out.print("</ul>");

			out.print("</ul></td></tr></table><center>");
			s3 = "select thread_id from message where thread_id>=? ";
			if (l != 0L)
				s3 = (new StringBuilder()).append(s3).append(" and problem_id=? ").toString();
			s3 = (new StringBuilder()).append(s3).append(" order by thread_id limit ").append(l2).toString();
			preparedstatement = connection.prepareStatement((new StringBuilder()).append("select max(thread_id) as maxt from (").append(s3).append(") as temp").toString());
			pp = 0;
			preparedstatement.setLong(++pp, l1);
			if (l != 0L)
				preparedstatement.setLong(++pp, l);
			resultset = preparedstatement.executeQuery();
			long l8 = 0x98967fL;
			if (resultset.next())
				l8 = resultset.getLong("maxt") + 1L;
			preparedstatement.close();
			String s6 = "";
			if (l != 0L)
				s6 = (new StringBuilder()).append(s6).append("?problem_id=").append(l).toString();
			out.print((new StringBuilder()).append("<hr>[<a href=bbs").append(s6).append(">Top</a>]").toString());
			s6 = (new StringBuilder()).append("?top=").append(l8).toString();
			if (l != 0L)
				s6 = (new StringBuilder()).append(s6).append("&problem_id=").append(l).toString();
			out.print((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;[<a href=SearchMessage").append(s6).append(">Previous</a>]").toString());
			s6 = (new StringBuilder()).append("?top=").append(l3).toString();
			if (l != 0L)
				s6 = (new StringBuilder()).append(s6).append("&problem_id=").append(l).toString();
			out.print((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;[<a href=SearchMessage").append(s6).append(">Next</a>]<br></center><form method=post action=postpage><input type=hidden name=problem_id value=").append(l).append("><input type=submit value='Post new message' name=B1></form>").toString());
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
