package com.pku.judgeonline.contest;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.common.LanguageType;
import com.pku.judgeonline.common.ResultType;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;

public class ContestStatus extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String _$6092 = "<TABLE cellSpacing=0 cellPadding=0 width=99% align=center border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF><tr bgcolor=#6589D1><td class=status align=center width=8%><b>Run ID</b></td><td class=status align=center width=10%><b>User</b></td><td class=status align=center width=6%><b>Problem</b></td><td class=status align=center width=20%><b>Result</b></td><td class=status align=center width=7%><b>Memory</b></td><td class=status align=center width=7%><b>Time</b></td><td class=status align=center width=7%><b>Language</b></td><td class=status align=center width=7%><b>Code Length</b></td><td class=status align=center width=17%><b>Submit Time</b></td></tr>\n";

	/**
	 * Constructor of the object.
	 */
	public ContestStatus()
	{
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy()
	{
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		int i;
		int j;
		int k;
		int p = 1;
		// int page = 0;
		int pageall = 1;
		int p_id = 99, pid = 0;
		String s;
		String s1;
		String s2;
		String s3;
		String s4;
		String sql;
		String sqlall;
		long l;
		boolean flag;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		Connection connection;
		ResultSet resultset;
		PreparedStatement preparedstatement;
		i = 0;
		j = 20;
		k = 0;
		s = null;
		s1 = null;
		s2 = null;
		s3 = null;
		s4 = null;
		l = 0L;
		flag = false;
		try
		{
			s = request.getParameter("problem_id");
			if (s == null)
				s = "";
			s = s.trim();
			if (s != "")
			{
				s = s.toUpperCase();
				char c = s.charAt(0);
				if (c >= 'A' && c <= 'Z')
				{
					pid = p_id = c - 'A';
				} else
					pid = p_id = Integer.parseInt(s);
			}
			s1 = request.getParameter("contest_id");
			if (s1 != null)
				l = Integer.parseInt(s1);
			s2 = request.getParameter("result");
			if (s2 == null)
				s2 = "";
			s2 = s2.trim();
			s3 = request.getParameter("user_id");
			if (s3 == null)
				s3 = "";
			s3 = s3.trim();

			s4 = request.getParameter("language");
			if (s4 == null)
				s4 = "";
			s4 = s4.trim();
			String s5 = request.getParameter("top");
			if (s5 != null)
			{
				i = Integer.parseInt(s5);
			} else
			{
				String s6 = request.getParameter("bottom");
				if (s6 != null)
					k = Integer.parseInt(s6);
			}
			String s7 = request.getParameter("size");
			if (s7 != null)
				j = Integer.parseInt(s7);
			String s8 = request.getParameter("p");
			if (s8 != null)
				p = Integer.parseInt(s8);
		} catch (NumberFormatException numberformatexception)
		{
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		long l2 = System.currentTimeMillis();
		try
		{
			if (l != 0L)
			{
				connection = DBConfig.getConn();
				if (p_id <= 26)
				{
					preparedstatement = connection.prepareStatement("select problem_id from contest_problem where contest_id=? and num=?");
					preparedstatement.setLong(1, l);
					preparedstatement.setLong(2, p_id);
					resultset = preparedstatement.executeQuery();
					if (!resultset.next())
					{
						ErrorProcess.Error((new StringBuilder()).append("Can not find contest problem (ID:").append(p_id).append(")<br><br>").toString(), out);
						preparedstatement.close();
						connection.close();
						return;
					}
					p_id = resultset.getInt("problem_id");
					s = new StringBuilder().append(p_id).toString();
				}
				preparedstatement = connection.prepareStatement("select title as ctitle ,start_time,end_time,description,private from contest where contest_id=? and start_time<? and UPPER(contest.defunct) = 'N'");
				preparedstatement.setLong(1, l);
				preparedstatement.setTimestamp(2, new Timestamp(l2));
				resultset = preparedstatement.executeQuery();
				if (!resultset.next())
				{
					FormattedOut.printContestHead(out, l, "Contest Status List", request);
					ErrorProcess.Error("No such contest or contest not start", out);
					preparedstatement.close();
					preparedstatement = null;
					connection.close();
					connection = null;
					return;
				}
				int i1 = resultset.getInt("private");
				flag = i1 > 0;
				if (flag && Tool.isContestRunning(connection, l))
				{
					if (!UserModel.isLoginned(request))
						s3 = "test";
					else if (!UserModel.isAdminLoginned(request))
						s3 = UserModel.getCurrentUser(request).getUser_id();
				}
				if (flag && !Tool.permission(connection, request, l))
				{
					FormattedOut.printContestHead(out, l, "Contest Status List", request);
					connection.close();
					connection = null;
					ErrorProcess.Error("It's a private contest.You have no permission.", out);
					return;
				}
				connection.close();
			}
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
		if (j > 500)
			j = 500;
		String s8 = "solution_id,user_id,problem_id,result,in_date,language,memory,time,contest_id ,num,code_length as len from solution ";
		boolean flag1 = false;
		String s9 = "";
		if (!s.trim().equals(""))
		{
			if (!flag1)
			{
				s8 = s8 + " where ";
				s9 = s9 + "?";
			} else
			{
				s8 = s8 + " and ";
				s9 = s9 + "&";
			}
			flag1 = true;
			s8 = s8 + "problem_id=? ";
			s9 = s9 + "problem_id=" + s;
		}
		if (s1 != null)
		{
			if (!flag1)
			{
				s8 = s8 + " where ";
				s9 = s9 + "?";
			} else
			{
				s8 = s8 + " and ";
				s9 = s9 + "&";
			}
			flag1 = true;
			s8 = s8 + "contest_id=? ";
			s9 = s9 + "contest_id=" + s1;
		}
		if (!s2.trim().equals(""))
		{
			if (!flag1)
			{
				s8 = s8 + " where ";
				s9 = s9 + "?";
			} else
			{
				s8 = s8 + " and ";
				s9 = s9 + "&";
			}
			flag1 = true;
			s8 = s8 + "result=? ";
			s9 = s9 + "result=" + s2;
		}
		if (!s3.trim().equals(""))
		{
			if (!flag1)
			{
				s8 = s8 + " where ";
				s9 = s9 + "?";
			} else
			{
				s8 = s8 + " and ";
				s9 = s9 + "&";
			}
			flag1 = true;
			s8 = s8 + "user_id=? ";
			s9 = s9 + "user_id=" + s3;
		}
		if (!s4.trim().equals(""))
		{
			if (!flag1)
			{
				s8 = s8 + " where ";
				s9 = s9 + "?";
			} else
			{
				s8 = s8 + " and ";
				s9 = s9 + "&";
			}
			flag1 = true;
			s8 = s8 + "language=? ";
			s9 = s9 + "language=" + s4;
		}
		boolean flag2 = flag1;
		if (j != 20)
		{
			if (!flag2)
				s9 = s9 + "?";
			else
				s9 = s9 + "&";
			flag2 = true;
			s9 = s9 + "size=" + j;
		}
		sqlall = "SELECT count(*)as page, " + s8;
		if (i != 0)
		{
			if (!flag1)
				s8 = s8 + " where ";
			else
				s8 = s8 + " and ";
			flag1 = true;
			s8 = s8 + "solution_id<? ";
		} else if (k != 0)
		{
			if (!flag1)
				s8 = s8 + " where ";
			else
				s8 = s8 + " and ";
			flag1 = true;
			s8 = s8 + "solution_id>? ";
		}

		sql = "SELECT count(*)as page, " + s8;
		s8 = "SELECT " + s8 + "order by solution_id " + (k != 0 ? "" : "desc") + " limit " + (p - 1) * j + "," + j;
		int j1 = 0;
		int k1 = 0;
		if (i > 0)
			k1 = i - 1;
		try
		{
			Connection connection1 = DBConfig.getConn();
			if (s1 == null)
				FormattedOut.printHead(out, request, connection1, "Contest Status");
			else
				FormattedOut.printContestHead(out, l, "Contest Status List", request);
			out.print("<script language=\"javascript\" type=\"text/javascript\">");
			out.print("$(document).ready(function(){");
			out.print("$(\":input[name=result]\").change(function(){$('#status').load(\"status\",{\"result\":$(this).val()});});");
			out.print("$(\"tr.status\").mouseover(function(){");
			out.print("$(this).css(\"background-color\",\"#6589D1\");");
			out.print("});");
			out.print("$(\"tr.status\").mouseout(function(){");
			out.print("$(this).css(\"background-color\",\"\");");
			out.print("});");
			out.print("});");
			out.print("</script>");
			int l1 = -1;
			if (!s2.trim().equals(""))
				l1 = Integer.parseInt(s2);
			int i2 = -1;
			if (!s4.trim().equals(""))
				i2 = Integer.parseInt(s4);
			out.println("<center><img src='./images/status.gif' alt='Status'></center><br>");
			out.print("<p align=center><font size=5 color=blue>Contest Status List</font></p>");
			out.print("<div id=status><form method=get action=conteststatus>" + (l != 0l ? ("<input type=hidden name=contest_id value=" + l + ">") : "") + "Problem ID:<input type=text name=problem_id size=8 value=\"" + (s != "" && p_id != pid ? "" + (char) (pid + 65) : s) + "\">User ID:<input type=text name=user_id size=15 value=\"" + s3 + "\">Result:<select size=1 name=result><option value='' " + (l1 != -1 ? "" : " selected") + ">All</option><option value=0" + (l1 != 0 ? "" : " selected") + ">Accepted</option><option value=1" + (l1 != 1 ? "" : " selected") + ">Presentation Error</option><option value=2" + (l1 != 2 ? "" : " selected") + ">Time Limit Exceeded</option><option value=3" + (l1 != 3 ? "" : " selected") + ">Memory Limit Exceeded</option><option value=4"
					+ (l1 != 4 ? "" : " selected") + ">Wrong Answer</option><option value=5" + (l1 != 5 ? "" : " selected") + ">Runtime Error</option><option value=6" + (l1 != 6 ? "" : " selected") + ">Output Limit Exceeded</option><option value=7" + (l1 != 7 ? "" : " selected") + ">Compile Error</option></select>Language:<select size=1 name=language><option value='' " + (i2 != -1 ? "" : " selected") + ">All</option><option value=0" + (i2 != 0 ? "" : " selected") + ">G++</option><option value=1" + (i2 != 1 ? "" : " selected") + ">GCC</option><option value=2" + (i2 != 2 ? "" : " selected") + ">Pascal</option><option value=3" + (i2 != 3 ? "" : " selected") + ">Java</option><option value=4" + (i2 != 4 ? "" : " selected")
					+ ">Masm32</option><option value=5" + (i2 != 5 ? "" : " selected") + ">Python</option></select><input type=submit width=8 value=Go></form>");
			preparedstatement = connection1.prepareStatement(sqlall);
			if (!s.equals(""))
				preparedstatement.setString(++j1, s);
			if (s1 != null)
				preparedstatement.setString(++j1, s1);
			if (!s2.equals(""))
				preparedstatement.setString(++j1, s2);
			if (!s3.equals(""))
				preparedstatement.setString(++j1, s3);
			if (!s4.equals(""))
				preparedstatement.setString(++j1, s4);
			resultset = preparedstatement.executeQuery();
			if (resultset.next())
			{
				pageall = (resultset.getInt("page") + j - 1) / j;
				if(pageall < 1)
					pageall = 1;
			}
			preparedstatement = connection1.prepareStatement(sql);
			j1 = 0;
			if (!s.equals(""))
				preparedstatement.setString(++j1, s);
			if (s1 != null)
				preparedstatement.setString(++j1, s1);
			if (!s2.equals(""))
				preparedstatement.setString(++j1, s2);
			if (!s3.equals(""))
				preparedstatement.setString(++j1, s3);
			if (!s4.equals(""))
				preparedstatement.setString(++j1, s4);
			if (i != 0)
				preparedstatement.setInt(++j1, i);
			else if (k != 0)
				preparedstatement.setInt(++j1, k);
			resultset = preparedstatement.executeQuery();
			// if (resultset.next())
			// page = resultset.getInt("page") / j + 1;
			PreparedStatement preparedstatement1 = connection1.prepareStatement(s8);
			j1 = 0;
			if (!s.equals(""))
				preparedstatement1.setString(++j1, s);
			if (s1 != null)
				preparedstatement1.setString(++j1, s1);
			if (!s2.equals(""))
				preparedstatement1.setString(++j1, s2);
			if (!s3.equals(""))
				preparedstatement1.setString(++j1, s3);
			if (!s4.equals(""))
				preparedstatement1.setString(++j1, s4);
			if (i != 0)
				preparedstatement1.setInt(++j1, i);
			else if (k != 0)
				preparedstatement1.setInt(++j1, k);
			ResultSet resultset1 = preparedstatement1.executeQuery();
			out.print(_$6092);
			boolean flag3 = UserModel.isAdminLoginned(request);
			boolean flag4 = UserModel.isSourceBrowser(request);
			boolean flag5 = k == 0;
			boolean flag6 = k != 0;
			if (flag5 && resultset1.first() || !flag5 && resultset1.last())
				do
				{
					i = resultset1.getInt("solution_id");
					if (i > k1)
						k1 = i;
					String s10 = resultset1.getString("user_id");
					String s11 = resultset1.getString("problem_id");
					String s12 = resultset1.getString("contest_id");
					int j2 = resultset1.getInt("num");
					int k2 = resultset1.getInt("result");
					String s13 = resultset1.getString("in_date");
					String s14 = LanguageType.getDesc(resultset1.getInt("language"));
					String s15;
					if (k2 == 0)
						s15 = "blue";
					else
						s15 = "red";
					out.print("<tr align=center class=status><td>" + i + "</td>");
					boolean flag7 = true;
					if (!flag3 && s12 != null)
					{
						PreparedStatement preparedstatement2 = connection1.prepareStatement("select end_time from contest where contest_id=?");
						preparedstatement2.setInt(1, Integer.parseInt(s12));
						ResultSet resultset2 = preparedstatement2.executeQuery();
						if (resultset2.next())
							flag7 = resultset2.getTimestamp("end_time").getTime() <= l2;
						preparedstatement2.close();
					}
					boolean flag8 = flag7 || UserModel.isUser(request, s10);
					String s16 = "";
					if (flag7 && (s1 == null || j2 == -1))
						s16 = s11;
					else
						s16 = (s1 == null ? s12 + ":" : "") + (char) (j2 + 65);
					out.print("<td><a href=userstatus?user_id=" + s10 + ">" + s10 + "</a></td><td><a href=showproblem?problem_id=" + (flag7 && (s1 == null || j2 == -1) ? s11 : j2 + "&contest_id=" + s12) + ">" + s16 + "</a></td>");
					if (k2 != 7)
						out.print("<td><font color=" + s15 + ">" + ResultType.getResultDescript(k2) + "</font></td>");
					else
						out.print("<td>" + (!flag3 && (!flag4 || flag) && !UserModel.isUser(request, s10) ? "" : "<a href=showcompileinfo?solution_id=" + i + " target=_blank>") + "<font color=green>" + ResultType.getResultDescript(k2) + "</font></a></td>");

					if (k2 == 0 && flag8)
						out.print("<td>" + resultset1.getString("memory") + "K</td><td>" + resultset1.getString("time") + "MS</td>");
					else
						out.print("<td>&nbsp;</td><td>&nbsp;</td>");
					PreparedStatement preparedstatement9;
					ResultSet resultset9;
					String user1 = null;
					String user2 = s10;
					boolean flags1 = false;
					boolean flags2 = false;
					if (UserModel.isLoginned(request) && !UserModel.isUser(request, user2))
					{
						user1 = UserModel.getCurrentUser(request).getUser_id();

						preparedstatement9 = connection1.prepareStatement("select share from users where user_id=?");
						// System.out.println(user1+" "+user2);
						preparedstatement9.setString(1, user1);
						resultset9 = preparedstatement9.executeQuery();
						if (resultset9.next() && resultset9.getInt("share") == 1)
						{
							preparedstatement9 = connection1.prepareStatement("select min(result) as result from solution where user_id=? and problem_id=?");
							preparedstatement9.setString(1, user1);
							preparedstatement9.setString(2, s11);
							resultset9 = preparedstatement9.executeQuery();
							if (resultset9.next() && resultset9.getInt("result") == 0)
							{
								if (resultset9.wasNull() == false)
									flags1 = true;
								// System.out.println(user1+" "+resultset9.getInt("result")+" "+resultset.getString("problem_id")+" "+resultset9.wasNull());
							}
							preparedstatement9 = connection1.prepareStatement("select share from users where user_id=?");
							preparedstatement9.setString(1, user2);
							resultset9 = preparedstatement9.executeQuery();
							if (resultset9.next() && resultset9.getInt("share") == 1)
								flags2 = true;
						}
					}
					// System.out.println(flags1+" "+flags2);
					if ((flags1 && flags2) || flag3 || flag4 || UserModel.isUser(request, s10))
						out.print("<td><a href=showsource?solution_id=" + i + "&contest_id=" + l + " target=_blank>" + s14 + "</a></td>");
					else
						out.print("<td>" + s14 + "</td>");
					if (flag8)
						out.print("<td>" + resultset1.getInt("len") + "b</td>");
					else
						out.print("<td>&nbsp;</td>");
					out.print("<td>" + s13 + "</td></tr>\n");
				} while (flag5 && resultset1.next() || flag6 && resultset1.previous());
			preparedstatement1.close();
			connection1.close();
			connection1 = null;
		} catch (Exception exception2)
		{
			ErrorProcess.ExceptionHandle(exception2, out);
			return;
		}
		out.print("</table></div>\n<p align=center>[<a href=conteststatus" + s9 + ">Top</a>]&nbsp;&nbsp;");
		if (flag2)
			s9 += "&";
		else
			s9 += "?";
		if (p > 1)
			out.print("[<a href=conteststatus" + s9 + "p=" + (p - 1) + "><font color=blue>Previous Page</font></a>]&nbsp;&nbsp;");
		else
			out.print("[Previous Page]&nbsp;&nbsp;");
		if (p < pageall)
			out.print("[<a href=conteststatus" + s9 + "p=" + (p + 1) + "><font color=blue>Next Page</font></a>]&nbsp;&nbsp;");
		else
			out.print("[Next Page]&nbsp;&nbsp;");
		out.print("[<a href=conteststatus" + s9 + "p=" + pageall + "><font color=blue>Last Page</font></a>]&nbsp;&nbsp;" + p + " of " + pageall + "</p>\n");
		FormattedOut.printBottom(request, out);
		out.close();
		return;
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException
	{
		// Put your code here
	}

}
