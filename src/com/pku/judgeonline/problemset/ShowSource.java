package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.LanguageType;
import com.pku.judgeonline.common.ResultType;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShowSource extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		String str2 = null;
		String str3 = null;
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter PrintWriter = paramHttpServletResponse.getWriter();
		Connection Connection = DBConfig.getConn();
		String str4 = ServerConfig.getValue("Source");
		String spid = "";
		long l = 0L;
		int i = 0;
		int pid = 0;
		int num = 0;
		boolean CEflag = false;
		String str1;
		String cid = null;
		String cstr = paramHttpServletRequest.getParameter("contest_id");
		try
		{
			str1 = paramHttpServletRequest.getParameter("solution_id");
			l = Long.parseLong(str1);
		} catch (Exception Exception1)
		{
			ErrorProcess.ExceptionHandle(Exception1, PrintWriter);
			return;
		}
		PrintWriter.println("<html>\n<head>");
		PrintWriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		PrintWriter.println("<title>Source Code</title>\n<link href=\"css/code.css\" type=\"text/css\" rel=\"Stylesheet\" rev=\"Stylesheet\" media=\"all\"/><script type=\"text/javascript\" src=\"js/oj.js\"></script>");
		PrintWriter.println("<link type='text/css' rel='stylesheet' href='js/syntaxhighlighter/styles/shCore.css'/>");
		PrintWriter.println("<link type='text/css' rel='stylesheet' href='js/syntaxhighlighter/styles/shThemeDefault.css'/>");
		PrintWriter.println("    <script src='js/syntaxhighlighter/scripts/shCore.js' type='text/javascript'></script>");
		PrintWriter.println("    <script src='js/syntaxhighlighter/scripts/shBrushCpp.js' type='text/javascript'></script>");
		PrintWriter.println("    <script src='js/syntaxhighlighter/scripts/shBrushJava.js' type='text/javascript'></script>");
		PrintWriter.println("    <script src='js/syntaxhighlighter/scripts/shBrushDelphi.js' type='text/javascript'></script>");
		PrintWriter.println("    <script src='js/syntaxhighlighter/scripts/shBrushPython.js' type='text/javascript'></script>");
		PrintWriter.println("    <script src='js/syntaxhighlighter/scripts/shBrushXml.js' type='text/javascript'></script>");
		PrintWriter.println("</head>\n<body>");
		PreparedStatement PreparedStatement1 = null;
		ResultSet ResultSet1 = null;
		try
		{
			String str5 = "SELECT * FROM solution  WHERE solution_id = ?";
			PreparedStatement1 = Connection.prepareStatement(str5);
			PreparedStatement1.setString(1, str1);
			ResultSet1 = PreparedStatement1.executeQuery();
			if (!ResultSet1.next())
			{
				ErrorProcess.Error("No such solution", PrintWriter);
				ResultSet1.close();
				PreparedStatement1.close();
				Connection.close();
				return;
			}
		} catch (Exception Exception2)
		{
			ErrorProcess.ExceptionHandle(Exception2, PrintWriter);
			return;
		}
		int m;
		int j;
		boolean bool1;
		Object Object4;
		String str7 = "";
		Object Object5;
		Object Object6;
		try
		{
			String str6 = ResultSet1.getString("contest_id");
			cid = str6;
			str3 = ResultSet1.getString("user_id");
			spid = ResultSet1.getString("problem_id");
			pid = Integer.parseInt(spid);
			m = 0;
			j = 1;
			bool1 = UserModel.isSourceBrowser(paramHttpServletRequest);
			ResultSet ResultSet3;
			if ((str6 != null) && (bool1))
			{
				Object4 = Connection.prepareStatement("select end_time from contest where contest_id=? and UPPER(defunct)='N'");
				((PreparedStatement) Object4).setInt(1, Integer.parseInt(str6));
				ResultSet3 = ((PreparedStatement) Object4).executeQuery();
				if (ResultSet3.next())
					j = ResultSet3.getTimestamp("end_time").getTime() < System.currentTimeMillis() ? 1 : 0;
				((PreparedStatement) Object4).close();
				Object4 = null;
			}
			if ((UserModel.isLoginned(paramHttpServletRequest)) && (!UserModel.isAdminLoginned(paramHttpServletRequest)))
			{
				Object4 = Connection.prepareStatement("select contest_id from problem where problem_id=? and UPPER(defunct)='N'");
				((PreparedStatement) Object4).setInt(1, pid);
				ResultSet3 = ((PreparedStatement) Object4).executeQuery();
				if (ResultSet3.next())
				{
					str7 = ResultSet3.getString("contest_id");
					if (str7 != null)
					{
						Object5 = Connection.prepareStatement("select start_time,end_time from contest where contest_id=? and UPPER(defunct)='N'");
						((PreparedStatement) Object5).setInt(1, Integer.parseInt(str7));
						Object6 = ((PreparedStatement) Object5).executeQuery();
						if ((((ResultSet) Object6).next()) && (((ResultSet) Object6).getTimestamp("end_time").getTime() > System.currentTimeMillis()) && (((ResultSet) Object6).getTimestamp("start_time").getTime() < System.currentTimeMillis()))
						{
							i = 1;
							m = ResultSet1.getTimestamp("in_date").getTime() < ((ResultSet) Object6).getTimestamp("start_time").getTime() ? 1 : 0;
							System.out.println(str3 + UserModel.isUser(paramHttpServletRequest, str3));
							if (!UserModel.isAdminLoginned(paramHttpServletRequest) && !UserModel.isUser(paramHttpServletRequest, str3))
							{
								ErrorProcess.Error("It's in a running contest.You have no permission.", PrintWriter);
								((PreparedStatement) Object4).close();
								Object4 = null;
								return;
							}
						}
						if ((((ResultSet) Object6).getTimestamp("start_time").getTime() > System.currentTimeMillis() ? 1 : 0) == 1)
						{
							if ((!UserModel.isAdminLoginned(paramHttpServletRequest)) && (str4 != null) && (str4.equals("FALSE")) && (!UserModel.isMember(paramHttpServletRequest)))
							{
								ErrorProcess.Error("Administrators have closed browsing code permissions.", PrintWriter);
								((PreparedStatement) Object4).close();
								Object4 = null;
								return;
							}
						}
						PreparedStatement preparedstatement = Connection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
						preparedstatement.setLong(1, Integer.parseInt(str7));
						preparedstatement.setLong(2, pid);
						ResultSet resultset = preparedstatement.executeQuery();
						if (!resultset.next())
						{
							ErrorProcess.Error((new StringBuilder()).append("Can not find contest problem (ID:").append(l).append(")<br><br>").toString(), PrintWriter);
							preparedstatement.close();
							Connection.close();
							return;
						}
						num = pid = resultset.getInt("num");
						spid = str7 + ":" + (char) (pid + 65);
						((PreparedStatement) Object5).close();
						Object5 = null;
					} else if ((!UserModel.isAdminLoginned(paramHttpServletRequest)) && (str4 != null) && (str4.equals("FALSE")) && (!UserModel.isMember(paramHttpServletRequest)))
					{
						ErrorProcess.Error("Administrators have closed browsing code permissions.", PrintWriter);
						((PreparedStatement) Object4).close();
						Object4 = null;
						return;
					}

					((PreparedStatement) Object4).close();
					Object4 = null;
				}
			}

			if ((m != 0) && (!UserModel.isMember(paramHttpServletRequest)))
			{
				ErrorProcess.Error("You have no permission as there is a contest is running.", PrintWriter);
				PreparedStatement1.close();
				Connection.close();
				return;
			}
		} catch (Exception Exception3)
		{
			ErrorProcess.ExceptionHandle(Exception3, PrintWriter);
			return;
		}
		try
		{
			int k = 0;
			m = 0;
			if ((i == 0) && (UserModel.isLoginned(paramHttpServletRequest)) && (!UserModel.isUser(paramHttpServletRequest, str3)))
			{
				str2 = UserModel.getCurrentUser(paramHttpServletRequest).getUser_id();

				PreparedStatement PreparedStatement2 = Connection.prepareStatement("select share from users where user_id=?");
				PreparedStatement2.setString(1, str2);
				ResultSet ResultSet2 = PreparedStatement2.executeQuery();
				if ((ResultSet2.next()) && (ResultSet2.getInt("share") == 1))
				{
					PreparedStatement2 = Connection.prepareStatement("select min(result) as result from solution where user_id=? and problem_id=?");
					PreparedStatement2.setString(1, str2);
					PreparedStatement2.setInt(2, pid);
					ResultSet2 = PreparedStatement2.executeQuery();
					if ((ResultSet2.next()) && (ResultSet2.getInt("result") == 0))
					{
						if (!ResultSet2.wasNull())
						{
							k = 1;
						}
					}
					PreparedStatement2 = Connection.prepareStatement("select share from users where user_id=?");
					PreparedStatement2.setString(1, str3);
					ResultSet2 = PreparedStatement2.executeQuery();
					if ((ResultSet2.next()) && (ResultSet2.getInt("share") == 1))
					{
						m = 1;
					}
				}
			}
			if (((k == 0) || (m == 0)) && (!UserModel.isAdminLoginned(paramHttpServletRequest)) && ((!bool1) || (j == 0)) && (!UserModel.isUser(paramHttpServletRequest, str3)) && (!UserModel.isMember(paramHttpServletRequest)))
			{
				ErrorProcess.Error("You have no permission.", PrintWriter);
				PreparedStatement1.close();
				Connection.close();
				return;
			}

			Object4 = null;
			PrintWriter.println("<center><p align=center><font size=5 color=#333399>View Code</font></p>");
			if (UserModel.isAdminLoginned(paramHttpServletRequest))
			{
				PrintWriter.println("<a href=showsource?solution_id=" + (l - 1L) + ">[" + (l - 1L) + "]</a>    <a href=showsource?solution_id=" + (l + 1L) + ">[" + (l + 1L) + "]</a><br>");
			}
			PrintWriter.println("<p align=center><font color=#006600>");
			if(cid != null)
			{
				PrintWriter.println("Contest:<a href=showcontest?contest_id="+cid+">"+cid+"</a>&nbsp;&nbsp;Problem: </font>"+(char)(num+65)+"<br>\n");
			}
			PrintWriter.println("<font color=#006600>Problem ID:<a href=showproblem?problem_id=" + pid + (pid <= 26 ? "&contest_id=" + str7 : "") + ">" + spid + "</a>" + "&nbsp;&nbsp;User ID:</font><a href=userstatus?user_id=" + ResultSet1.getString("user_id") + "><font size=4>" + ResultSet1.getString("user_id") + "</font></a>");
			PrintWriter.println("<br><font color=#006600>Memory:</font>" + ResultSet1.getString("memory") + "K&nbsp;&nbsp;<font color=#006600>Time:</font>" + ResultSet1.getString("time") + "MS<br>");
			PrintWriter.println("<font color=#006600>Language:</font>" + (Object4 = LanguageType.getDesc(ResultSet1.getInt("language"))) + "&nbsp;&nbsp;<font color=#006600>Result:</font><font color=red>" + ResultType.getResultDescript(ResultSet1.getInt("result")) + "</font>");
			CEflag = ResultType.getResult(ResultSet1.getInt("result")).equals("CE");
			boolean bool2 = UserModel.isAdminLoginned(paramHttpServletRequest);
			str7 = "<a href=admin.rejudge?solution_id=" + str1 + (cstr==null ? "" : "&contest_id=" + cstr) + "><font color=red><u>Rejudge</u></font></a>";
			if (bool2)
				PrintWriter.println("<br>" + str7);
			PrintWriter.println("<ul><li><font color=#333399 size=5>Source</font></li></ul></center>");
			PreparedStatement1.close();
			PreparedStatement1 = Connection.prepareStatement("select uncompress(source) as source from source_code where solution_id=?");
			PreparedStatement1.setString(1, str1);
			ResultSet1 = PreparedStatement1.executeQuery();
			Object5 = "";
			Object6 = null;
			byte[] arrayOfByte = null;
			if (ResultSet1.next())
			{
				Object6 = ResultSet1.getBlob("source");
				arrayOfByte = ((Blob) Object6).getBytes(1L, (int) ((Blob) Object6).length());
				Object5 = new String(arrayOfByte, "UTF-8");
			}

			PreparedStatement1.close();
			String str8 = "cpp";
			if ((Object4 != null) && (((String) Object4).equals("Java")))
				str8 = "java";
			if ((Object4 != null) && (((String) Object4).equals("Pascal")))
				str8 = "pascal";
			if ((Object4 != null) && (((String) Object4).equals("Python")))
				str8 = "python";
			PrintWriter.println("<textarea class='brush: " + str8 + "'>");
			PrintWriter.println((String) Object5);
			PrintWriter.println("</textarea>");
			PrintWriter.println("<script>");
			PrintWriter.println((String) Object5);
			PrintWriter.println("</script>");
			PrintWriter.println("<script language='javascript' type='text/javascript'>SyntaxHighlighter.config.tagName = 'textarea';SyntaxHighlighter.all();</script>");
			if (CEflag)
			{
				PrintWriter.println("<div id=ce_info class=info>");
				PrintWriter.println("<h2>Compile Info</h2>");
				PreparedStatement1 = Connection.prepareStatement("SELECT * FROM compileinfo  WHERE solution_id = ?");
				PreparedStatement1.setString(1, str1);
				ResultSet1 = PreparedStatement1.executeQuery();
				if (ResultSet1.next())
				{
					PrintWriter.println(Tool.dealCompileInfo(ResultSet1.getString("error")));
				}
				PrintWriter.println("</div>");
			}

			String ss = paramHttpServletRequest.getRequestURI();
			String ss1 = paramHttpServletRequest.getQueryString();
			if (ss1 != null)
				ss = ss + "?" + ss1;
			PreparedStatement1 = Connection.prepareStatement("update sessions set uri=?,last_activity=UNIX_TIMESTAMP() where session_id=?");
			PreparedStatement1.setString(1, ss);
			PreparedStatement1.setString(2, paramHttpServletRequest.getSession().getId());
			PreparedStatement1.executeUpdate();
			
			Connection.close();
			PrintWriter.println("</body>\n</html>");
		} catch (Exception Exception4)
		{
			ErrorProcess.ExceptionHandle(Exception4, PrintWriter);
			return;
		}
	}

	public void destroy()
	{
	}
}
