package com.pku.judgeonline.contest;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ShowContest extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowContest()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		String s;
		long l;
		Connection connection;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		s = request.getParameter("contest_id");
		if (s == null || s.trim().equals(""))
		{
			ErrorProcess.Error("No such contest", out);
			return;
		}
		l = 0L;
		try
		{
			l = Integer.parseInt(s);
		} catch (Exception exception)
		{
			ErrorProcess.Error("No such contest", response.getWriter());
			return;
		}
		String str = request.getParameter("contest_id");
		connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		PreparedStatement preparedstatement2;
		ResultSet resultset2;
		try
		{
			preparedstatement = connection.prepareStatement("select title as ctitle ,start_time,end_time,description,private from contest where contest_id=? and UPPER(contest.defunct) = 'N'");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error("No such contest", out);
				preparedstatement.close();
				preparedstatement = null;
				connection.close();
				connection = null;
				return;
			}

			int ii = resultset.getInt("private");
			boolean flagc = (boolean) (ii > 0);
			if (flagc)
			{
				if (!Tool.permission(connection, request, l))
				{
					ErrorProcess.Error("It's a private contest.You have no permission.", out);
					preparedstatement.close();
					preparedstatement = null;
					connection.close();
					connection = null;
					return;
				}
			}
			String s1 = resultset.getString("ctitle");
			long l1 = System.currentTimeMillis();
			// long start_time = resultset.getTimestamp("start_time").getTime();
			FormattedOut.printContestHead(out, l, s1, request);

			boolean flag2 = resultset.getTimestamp("start_time").getTime() <= l1;
			boolean flag3 = resultset.getTimestamp("end_time").getTime() <= l1;
			boolean flag_freeze = resultset.getTimestamp("end_time").getTime() <= l1+3600000;
			out.println("<p align=\"center\"><b><font size=5 face=\"Arial\">" + (flagc ? "Private " : "Public ") + "Contest - " + s1 + "</font></b></p>");
			boolean flag4 = UserModel.isAdminLoginned(request);
			if (flag4)
				out.println((new StringBuilder()).append("&nbsp;&nbsp;<a href='admin.contestmanagerpage?contest_id=").append(s).append("'>Manager</a>").toString());
			out.println((new StringBuilder()).append("<font face=Arial><p align=\"center\">Start time:&nbsp;&nbsp;<font color=\"#993399\">").append(resultset.getTimestamp("start_time")).append("</font>&nbsp;&nbsp;End time:&nbsp;&nbsp;<font color=\"#993399\">").append(resultset.getTimestamp("end_time")).append("</font><br>Current System Time:&nbsp;&nbsp;<font color=\"#993399\" ><span id=\"cur_time\">").append(new Timestamp(l1)).append("</span></font>&nbsp;&nbsp;Contest Status:&nbsp;&nbsp;<font color=red>").toString());

			out.println("<script type=\"text/javascript\" language=\"javascript\">");
			out.println("var timeDiff = " + l1 + " - new Date().valueOf();");
			out.println("	");
			out.println("	function updateTime() {");
			out.println("		$(\"#cur_time\").html(new Date(new Date().valueOf() + timeDiff).format(\"yyyy-MM-dd hh:mm:ss\"));");
			out.println("	}");
			out.println("	updateTime();");
			out.println("	setInterval(updateTime, 1000);");
			out.println("</script>");

			if (!flag2)
				out.print("Pending");
			if (flag2 && !flag3)
			{
				out.print("Running");
				if(flag_freeze)
					Tool.freeze_board(connection, l);
			}
			if (flag3)
				out.print("Ended");
			out.println("</font></p>");
			out.println("</font>");
			String s2 = resultset.getString("description");
			preparedstatement.close();
			if (flag2)
			{
				out.println("<div align=\"center\">");
				if (s2 != null)
					out.println(s2);
				out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=750 border=1 background=images/table_back.jpg>");
				out.println("<tr bgcolor=#6589D1>");
				if (UserModel.isLoginned(request))
				{
					out.println("<td width=\"6%\" align=\"center\">&nbsp;</td>");
					out.println("<td width=\"17%\" align=\"center\"><b>Problem Id</b></td>");
					out.println("<td width=\"30%\" align=\"center\"><b>Title</b></td>");
					out.println("<td width=\"12%\" align=\"center\"><b>Ratio(AC/Submit)</b></td>");
				} else
				{
					out.println("<td width=\"20%\" align=\"center\"><b>Problem Id</b></td>");
					out.println("<td width=\"30%\" align=\"center\"><b>Title</b></td>");
					out.println("<td width=\"10%\" align=\"center\"><b>Ratio(AC/Submit)</b></td>");
				}
				out.println("</tr>");
				PreparedStatement preparedstatement1;
				if (UserModel.isLoginned(request))
				{
					preparedstatement1 = connection.prepareStatement("select contest_problem.problem_id,contest_problem.num,title,status from contest_problem left outer join (select problem_id ,min(result)as status from solution where user_id=? and contest_id=? group by problem_id )as temp on(contest_problem.problem_id=temp.problem_id)where  contest_problem.contest_id=? order by num");
					String s3 = UserModel.getCurrentUser(request).getUser_id();
					preparedstatement1.setString(1, s3);
					preparedstatement1.setLong(2, l);
					preparedstatement1.setLong(3, l);
				} else
				{
					preparedstatement1 = connection.prepareStatement("select problem_id,title,num from contest_problem where contest_id=? order by num");
					preparedstatement1.setLong(1, l);
				}
				ResultSet resultset1 = preparedstatement1.executeQuery();
				String s4;
				for (; resultset1.next();)
				{
					s4 = resultset1.getString("problem_id");
					int j = resultset1.getInt("num");
					preparedstatement2 = connection.prepareStatement("select count(if(result=0,1,null)) as accepted,count(*) as submit from solution where contest_id=" + l + " and problem_id=" + s4);
					resultset2 = preparedstatement2.executeQuery();
					resultset2.next();
					out.println("<tr>");
					if (UserModel.isLoginned(request))
					{
						out.println("<td align=center>");
						int k = resultset1.getInt("status");
						if (resultset1.wasNull())
							out.print("&nbsp;");
						else if (k == 0)
							out.print((new StringBuilder()).append("<img border=0 src=\"images/pass.gif\" width = 40>").toString());
						else
							out.print((new StringBuilder()).append("<img border=0 src=\"images/notpass.gif\" >").toString());
						out.println("</td>");
					}
					out.println("<td  align=\"center\"><b>" + (flag4 ? s4 : "") + " Problem " + (char) (j + 65) + "</b></td>");

					out.println((new StringBuilder()).append("<td  align=\"left\"><a href=\"showproblem?problem_id=").append(j).append("&contest_id=" + str + "\">").append(resultset1.getString("title")).append("</a></td>").toString());

					long l4 = resultset2.getLong("submit");
					long l5 = resultset2.getLong("accepted");
					long i1 = 0;
					if (l4 != 0l)
						i1 = 100 * l5 / l4;
					String s5;
					if (l4 != 0L)
						s5 = (new StringBuilder()).append(i1).append("%(<a href=conteststatus?contest_id=" + l + "&result=0&problem_id=").append(j).append(">").append(l5).append("</a>/<a href=conteststatus?contest_id=" + l + "&problem_id=").append(j).append(">").append(l4).append("</a>)").toString();
					else
						s5 = (new StringBuilder()).append(l5).append("/").append(l4).toString();
					out.println((new StringBuilder()).append("<td align=center>").append(s5).append("</td></tr>").toString());
				}

				out.println("</table></div>");
				out.print((new StringBuilder()).append("<p align=\"center\">[<a href=conteststanding?contest_id=").append(s).append(">Standings</a>]").toString());
				out.print((new StringBuilder()).append("&nbsp;&nbsp;[<a href=conteststatus?contest_id=").append(s).append(">Status</a>]").toString());
				out.print((new StringBuilder()).append("&nbsp;&nbsp;[<a href=conteststatistics?contest_id=").append(s).append(">Statistics</a>]</p>").toString());
				preparedstatement1.close();
			}
			connection.close();
			FormattedOut.printBottom(request, out);
		} catch (SQLException sqlexception)
		{
			ErrorProcess.ExceptionHandle(sqlexception, out);
		}
		return;
	}

	public void destroy()
	{
	}
}
