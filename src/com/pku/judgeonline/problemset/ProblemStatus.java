package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ProblemStatus extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProblemStatus()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		//long start = System.currentTimeMillis();
		//System.out.println("start:"+start);
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		long l = 0L, pid = 0L;
		long l1 = 0L;
		long l2 = 0L;
		long l3 = 20L;
		long l4 = -1L;
		long id = 0l;
		String c_id = request.getParameter("contest_id");
		String s = "time";
		String s1 = null;
		String s2 = "";
		String ss = "time,memory,code_length";
		String s_p = request.getParameter("problem_id");
		try
		{
			char c = s_p.charAt(0);
			if (c >= 'A' && c <= 'Z')
			{
				pid = l = c - 'A';
			} else
				pid = l = Integer.parseInt(s_p);
			l4 = Integer.parseInt(request.getParameter("language"));
		} catch (Exception exception)
		{
		}
		try
		{
			id = Integer.parseInt(c_id);
		} catch (Exception exception1)
		{
		}
		try
		{
			l2 = Integer.parseInt(request.getParameter("start"));
		} catch (Exception exception1)
		{
		}
		if (l4 != -1L)
			s2 = " and language=? ";
		s = request.getParameter("orderby");
		if (s == null)
			s = "time";
		if (s.equalsIgnoreCase("memory"))
		{
			s1 = " memory,time,code_length,n,solution_id,user_id";
			ss = "memory,time,code_length";
		} else if (s.equalsIgnoreCase("clen"))
		{
			s1 = " code_length,time,memory,n,solution_id,user_id";
			ss = "code_length,memory,time";
		} else if (s.equalsIgnoreCase("num"))
		{
			s1 = " n desc,code_length,time,memory,solution_id,user_id";
		} else
		{
			s1 = " time,memory,code_length,n,solution_id,user_id";
			s = "time";
		}
		s1 = (new StringBuilder()).append(s1).append(",user_id ").toString();
		Connection connection = null;
		PreparedStatement preparedstatement = null;
		try
		{
			connection = DBConfig.getConn();
			//System.out.println("conn :"+(System.currentTimeMillis()-start));
			ResultSet resultset;
			if (id != 0 && l <= 26)
			{
				preparedstatement = connection.prepareStatement("select problem_id from contest_problem where contest_id=? and num=?");
				preparedstatement.setLong(1, id);
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
			preparedstatement = connection.prepareStatement("select contest_id from problem where problem_id=?");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			if (resultset.next())
				l1 = resultset.getLong("contest_id");
			preparedstatement.close();
			if (l1 != 0)
			{
				PreparedStatement preparedstatement1 = connection.prepareStatement("select * from contest where contest_id=? and UPPER(defunct)='N'");
				preparedstatement1.setLong(1, l1);
				ResultSet resultset1 = preparedstatement1.executeQuery();
				if (resultset1.next())
				{
					int ii = resultset1.getInt("private");
					boolean flagc = (boolean) (ii > 0);
					if (flagc)
					{
						if (!Tool.permission(connection, request, l1))
						{
							ErrorProcess.Error("It's a private contest.You have no permission.", out);
							return;
						}
					}
				}
			}
			long l5 = 0L;
			long l6 = 0L;
			if (id == 0)
			{
				preparedstatement = connection.prepareStatement("select submit_user,solved,submit from problem where problem_id=?");
				preparedstatement.setLong(1, l);
			} else
			{
				preparedstatement = connection.prepareStatement("select count(if(result=0,1,null)) as solved,count(*) as submit_user from solution where contest_id=" + id + " and problem_id=" + l);
			}
			resultset = preparedstatement.executeQuery();
			if (resultset.next())
			{
				l5 = resultset.getLong("submit_user");
				l6 = resultset.getLong("solved");
			}
			preparedstatement.close();
			//System.out.println("before head :"+(System.currentTimeMillis()-start));
			if (id == 0L)
				FormattedOut.printHead(out, request, connection, (new StringBuilder()).append(l).append("'s Status List").toString());
			else
				FormattedOut.printContestHead(out, id, (new StringBuilder()).append(pid != l ? (id + ":" + (char) (pid + 65)) : pid).append("'s Status List").toString(), request);
			//System.out.println("after head :"+(System.currentTimeMillis()-start));
			out.println("<TABLE style='BORDER-COLLAPSE: collapse' borderColor=#ffffff width=100% border=1>");
			out.println("<TBODY>");
			out.println("");
			out.println("<td align=left>");
			out.println("<STYLE> v\\:* { Behavior: url(#default#VML) }o\\:* { behavior: url(#default#VML) }</STYLE>");
			out.println("");
			out.println("<table id=problem_status class=status>");
			out.println("<tr>");
			out.println("<td valign=top>");
			out.println("<div style='position:relative; height:650px; width:260px'>");
			out.println("<script language='javascript'>");
			out.println("var sa = new Array(); ");
			out.println("sa[0] = new Array();");
			out.println("sa[1] = new Array();");
			out.println("sa[2] = new Array();");
			out.println("var len = 0;");
			preparedstatement = connection.prepareStatement("select result,count(*) as sum from solution where problem_id=? " + (id != 0 ? "and contest_id=? " : "") + "group by result");
			preparedstatement.setLong(1, l);
			if (id != 0)
				preparedstatement.setLong(2, id);
			int i = 0;
			for (resultset = preparedstatement.executeQuery(); resultset.next();)
			{
				int j = resultset.getInt("result");
				out.print((new StringBuilder()).append("sa[1][").append(i).append("]='").append(ResultType.getResultDescript(j)).append("';").toString());
				out.print((new StringBuilder()).append("sa[0][").append(i).append("]=").append(resultset.getLong("sum")).append(";").toString());
				out.print((new StringBuilder()).append("sa[2][").append(i).append("]='").append(id != 0 ? "conteststatus" : "status").append("?problem_id=").append(pid).append((id != 0 ? "&contest_id=" + id : "") + "&result=").append(j).append("';").toString());
				i++;
			}

			out.println("if (sa[0].length > sa[1].length)");
			out.println("{ ");
			out.println("len = sa[1].length;");
			out.println(" }");
			out.println("else ");
			out.println("{ ");
			out.println("len = sa[0].length;");
			out.println(" }\t");
			out.print((new StringBuilder()).append("table(len,0,0,600,600,'Statistics','',200,250,").append(l5).append(",").append(l6).append(",'").append(id != 0 ? "conteststatus" : "status").append("?problem_id=").append(pid).append((id != 0 ? "&contest_id=" + id : "") + "'); ").toString());
			out.println("</script>");
			out.print("</div>\n</td>\n<td valign=top>");
			out.println((new StringBuilder()).append("<p align=center><font size=5 color=#333399>Best solutions of Problem <a href=showproblem?problem_id=").append(pid).append((id != 0 ? "&contest_id=" + id : "") + ">").append(pid != l ? (id + ":" + (char) (pid + 65)) : pid).append("</a></font></p>").toString());
			out.println((new StringBuilder()).append("<div align=right><a href=problemstatus?problem_id=").append(pid).append((id != 0 ? "&contest_id=" + id : "") + ">").append(l4 != -1L ? "" : "<font color=red><b>").append("All").append(l4 != -1L ? "" : "</b></font>").append("</a>&nbsp<a href=problemstatus?problem_id=").append(pid).append("&language=0&orderby=").append(s).append((id != 0 ? "&contest_id=" + id : "") + ">").append(l4 != 0L ? "" : "<font color=red><b>").append("G++").append(l4 != 0L ? "" : "</b></font>").append("</a>&nbsp<a href=problemstatus?problem_id=").append(pid).append("&language=1&orderby=").append(s).append((id != 0 ? "&contest_id=" + id : "") + ">").append(l4 != 1L ? "" : "<font color=red><b>").append("GCC").append(l4 != 1L ? "" : "</b></font>").append(
					"</a>&nbsp<a href=problemstatus?problem_id=").append(pid).append("&language=2&orderby=").append(s).append((id != 0 ? "&contest_id=" + id : "") + ">").append(l4 != 2L ? "" : "<font color=red><b>").append("Pascal").append(l4 != 2L ? "" : "</b></font>").append("</a>&nbsp<a href=problemstatus?problem_id=").append(pid).append("&language=3&orderby=").append(s).append((id != 0 ? "&contest_id=" + id : "") + ">").append(l4 != 3L ? "" : "<font color=red><b>").append("Java").append(l4 != 3L ? "" : "</b></font>").append("</a>&nbsp<a href=problemstatus?problem_id=").append(pid).append("&language=4&orderby=").append(s).append((id != 0 ? "&contest_id=" + id : "") + ">").append(l4 != 4L ? "" : "<font color=red><b>").append("Masm32").append(l4 != 4L ? "" : "</b></font>").append(
					"</a>&nbsp<a href=problemstatus?problem_id=").append(pid).append("&language=5&orderby=").append(s).append((id != 0 ? "&contest_id=" + id : "") + ">").append(l4 != 5L ? "" : "<font color=red><b>").append("Python").append(l4 != 5L ? "" : "</b></font>").append("</a></div>").toString());
			out.println("<TABLE cellSpacing=0 cellPadding=0 width=100% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.println((new StringBuilder()).append("<tr align=center bgcolor=#6589D1><th width=5%>Rank</th><th align=center width=15%><a href=problemstatus?problem_id=").append(pid).append("&language=").append(l4).append("&orderby=num" + (id != 0 ? "&contest_id=" + id : "") + ">Run ID</a></th>").toString());
			out.println("<th width=15%>User</th>");
			out.println((new StringBuilder()).append("<th width=10%><a href=problemstatus?problem_id=").append(pid).append("&language=").append(l4).append("&orderby=memory" + (id != 0 ? "&contest_id=" + id : "") + "><font color=white>Memory</font></a></th>").toString());
			out.println((new StringBuilder()).append("<th width=10%><a href=problemstatus?problem_id=").append(pid).append("&language=").append(l4).append("&orderby=time" + (id != 0 ? "&contest_id=" + id : "") + "><font color=white>Time</font></a></th>").toString());
			out.println("<th width=10%>Language</td>");
			out.println((new StringBuilder()).append("<th width=10%><a href=problemstatus?problem_id=").append(pid).append("&language=").append(l4).append("&orderby=clen" + (id != 0 ? "&contest_id=" + id : "") + "><font color=white>Code Length</font></a></th>").toString());
			out.println("<th width=25%>Submit Time</th></tr>");
			preparedstatement = connection.prepareStatement((new StringBuilder()).append("select * from ( select solution_id,user_id,count(*) as n ,in_date,language,memory,time,num,code_length from (select * from solution " + (id != 0 ? "where contest_id=? " : "") + " order by " + ss + ")b where problem_id=? and result=0  ").append(s2).append("group  BY user_id )a ").append(" group by ").append(s1).append(" limit ?,?").toString());
			int k = 0;
			if (id != 0)
				preparedstatement.setLong(++k, id);
			preparedstatement.setLong(++k, l);
			if (l4 != -1L)
				preparedstatement.setLong(++k, l4);
			preparedstatement.setLong(++k, l2);
			preparedstatement.setLong(++k, l3);
			resultset = preparedstatement.executeQuery();
			boolean flag1 = UserModel.isAdminLoginned(request);
			boolean flag2 = UserModel.isSourceBrowser(request);
			boolean flag3 = flag1 || flag2;
			long l9 = l2;
			String s3;
			String user1 = null;
			if (UserModel.isLoginned(request))
				user1 = UserModel.getCurrentUser(request).getUser_id();
			for (; resultset.next(); out.print((new StringBuilder()).append("<td>").append(s3).append("</td></tr>").toString()))
			{
				l9++;
				String s4 = resultset.getString("solution_id");
				String s6 = resultset.getString("user_id");
				s3 = resultset.getString("in_date");
				String s7 = resultset.getString("memory");
				String s8 = resultset.getString("time");
				String s9 = LanguageType.getDesc(resultset.getInt("language"));
				String s10 = resultset.getString("code_length");
				int i1 = resultset.getInt("n");
				String s11 = "";
				if (id != 0L)
					s11 = (new StringBuilder()).append("<a href=conteststatus?problem_id=").append(pid).append("&user_id=").append(s6).append("&contest_id=").append(id).append("&result=0&language=").append(l4 == -1L ? "" : ((Object) (Long.valueOf(l4)))).append(">").toString();
				else
					s11 = (new StringBuilder()).append("<a href=status?problem_id=").append(pid).append("&user_id=").append(s6).append("&result=0&language=").append(l4 == -1L ? "" : ((Object) (Long.valueOf(l4)))).append(">").toString();
				out.print((new StringBuilder()).append("<tr align=center><td>").append(l9).append((new StringBuilder()).append("</td><td>").append(i1 > 1 ? s11 : "").toString()).append(s4).append(i1 > 1 ? (new StringBuilder()).append("(").append(i1).append(")").toString() : (new StringBuilder()).append("").append(i1 > 1 ? "</a>" : "").append("</td>").toString()).toString());
				out.print((new StringBuilder()).append("<td><a href=userstatus?user_id=").append(s6).append(">").append(s6).append("</a></td>").toString());
				if (flag3 || l1 == 0L)
					out.print((new StringBuilder()).append("<td>").append(s7).append("K</td><td>").append(s8).append("MS</td>").toString());
				else
					out.print("<td>&nbsp;</td><td>&nbsp;</td>");
				PreparedStatement preparedstatement9;
				ResultSet resultset9;
				
				String user2 = s6;
				boolean flags1 = false;
				boolean flags2 = false;
				if (user1 != null && !UserModel.isUser(request, user2))
				{
					preparedstatement9 = connection.prepareStatement("select share from users where user_id=?");
					// System.out.println(user1+" "+user2);
					preparedstatement9.setString(1, user1);
					resultset9 = preparedstatement9.executeQuery();
					if (resultset9.next() && resultset9.getInt("share") == 1)
					{
						preparedstatement9 = connection.prepareStatement("select min(result) as result from solution where user_id=? and problem_id=?");
						preparedstatement9.setString(1, user1);
						preparedstatement9.setLong(2, l);
						resultset9 = preparedstatement9.executeQuery();
						if (resultset9.next() && resultset9.getInt("result") == 0)
						{
							if (resultset9.wasNull() == false)
								flags1 = true;
							// System.out.println(user1+" "+resultset9.getInt("result")+" "+resultset.getString("problem_id")+" "+resultset9.wasNull());
						}
						preparedstatement9 = connection.prepareStatement("select share from users where user_id=?");
						preparedstatement9.setString(1, user2);
						resultset9 = preparedstatement9.executeQuery();
						if (resultset9.next() && resultset9.getInt("share") == 1)
							flags2 = true;
					}
				}
				if ((flags1 && flags2) || flag1 || flag2 || UserModel.isUser(request, s6))
					out.println((new StringBuilder()).append("<td><a href=showsource?solution_id=").append(s4).append(" target=_blank>").append(s9).append("</a></td>").toString());
				else
					out.print((new StringBuilder()).append("<td>").append(s9).append("</td>").toString());
				if (flag3 || l1 == 0L)
					out.println((new StringBuilder()).append("<td>").append(s10).append("b</td>").toString());
				else
					out.println("<td>&nbsp;</td>");
			}

			preparedstatement.close();
			preparedstatement = null;
			connection.close();
			connection = null;
			out.println("</table>\n</td>\n</table>\n");
			out.print("<p align=center>");
			String s5 = (new StringBuilder()).append("[<a href=problemstatus?problem_id=").append(pid).append((id != 0 ? "&contest_id=" + id : "") + "&language=").append(l4).toString();
			out.print((new StringBuilder()).append(s5).append("&orderby=").append(s).append(">Top</a>]").toString());
			out.print((new StringBuilder()).append(s5).append("&start=").append(l2 > l3 ? l2 - l3 : 0L).append("&orderby=").append(s).append(">Previous Page</a>]").toString());
			out.print((new StringBuilder()).append(s5).append("&start=").append(l2 + l3).append("&orderby=").append(s).append(">Next Page</a>]</p>").toString());
			out.println("</td></tr></table>");
			FormattedOut.printBottom(request, out);
			//System.out.println("end  :"+(System.currentTimeMillis()-start));
		} catch (Exception exception2)
		{
			exception2.printStackTrace(System.err);
			try
			{
				if (preparedstatement != null)
					preparedstatement.close();
				preparedstatement = null;
				if (connection != null)
					connection.close();
				connection = null;
			} catch (Exception exception3)
			{
			}
		}

		out.close();
	}

	public void destroy()
	{
	}
}
