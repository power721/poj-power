package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ShowProblemServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowProblemServlet()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		int problem_id, pid;
		int cid = 0;
		int page = 1;
		int j = -1;
		String s_cid = request.getParameter("contest_id");
		String s_pid = request.getParameter("problem_id");
		boolean b_admin;
		PrintWriter out = response.getWriter();
		
		try
		{
			char c = s_pid.charAt(0);
			if (c >= 'A' && c <= 'Z')
			{
				pid = problem_id = c - 'A';
			} else
				pid = problem_id = Integer.parseInt(s_pid);
			if (s_cid != null && !s_cid.equals(""))
				cid = Integer.parseInt(s_cid);
		} catch (Exception exception)
		{
			ErrorProcess.Error("Problem ID is incorrect!", out);
			return;
		}

		if (!Guard.Guarder(request, response, out))
			return;

		if (cid == 0)
		{
			page = (pid-1000) / 100 + 1;
			Tool.addCookie(response, "oj_problem_page", String.valueOf(page), 604800);
		}
		Connection connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			if (cid != 0 && pid <= 26)
			{
				preparedstatement = connection.prepareStatement("select problem_id from contest_problem where contest_id=? and num=?");
				preparedstatement.setInt(1, cid);
				preparedstatement.setInt(2, pid);
				resultset = preparedstatement.executeQuery();
				if (!resultset.next())
				{
					ErrorProcess.Error((new StringBuilder()).append("Can not find contest problem (ID:").append(pid).append(")<br><br>").toString(), out);
					resultset.close();
					preparedstatement.close();
					connection.close();
					return;
				}
				j = pid;
				problem_id = resultset.getInt("problem_id");
				resultset.close();
				preparedstatement.close();
			}

			b_admin = UserModel.isAdminLoginned(request);
			String sql = "SELECT * FROM problem WHERE problem_id = ? AND UPPER(defunct) = 'N'";
			if (b_admin)
				sql = "SELECT * FROM problem WHERE problem_id = ?";
			preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setInt(1, problem_id);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error((new StringBuilder()).append("Can not find problem (ID:").append(problem_id).append(")<br><br><div algin=center><a href=showproblem?problem_id=").append(problem_id - 1).append(" title=\"按←键上一页\">&lt;&lt;Prev</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=showproblem?problem_id=").append(problem_id + 1).append(" title=\"按→键下一页\">Next&gt;&gt;</a></div><br><form method=get action=gotoproblem>Prob.ID:</font><input type=text name=pid size=6><input type=Submit value=Go name=pb1></form>").toString(), out);
				resultset.close();
				preparedstatement.close();
				connection.close();
				return;
			}

			long l;
			long l1;
			String contest_id;
			String s_spjFile;
			boolean in_contest = false;
			boolean b_start = true;
			boolean b_end = true;
			boolean b_login = UserModel.isLoginned(request);
			PreparedStatement preparedstatement2;
			ResultSet resultset2;
			if (cid == 0)
			{
				if (b_admin)
					preparedstatement2 = connection.prepareStatement("SELECT * FROM problem WHERE problem_id = ?");
				else
					preparedstatement2 = connection.prepareStatement("SELECT * FROM problem WHERE problem_id = ? AND UPPER(defunct) = 'N'");
			} else
			{
				preparedstatement2 = connection.prepareStatement("select count(if(result=0,1,null)) as accepted,count(*) as submit from solution where problem_id=? and contest_id=?");
			}
			preparedstatement2.setInt(1, problem_id);
			if (cid > 0)
				preparedstatement2.setInt(2, cid);
			resultset2 = preparedstatement2.executeQuery();
			if (!resultset2.next())
			{
				ErrorProcess.Error((new StringBuilder()).append("Can not find problem (ID:").append(problem_id).append(")<br><br><div algin=center><a href=showproblem?problem_id=").append(problem_id - 1).append(" title=\"按←键上一页\">&lt;&lt;Prev</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=showproblem?problem_id=").append(problem_id + 1).append(" title=\"按→键下一页\">Next&gt;&gt;</a></div><br><form method=get action=gotoproblem>Prob.ID:</font><input type=text name=pid size=6><input type=Submit value=Go name=pb1></form>").toString(), out);
				resultset2.close();
				preparedstatement2.close();
				resultset.close();
				preparedstatement.close();
				connection.close();
				return;
			}
			l = resultset2.getLong("accepted");
			l1 = resultset2.getLong("submit");
			s_spjFile = (new StringBuilder()).append(resultset.getString("input_path")).append("\\").append(ServerConfig.VALIDATE_FILE_NAME).toString();

			contest_id = resultset.getString("contest_id");
			if (contest_id != null)
			{
				preparedstatement2 = connection.prepareStatement("select * from contest where contest_id=? and UPPER(defunct)='N'");
				preparedstatement2.setInt(1, Integer.parseInt(contest_id));
				resultset2 = preparedstatement2.executeQuery();
				if (resultset2.next())
				{
					boolean b_private = resultset2.getInt("private") > 0;
					if (b_private && !Tool.permission(connection, request, Integer.parseInt(contest_id)))
					{
						ErrorProcess.Error("It's a private contest.You have no permission.", out);
						resultset2.close();
						preparedstatement2.close();
						resultset.close();
						preparedstatement.close();
						connection.close();
						return;
					}
					in_contest = true;
					Timestamp timestamp = resultset2.getTimestamp("start_time");
					Timestamp timestamp1 = resultset2.getTimestamp("end_time");
					b_start = timestamp.getTime() < System.currentTimeMillis();
					b_end = timestamp1.getTime() < System.currentTimeMillis();
					if (cid == 0)
						cid = Integer.parseInt(contest_id);
				}
			}
			resultset2.close();
			preparedstatement2.close();

			if (in_contest)
			{
				if ((s_cid == null || !b_admin) && !b_start)
				{
					ErrorProcess.Error("You cannot access this problem.", out);
					resultset.close();
					preparedstatement.close();
					connection.close();
					return;
				}
				if (b_end)
				{
					PreparedStatement preparedstatement3 = connection.prepareStatement("update problem set contest_id=null where problem_id=?");
					preparedstatement3.setLong(1, problem_id);
					preparedstatement3.executeUpdate();
					preparedstatement3.close();
					contest_id = null;
				}
				preparedstatement2 = connection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
				preparedstatement2.setInt(1, cid);
				preparedstatement2.setLong(2, problem_id);
				resultset2 = preparedstatement2.executeQuery();
				if (resultset2.next())
					j = resultset2.getInt("num");
				resultset2.close();
				preparedstatement2.close();
			}
			try
			{
				boolean b_defunct = resultset.getString("defunct").equals("Y");
				String s_title = resultset.getString("title");
				long l3 = resultset.getLong("time_limit");
				long l4 = resultset.getLong("case_time_limit");
				long l5 = resultset.getInt("memory_limit");
				String s_desc = resultset.getString("description");
				String s_input = resultset.getString("input");
				String s_output = resultset.getString("output");
				String s_sinput = resultset.getString("sample_input");
				String s_soutput = resultset.getString("sample_output");
				String s_hint = resultset.getString("hint");
				String s_source = resultset.getString("source");
				String s_view = resultset.getString("view");
				String s_recommend = resultset.getString("Recommend");
				String s_status = "&nbsp;";
				preparedstatement.close();
				preparedstatement = connection.prepareStatement("UPDATE problem SET view = view+1 where problem_id =?");
				preparedstatement.setInt(1, problem_id);
				preparedstatement.executeUpdate();
				String s13 = (new StringBuilder()).append("&nbsp;<a href=\"admin.probmanagerpage?problem_id=").append(problem_id).append("\"><font color=red><u>Edit</u></font></a>&nbsp;&nbsp;<a href=\"javascript:rejudge('problem_id',").append(problem_id).append(")\"><font color=red><u>Rejudge</u></font></a>").toString();
				if (b_login)
				{
					String s14 = "select min(result) as status from solution where user_id = ? and problem_id = ? ";
					if (j != -1)
						s14 = (new StringBuilder()).append(s14).append(" and contest_id = ?").toString();
					PreparedStatement preparedstatement1 = connection.prepareStatement(s14);
					preparedstatement1.setString(1, UserModel.getCurrentUser(request).getUser_id());
					preparedstatement1.setLong(2, problem_id);
					if (j != -1)
						preparedstatement1.setString(3, contest_id);
					ResultSet resultset1 = preparedstatement1.executeQuery();
					if (resultset1.next())
					{
						int i1 = resultset1.getInt("status");
						if (resultset1.wasNull())
							s_status = "&nbsp;";
						else if (i1 == 0)
							s_status = "<img border=0 src=images/accepted.gif>";
						else
							s_status = "<img border=0 src=images/wrong.gif>";
					}
					resultset1.close();
					preparedstatement1.close();
				}
				if (j == -1)
				{
					FormattedOut.printHead(out, request, connection, (new StringBuilder()).append(problem_id).append(" -- ").append(s_title).toString());
					long prev_pid = Tool.getPreviousProblemID(connection, request, problem_id);
					long next_pid = Tool.getNextProblemID(connection, request, problem_id);
					String prev_url = "showproblem?problem_id=" + prev_pid;
					String next_url = "showproblem?problem_id=" + next_pid;
					out.println("<script language=\"javascript\" type=\"text/javascript\">");
					out.println("$(document).ready(function(){");
					out.println("$(\"a.ProblemHead\").click(function(){");
					out.println("$(this).parent().next(\"div\").children(\"pre\").toggle(\"slow\");");
					out.println("});");
					out.println("document.onkeydown=nextpage");
					out.println("document.onkeydown=nextpage");
					out.println("var prevpage=\"" + prev_url + "\"");
					out.println("var nextpage=\"" + next_url + "\"");
					out.println("function nextpage(event)");
					out.println("{");
					out.println("    event=event?event:(window.event?window.event:null);");
					out.println("    if(event.keyCode==37)location=prevpage;");
					out.println("    if(event.keyCode==39)location=nextpage;");
					out.println("}");

					out.println("});");
					out.println("</script>");
					out.println("<table class=problem border=0 align=center width=99% background=images/table_back.jpg><tr><td>");

					out.println("<table border=\"0\" cellpadding=0 cellspacing=0 width=99%><tr><td  class=no width=50% align=left><a href=\"" + prev_url + "\" title=\"按←键上一页\">&lt;&lt;Previous</a></td><td  class=no width=50% align=right><a href=\"" + next_url + "\" title=\"按→键下一页\">Next&gt;&gt;</a></td></tr></table>");
					out.println((new StringBuilder()).append((new StringBuilder()).append("<p align=\"center\">").append(s_status).append("<font  size=5>").toString()).append(problem_id).append("</font>").append(":").append((new StringBuilder()).append("<font color=blue size=5>").append(!b_admin || !b_defunct ? "" : "<del>").toString()).append((new StringBuilder()).append(s_title).append(!b_admin || !b_defunct ? "" : "</del>").toString()).append((new StringBuilder()).append("</font>").append(b_admin ? s13 : "").append("</p>").toString()).toString());
				} else
				{
					PreparedStatement preparedstatement5 = connection.prepareStatement("select title from contest_problem where contest_id=? and num=?");
					preparedstatement5.setLong(1, cid);
					preparedstatement5.setLong(2, j);
					ResultSet resultset4 = preparedstatement5.executeQuery();
					if (resultset4.next())
						s_title = resultset4.getString("title");
					resultset4.close();
					preparedstatement5.close();
					FormattedOut.printContestHead(out, cid, (new StringBuilder()).append((char) (j + 65)).append(":").append(cid).append(" -- ").append(s_title).toString(), request);
					out.println("<script language=\"javascript\" type=\"text/javascript\">");
					out.println("$(document).ready(function(){");
					out.println("$(\"a.ProblemHead\").click(function(){");
					out.println("$(this).parent().next(\"div\").children(\"pre\").toggle(\"slow\");");
					out.println("});");
					out.println("});");
					out.println("</script>");
					out.println("<table class=problem border=0 align=center width=99% background=images/table_back.jpg><tr><td>");
					preparedstatement5 = connection.prepareStatement("select problem_id,num from contest_problem where contest_id=? order by num");
					preparedstatement5.setLong(1, cid);
					resultset4 = preparedstatement5.executeQuery();
					out.println("<table border=0 align=center width=99%><tr>");
					for (; resultset4.next(); out.println((new StringBuilder()).append("<td><a href=showproblem?problem_id=").append(resultset4.getLong("num")).append("&contest_id=" + cid + "><b>").append((char) (resultset4.getInt("num") + 65)).append("</b></a></td>").toString()))
						;
					resultset4.close();
					preparedstatement5.close();
					out.println("</tr></table>");

					out.println((new StringBuilder()).append("<p align=center>").append(s_status).append("<font color=blue size=5>Problem ").append((char) (j + 65)).append(":").append(s_title).append("</font>").append(b_admin ? s13 : "").append("</p>").toString());
				}
				File f_spjFile = new File(s_spjFile);
				boolean b_spj = f_spjFile.exists();
				out.println((new StringBuilder()).append("<p align=\"center\"><font color=blue>Time Limit:</font>").append(l3).append("MS&nbsp; <font color=blue>Memory Limit:</font>").append(l5).append("K<br>").toString());
				out.println((new StringBuilder()).append("<font color=red>Total Submit:</font><a href=\"").append(cid != 0 ? "conteststatus" : "status").append("?problem_id=").append(pid).append((cid == 0 ? "" : "&contest_id=" + cid) + "\">").append(l1).append("</a><font color=red> Accepted:</font><a href=\"").append(cid != 0 ? "conteststatus" : "status").append("?problem_id=").append(pid).append("&result=0" + (cid == 0 ? "" : "&contest_id=" + cid) + "\">").append(l).append("</a>").toString());
				// if (b_admin)
				out.println((new StringBuilder()).append(" Page View:<font color=blue>").append(s_view).append("</font>").toString());
				if (b_spj)
					out.println("<font color=red><b>Special Judged</b></font>");
				if (l4 < l3)
					out.println((new StringBuilder()).append("<br><font color=read>Case Time Limit:").append(l4).append("MS</font>").toString());
				out.println("</p>");
				out.println("<font color=blue size=\"4\"><p align=\"center\">");
				out.println((new StringBuilder()).append("[<a href=\"submitpage?problem_id=").append(pid).append(cid == 0L ? "" : (new StringBuilder()).append("&contest_id=").append(cid).toString()).append("\">Submit</a>]&nbsp;&nbsp;").toString());
				out.println((new StringBuilder()).append(" [<a href=\"problemstatus?problem_id=").append(pid).append((cid == 0 ? "" : "&contest_id=" + cid) + "\">Status</a>]&nbsp;&nbsp; ").toString());
				if (b_end)
					out.println((new StringBuilder()).append("[<a href=\"bbs?problem_id=").append(problem_id).append("\">Discuss</a>]").toString());
				else
					out.println((new StringBuilder()).append("[<a href=\"bbs?problem_id=").append(pid + "&contest_id=" + cid).append("\" target=_blank>Clarify</a>]").toString());
				out.println("</p></font>");
				out.println("<div  style=\"border-top:1px solid #aaa; width:99%; color:#555\" align=\"right\" >Font Size:");
				out.println("<span style=\"font-size:16px;\" ><a href=\"javascript:void(0)\" onclick=\"set_size(17);\">Aa</a></span>");
				out.println("<span style=\"font-size:18px;\" ><a href=\"javascript:void(0)\" onclick=\"set_size(19);\">Aa</a></span>");
				out.println("<span style=\"font-size:20px;\" ><a href=\"javascript:void(0)\" onclick=\"set_size(21);\">Aa</a></span>");
				out.println("</div>");
				out.println("<div id=\"Problem\">");
				out.println("<div class=\"ProblemBody\">");
				out.println("<h2><a href=\"javascript:void(0)\" class=\"ProblemHead\">Description</a></h2>");
				out.println("<div><pre>");
				out.print(Tool.gethtmlFormattedString(s_desc));
				out.println("</pre></div>");
				out.println("<h2><a href=\"javascript:void(0)\" class=\"ProblemHead\">Input</a></h2>");
				out.println("<div><pre>");
				out.print(Tool.gethtmlFormattedString(s_input));
				out.println("</pre></div>");
				out.println("<h2><a href=\"javascript:void(0)\" class=\"ProblemHead\">Output</a></h2>");
				out.println("<div><pre>");
				out.print(Tool.gethtmlFormattedString(s_output));
				out.println("</pre></div>");
				out.println("<h2><a href=\"javascript:void(0)\" class=\"ProblemHead\">Sample Input</a></h2>");
				out.println("<div><pre>");
				out.print(Tool.gethtmlFormattedString(s_sinput));
				out.println("</pre></div>");
				out.println("<h2><a href=\"javascript:void(0)\" class=\"ProblemHead\">Sample Output</a></h2>");
				out.println("<div><pre>");
				out.print(Tool.gethtmlFormattedString(s_soutput));
				out.println("</pre></div>");
				if (s_hint != null && !s_hint.trim().equals(""))
				{
					out.println("<h2><a href=\"javascript:void(0)\" class=\"ProblemHead\">Hint</a></h2>");
					out.println("<div><pre>");
					out.print(Tool.gethtmlFormattedString(s_hint));
					out.println("</pre></div>");
				}
				if ((b_end || b_admin) && s_source != null && !s_source.trim().equals(""))
				{
					out.println("<h2><a href=\"javascript:void(0)\" class=\"ProblemHead\">Source</a></h2>");
					out.println("<div><pre>");
					out.println((new StringBuilder()).append("<a href=\"searchproblem?sstr=").append(Tool.gethtmlFormattedString(s_source)).append("&manner=2\">").append(Tool.gethtmlFormattedString(s_source)).append("</a>").toString());
					out.println("</font></pre></div>");
				}
				if (s_recommend != null && !s_recommend.trim().equals(""))
				{
					out.println("<h2><a href=\"javascript:void(0)\" class=\"ProblemHead\">Recommend</a></h2>");
					out.println("<div><pre>");
					out.print((new StringBuilder()).append("<a href=\"userstatus?user_id=").append(s_recommend).append("\">").append(s_recommend).append("</a>").toString());
					out.println("</pre></div>");
				}
				if (j == -1)
				{
					PreparedStatement preparedstatement6 = connection.prepareStatement("SELECT * FROM tag where problem_id=?");
					preparedstatement6.setInt(1, problem_id);
					ResultSet resultset5 = preparedstatement6.executeQuery();
					if (resultset5.first())
					{
						out.println("<a name=tag></a><h2>Tag</h2>");
						out.println("<div><font color=blue>");
						do
						{
							String s_tag = resultset5.getString("tag");
							String s_user = resultset5.getString("user_id");
							out.print((new StringBuilder()).append("<a href=\"searchproblem?sstr=").append(s_tag).append("&manner=3&title=").append(s_user).append("\">").append(s_tag).append("</a>  ").toString());
						} while (resultset5.next());
					}
					out.println("<br></div>");
					resultset5.close();
					preparedstatement6.close();
				}
				if (b_admin || UserModel.isSourceBrowser(request))
				{
					out.println("<form method=get action=addtag>");
					out.println((new StringBuilder()).append("<input type=hidden name=problem_id value=").append(problem_id).append(">").toString());
					out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Add a Tag:<input type=text name=tag size=20>");
					out.println("<input type=submit value=Submit ></form>");
				}
				out.println("</div></td></tr></table>");
				out.println("<font color=blue size=\"4\"><p align=\"center\">");
				out.println((new StringBuilder()).append("[<a href=\"submitpage?problem_id=").append(pid).append(cid == 0L ? "" : (new StringBuilder()).append("&contest_id=").append(cid).toString()).append("\">Submit</a>]&nbsp;&nbsp;").toString());
				out.println((new StringBuilder()).append(" [<a href=\"problemstatus?problem_id=").append(pid).append((cid == 0 ? "" : "&contest_id=" + cid) + "\">Status</a>]&nbsp;&nbsp; ").toString());
				if (b_end)
					out.println((new StringBuilder()).append("[<a href=\"bbs?problem_id=").append(problem_id).append("\">Discuss</a>]").toString());
				else
					out.println((new StringBuilder()).append("[<a href=\"bbs?problem_id=").append(pid).append("&contest_id=").append(cid).append("\" target=_blank>Clarify</a>]").toString());
				out.println("</p></font>");
				FormattedOut.printBottom(request, out);
			} catch (Exception exception1)
			{
				ErrorProcess.ExceptionHandle(exception1, out);
			}
			connection.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
		}
		return;
	}

	public void destroy()
	{
	}
}
