package com.pku.judgeonline.admin.contest;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ContestManagerPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContestManagerPage()
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
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
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
		int Private = 0;
		connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			preparedstatement = connection.prepareStatement("select title as ctitle ,start_time,end_time,description,private,contest_id from contest where contest_id=? and UPPER(contest.defunct) = 'N'");
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
			Private = resultset.getInt("private");
			String s1 = resultset.getString("ctitle");
			String id = resultset.getString("contest_id");
			long l1 = System.currentTimeMillis();
			FormattedOut.printHead(out, s1);
			boolean flag2 = resultset.getTimestamp("start_time").getTime() <= l1;
			boolean flag3 = resultset.getTimestamp("end_time").getTime() <= l1;
			out.println("<p align=\"center\"><b><font size=5 face=\"Arial\">" + (Private != 0 ? "Private" : "Public") + " Contest - <a href=showcontest?contest_id=" + s + ">" + s1 + "</a></font></b>");
			out.println("&nbsp;&nbsp;<a href='admin.editcontestpage?contest_id=" + s + "'>Edit</a>");
			out.println((new StringBuilder()).append("<font face=Arial><p align=\"center\">Start time:&nbsp;&nbsp;<font color=\"#993399\">").append(resultset.getTimestamp("start_time")).append("</font>&nbsp;&nbsp;End time:&nbsp;&nbsp;<font color=\"#993399\">").append(resultset.getTimestamp("end_time")).append("</font><br>Current System Time:&nbsp;&nbsp;<font color=\"#993399\">").append("<span id=\"cur_time\">" + new Timestamp(l1) + "</span>").append("</font>&nbsp;&nbsp;Contest Status:&nbsp;&nbsp;<font color=red>").toString());

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
				out.print("Not Started");
			if (flag2 && !flag3)
				out.print("Running");
			if (flag3)
				out.print("Ended");
			out.println("</font></p>");
			out.println("</font>");
			String s2 = resultset.getString("description");
			preparedstatement.close();
			out.println("<div align=\"center\">");
			if (s2 != null)
				out.println(s2 + "<br>\n");
			if (Private != 0)
				out.println("<br/><a href=admin.contestuser?contest_id=" + id + "><font size=4>Add user to this contest.</font></a><br/>");
			out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=600 border=1 background=images/table_back.jpg>");
			out.println("<tr bgcolor=#6589D1 align=center>");
			out.println("<td width=\"20%\"><b>Problem Id</b></td>");
			out.println("<td width=\"30%\"><b>Title</b></td>");
			out.println("<td width=10%><b>Action</b></td></tr>");
			preparedstatement = connection.prepareStatement("select problem_id,title,num from contest_problem where contest_id=? order by num");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			for (; resultset.next(); out.println("</tr>"))
			{
				String s3 = resultset.getString("problem_id");
				int i = resultset.getInt("num");
				out.println("<tr>");
				out.println((new StringBuilder()).append("<td align=center><b>").append(s3).append(" Problem ").append((char) (i + 65)).append("</b></td>").toString());
				out.println((new StringBuilder()).append("<td align=left><a href=admin.showproblem?problem_id=").append(s3).append(">").append(resultset.getString("title")).append("</a></td>").toString());
				out.println((new StringBuilder()).append("<td align=center><a href=admin.contestmanager?action=del&contest_id=").append(s).append("&problem_id=").append(s3).append(">DEL</a></td>").toString());
			}

			// out.println((new StringBuilder()).append((new
			// StringBuilder()).append("<form method=get action=admin.contestmanager?action=add&contest_id=").append(s).append(">").toString()).append("<input type=hidden name=contest_id value=").append(s).append("><font color=blue>Prob.ID:</font><input type=text name=problem_id size=6><span id=ptitle></span><input type=Submit value=add name=action></form>").toString());
			out.println((new StringBuilder()).append((new StringBuilder()).append("<form method=get action=admin.contestmanager?action=add&contest_id=").append(s).append(">").toString()).append("<input type=hidden name=contest_id value=").append(s).append("><font color=blue>Prob.ID:</font><input type=text name=problem_id size=6>&nbsp;<font color=blue>Title:</font><input type=text name=ptitle size=30 value=>&nbsp;<input type=Submit value=add name=action></form>").toString());
			// out.println("<span id=ptitle display=none></span>");
			out.println("</table></div>");
			out.println("<script language=\"javascript\" type=\"text/javascript\">");
			out.println("$(\"input[name='problem_id']\").keyup(function(){");
			out.println("	  $.get(\"ajax\", { name: \"ptitle\", val: this.value },");
			out.println("	  	  function(data){");
			out.println("	  		  $(\"input[name='ptitle']\").val(data);  });");
			out.println("	});");
			out.println("</script>");
			preparedstatement.close();
			connection.close();
			FormattedOut.printBottom(out);
		} catch (SQLException sqlexception)
		{
			ErrorProcess.ExceptionHandle(sqlexception, out);
		}
		out.close();
		return;
	}

	public void destroy()
	{
	}
}
