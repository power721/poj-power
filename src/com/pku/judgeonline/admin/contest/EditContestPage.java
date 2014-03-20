package com.pku.judgeonline.admin.contest;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.util.Date;

public class EditContestPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EditContestPage()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		} else
		{
			response.setContentType("text/html; charset=UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			String s;
			long l;
			Connection connection;
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
				ErrorProcess.Error("No such contest", out);
				return;
			}
			connection = DBConfig.getConn();
			PreparedStatement preparedstatement;
			ResultSet resultset = null;
			try
			{
				preparedstatement = connection.prepareStatement("select title as ctitle ,start_time,end_time,description,private,freeze from contest where contest_id=? and UPPER(contest.defunct) = 'N'");
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
				
				Calendar calendar = Calendar.getInstance();
				String s1 = resultset.getString("ctitle");
				int Private = resultset.getInt("private");
				int freeze = resultset.getInt("freeze");
				long st = resultset.getTimestamp("start_time").getTime();
				Date sdate = new Date(st);
				// Time stime = new Time(st);
				calendar.setTime(sdate);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1;
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int min = calendar.get(Calendar.MINUTE);
				long et = resultset.getTimestamp("end_time").getTime();
				Date edate = new Date(et);
				// Time etime = new Time(et);
				calendar.setTime(edate);
				int eyear = calendar.get(Calendar.YEAR);
				int emonth = calendar.get(Calendar.MONTH) + 1;
				int eday = calendar.get(Calendar.DAY_OF_MONTH);
				int ehour = calendar.get(Calendar.HOUR_OF_DAY);
				int emin = calendar.get(Calendar.MINUTE);
				String s2 = resultset.getString("description");

				// response.setContentType("text/html; charset=UTF-8");
				// request.setCharacterEncoding("UTF-8");
				// PrintWriter out = response.getWriter();
				FormattedOut.printHead(out, "Edit the contest");
				// Calendar calendar = Calendar.getInstance();
				// int i = calendar.get(1);
				// int j = calendar.get(2) + 1;
				out.println("<form method=POST action=admin.editcontest?contest_id=" + s + ">");
				out.println("<p align=center><font size=4 color=#333399>Edit the Contest</font></p>");
				out.println("<p align=left>Title:<input type=text name=title value=\"" + s1 + "\" size=71></p>");
				out.println("<p align=left>Description:<br><textarea rows=8 name=description cols=76>" + s2 + "</textarea></p>");
				out.println("<p align=left>Start Time:<br>&nbsp;&nbsp;&nbsp;");
				out.println((new StringBuilder()).append("Year:<input type=text name=syear value=").append(year).append(" size=7 >").toString());
				out.println((new StringBuilder()).append("Month:<input type=text name=smonth value=").append(month).append(" size=7 >").toString());
				out.println("Day:<input type=text name=sday value=" + day + " size=7 >&nbsp;");
				out.println("Hour:<input type=text name=shour value=" + hour + " size=7 >&nbsp;");
				out.println("Minute:<input type=text name=sminute value=" + min + " size=7 ></p>");
				out.println("<p align=left>End Time:<br>&nbsp;&nbsp;&nbsp;");
				out.println((new StringBuilder()).append("Year:<input type=text name=eyear value=").append(eyear).append(" size=7 >").toString());
				out.println((new StringBuilder()).append("Month:<input type=text name=emonth value=").append(emonth).append(" size=7 >").toString());
				out.println("Day:<input type=text name=eday value=" + eday + " size=7 >&nbsp;");
				out.println("Hour:<input type=text name=ehour value=" + ehour + " size=7 >&nbsp;");
				out.println("Minute:<input type=text name=eminute value=" + emin + " value=0 size=7 ></p>");
				out.println("<br/>Type<br/>&nbsp;&nbsp;<input type=\"radio\" name=\"private\" " + (Private == 0 ? "checked" : "") + "  value=\"0\">Public<input type=\"radio\" name=\"private\" " + (Private == 1 ? "checked" : "") + "  value=\"1\">Private<input type=\"radio\" name=\"private\" " + (Private == 2 ? "checked" : "") + " value=\"2\">Strict Private");
				out.println("<br>Freeze Board?<input type=\"checkbox\" name=\"freeze\" " + (freeze == 1 ? "checked" : "") + " value=\"1\" />");
				out.println("<div align=center>");
				out.println("<p><input type=submit value=Submit name=submit><input type=reset value=Reset name=reset></p>");
				out.println("</div></form>");
				FormattedOut.printBottom(out);
				out.close();
			} catch (SQLException sqlexception)
			{
				ErrorProcess.ExceptionHandle(sqlexception, out);
			}
		}
	}

	public void destroy()
	{
	}
}
