package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class EditAnnouncePage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EditAnnouncePage()
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
		}
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String s = request.getParameter("announce_id");
		if (s == null || s.trim().equals(""))
		{
			ErrorProcess.Error("No such Announce", out);
			return;
		}
		long l = 0L;
		try
		{
			l = Integer.parseInt(s);
		} catch (Exception exception)
		{
			ErrorProcess.Error("No such Announce", out);
			return;
		}
		Connection connection = DBConfig.getConn();
		ResultSet resultset = null;
		try
		{
			PreparedStatement preparedstatement = connection.prepareStatement("select defunct,title as ctitle ,start_time,end_time,content from announce where id=?");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error("No such Announce", out);
				preparedstatement.close();
				preparedstatement = null;
				connection.close();
				connection = null;
				return;
			}
		} catch (SQLException sqlexception)
		{
			ErrorProcess.ExceptionHandle(sqlexception, out);
		}
		try
		{
			Calendar calendar = Calendar.getInstance();
			String s1 = resultset.getString("ctitle");
			long l1 = resultset.getTimestamp("start_time").getTime();
			Date date = new Date(l1);
			calendar.setTime(date);
			int i = calendar.get(1);
			int j = calendar.get(2) + 1;
			int k = calendar.get(5);
			int i1 = calendar.get(11);
			int j1 = calendar.get(12);
			long l2 = resultset.getTimestamp("end_time").getTime();
			Date date1 = new Date(l2);
			calendar.setTime(date1);
			int k1 = calendar.get(1);
			int i2 = calendar.get(2) + 1;
			int j2 = calendar.get(5);
			int k2 = calendar.get(11);
			int i3 = calendar.get(12);
			String s2 = resultset.getString("content");
			String s3 = resultset.getString("defunct");
			boolean flag2 = s3.equals("N");

			FormattedOut.printHead(out, "Edit the Announce");
			out.println((new StringBuilder()).append("<form method=POST action=admin.editannounce?announce_id=").append(s).append(">").toString());
			out.println("<p align=center><font size=4 color=#333399>Edit the Announce</font></p>");
			out.println((new StringBuilder()).append("<p align=left>Title:<textarea class=ckeditor rows=5 name=title cols=50>").append(s1).append("</textarea></p>").toString());
			out.println((new StringBuilder()).append("<p align=left>Content:<br><textarea class=ckeditor rows=20 name=content  cols=76>").append(s2).append("</textarea></p>").toString());
			out.println("<p align=left>Start Time:<br>&nbsp;&nbsp;&nbsp;");
			out.println((new StringBuilder()).append("Year:<input type=text name=syear value=").append(i).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Month:<input type=text name=smonth value=").append(j).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Day:<input type=text name=sday value=").append(k).append(" size=7 >&nbsp;").toString());
			out.println((new StringBuilder()).append("Hour:<input type=text name=shour value=").append(i1).append(" size=7 >&nbsp;").toString());
			out.println((new StringBuilder()).append("Minute:<input type=text name=sminute value=").append(j1).append(" size=7 ></p>").toString());
			out.println("<p align=left>End Time:<br>&nbsp;&nbsp;&nbsp;");
			out.println((new StringBuilder()).append("Year:<input type=text name=eyear value=").append(k1).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Month:<input type=text name=emonth value=").append(i2).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Day:<input type=text name=eday value=").append(j2).append(" size=7 >&nbsp;").toString());
			out.println((new StringBuilder()).append("Hour:<input type=text name=ehour value=").append(k2).append(" size=7 >&nbsp;").toString());
			out.println((new StringBuilder()).append("Minute:<input type=text name=eminute value=").append(i3).append(" value=0 size=7 ></p>").toString());
			out.println((new StringBuilder()).append("<input type=\"radio\" name=\"hide\" ").append(flag2 ? "checked" : "").append(" value=\"N\">Show<input type=\"radio\" name=\"hide\" ").append(flag2 ? "" : "checked").append(" value=\"Y\">Hide").toString());
			out.println("<div align=center>");
			out.println("<p><input type=submit value=Submit name=submit><input type=reset value=Reset name=reset></p>");
			out.println("</div></form>");
			FormattedOut.printBottom(out);
			out.close();
			return;
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
		}
	}

	public void destroy()
	{
	}
}
