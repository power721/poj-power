package com.pku.judgeonline.contest;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ContestStanding extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContestStanding()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		long l;
		long l1;
		boolean flag;
		HashMap<String, Object> hashmap;
		boolean flag1;
		boolean flag2;
		Connection connection;
		PreparedStatement preparedstatement;
		ResultSet resultset;
		long l2;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		l1 = 0L;
		String str = request.getParameter("contest_id");
		try
		{
			l = Integer.parseInt(str);
		} catch (Exception exception)
		{
			ErrorProcess.Error("No such contest", out);
			return;
		}
		
		flag = false;
		HttpSession httpsession = request.getSession();
		ContestData contestdata = (ContestData) httpsession.getAttribute((new StringBuilder()).append(l).toString());
		hashmap = null;
		flag1 = false;
		flag2 = false;
		if (contestdata != null)
		{
			flag = true;
			hashmap = contestdata.hm;
			l1 = contestdata.top;
			flag1 = contestdata.top_enable;
		}
		if (request.getParameter("displayall") != null)
			flag = false;
		connection = null;
		preparedstatement = null;
		resultset = null;
		l2 = System.currentTimeMillis();
		FormattedOut.printContestHead(out, l, "Contest Standing", request);
		int i1;
		try
		{
			connection = DBConfig.getConn();
			preparedstatement = connection.prepareStatement("select title,end_time,private,freeze from contest where contest_id=? and start_time<? and UPPER(defunct)='N'");
			preparedstatement.setLong(1, l);
			preparedstatement.setTimestamp(2, new Timestamp(l2));
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error("No such contest or contest not start", out);
				preparedstatement.close();
				connection.close();
				return;
			}
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
		try
		{
			long l3 = resultset.getTimestamp("end_time").getTime();
			int freeze = resultset.getInt("freeze");
			if ((freeze == 1) && (l3 < l2 + 3600000) && (l3 > l2))
			{
				Tool.freeze_board(connection, l);
			}
			i1 = resultset.getInt("private");
			flag2 = i1 > 0;
			if (flag2 && !Tool.permission(connection, request, l))
			{
				ErrorProcess.Error("It's a private contest.You have no permission.", out);
				return;
			}
			/*if (!UserModel.isAdminLoginned(request))
			{
				String File = this.getServletContext().getRealPath("/") + "/Contest Standing.htm";
				File file = new File(File);
				boolean flagFile = file.exists();
				if (flagFile)
				{
					System.out.println("Standing");
					Tool.GoToURL("Contest%20Standing.htm", response);
					return;
				}
				File = this.getServletContext().getRealPath("/") + "/Contest Standing.mht";
				file = new File(File);
				flagFile = file.exists();
				if (flagFile)
				{
					System.out.println("Standing");
					Tool.GoToURL("Contest%20Standing.mht", response);
					return;
				}
			}*/
			
			String s1 = resultset.getString("title");
			out.print((new StringBuilder()).append("<p align=center><font size=5 color=blue>Contest Standing").append(freeze==2?"(<font color=red>Freezed</font>)":"").append("--").append(s1).toString());
			if (l3 > l2)
				out.print((new StringBuilder()).append("<br>Time to go:").append(Tool.formatTime((l3 - l2) / 1000L)).toString());
			preparedstatement.close();
			out.print((new StringBuilder()).append("</font></p><form action=setconcernedteams method=POST><input type=hidden name=cid value=").append(l).append("><font size=5 color=red>Set teams you concern:just check them and click the button.</font>").toString());
			out.print("<br><input type=submit value=\"Set It\">");
			out.print((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=conteststanding?contest_id=").append(l).append("&displayall>Show all teams</a>").toString());
			out.print((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=conteststanding?contest_id=").append(l).append(">Show concerned teams</a>").toString());
			out.print("<br><input type=checkbox name=top_check value=true ");
			if (flag1)
				out.print("checked");
			out.print((new StringBuilder()).append("><font color=red>Always show top <input type=text name=top value=").append(l1).append(" size=3> teams.</font>").toString());
			out.print("<TABLE cellSpacing=0 cellPadding=0 width=99% align=center border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.print("<tr align=center><th width=5%>&nbsp;</th><th width=5%>Rank</th><th width=10%>Nick Name</th><th width=5%>Accepts</th><th width=10%>Penalty</th>");
			preparedstatement = connection.prepareStatement("select problem_id,num,title from contest_problem where contest_id=? order by num");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			int i = 0;
			for (; resultset.next(); out.print((new StringBuilder()).append("<th><a href=showproblem?problem_id=").append(resultset.getInt("num")).append("&contest_id=" + str + " title=\"" + resultset.getString("title") + "\">").append((char) (resultset.getInt("num") + 65)).append("</a></th>").toString()))
				i++;

			preparedstatement.close();
			out.print("</tr>\n");
			String sql = "SELECT * FROM attend WHERE contest_id=? ORDER BY accepts DESC,penalty ASC";
			if (freeze>0 && !UserModel.isAdminLoginned(request) && (l3 <= l2 + 3600000) && (l3 > l2))
				sql = "SELECT * FROM freeze_board WHERE contest_id=? ORDER BY accepts DESC,penalty ASC";
			preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			int j = 0;
			do
			{
				if (!resultset.next())
					break;
				j++;
				//boolean flag3 = false;
				String s2 = resultset.getString("user_id");
				if (!flag || hashmap.containsKey(s2) || flag1 && (long) j <= l1)
				{
					out.print((new StringBuilder()).append("<tr align=center><td><input type=checkbox name=uid value=").append(s2).toString());
					if (hashmap != null && hashmap.containsKey(s2))
						out.print(" checked");
					out.print((new StringBuilder()).append("></td><td>").append(j).append("</td>").toString());
					String s3 = resultset.getString("nick");
					boolean flag4 = true;
					PreparedStatement preparedstatement9;
					preparedstatement9 = connection.prepareStatement("select rightstr from privilege where user_id=?" + " and upper(defunct)='N' and (rightstr='title' OR rightstr='Administrator' OR rightstr='member')");
					preparedstatement9.setString(1, s2);
					ResultSet resultset9 = preparedstatement9.executeQuery();
					if (resultset9.next())
						flag4 = false;
					if (flag4 == true && s3.length() > 50)
						s3 = (new StringBuilder()).append(s3.substring(0, 45)).append("...").toString();
					/*
					 * PreparedStatement preparedstatement1 =
					 * connection.prepareStatement(
					 * "select rightstr from privilege where user_id=? and upper(defunct)='N' and rightstr='Administrato'"
					 * ); preparedstatement1.setString(1, s2); ResultSet
					 * resultset1 = preparedstatement1.executeQuery(); if
					 * (resultset1.next()) { // String s5 =
					 * resultset1.getString("rightstr"); // if
					 * (s5.equalsIgnoreCase("administrator")) flag3 = true; }
					 */
					//flag3 = UserModel.isAdminLoginned(request);
					out.print((new StringBuilder()).append("<td><a href=userstatus?user_id=").append(s2).append(" title=").append(resultset.getString("user_id")).append((new StringBuilder()).append(">").append(Tool.isAdmin(connection, s2) ? "<font color=red>*</font>" : "").toString()).append(Tool.titleEncode(connection, s2, s3)).append("</a></td><td><a href=conteststatus?user_id=").append(s2).append("&contest_id=").append(resultset.getLong("contest_id")).append(">").append(resultset.getString("accepts")).append("</a></td>").toString());
					long l6 = resultset.getLong("penalty");
					out.print((new StringBuilder()).append("<td>").append(Tool.formatTime(l6)).append("</td>").toString());
					for (int k = 0; k < i; k++)
					{
						char c = (char) (65 + k);
						long l4 = resultset.getLong((new StringBuilder()).append(c).append("_time").toString());
						long l5 = resultset.getLong((new StringBuilder()).append(c).append("_WrongSubmits").toString());
						String s4 = "";
						if (l4 != 0L && l5 != 0L)
							s4 = "<br>";
						if (l4 == 0L && l5 == 0L)
							out.print("<td>&nbsp;</td>");
						else
							out.print((new StringBuilder()).append("<td>").append(l4 == 0L ? "<font color=red><b>" : Tool.formatTime(l4)).append(s4).append(l5 == 0L ? "" : (new StringBuilder()).append("(-").append(l5).append(")").toString()).append(l4 == 0l ? "</b></font>" : "" + "</td>").toString());
					}

					out.print("</tr>\n");
				}
			} while (true);
			preparedstatement.close();
			connection.close();
		} catch (Exception exception2)
		{
			ErrorProcess.ExceptionHandle(exception2, out);
			return;
		}
		out.print((new StringBuilder()).append("</table><input type=submit value=\"Set It\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=conteststanding?contest_id=").append(l).append("&displayall>Show all teams</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=conteststanding?contest_id=").append(l).append(">Show concerned teams</a></form>").toString());
		FormattedOut.printBottom(request, out);
		out.close();
		return;
	}

	public void destroy()
	{
	}
}
