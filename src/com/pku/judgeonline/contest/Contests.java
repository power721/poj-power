package com.pku.judgeonline.contest;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Contests extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Contests()
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
		int type = 0;
		String s = request.getParameter("type");
		String sql = null;
		if (s == null || s.trim().equals(""))
			type = 0;
		else if (s.trim().equals("r"))
			type = 1;
		else if (s.trim().equals("s"))
			type = 2;
		else if (s.trim().equals("p"))
			type = 3;
		try
		{
			Connection connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, "Contests");
			// if(type==0)
			boolean flag = UserModel.isAdminLoginned(request);
			sql = "select * from contest where end_time>? and UPPER(defunct)='N' order by contest_id";
			if (type == 1)
				sql = "select * from contest where start_time<? and end_time>? and UPPER(defunct)='N' order by contest_id";
			PreparedStatement preparedstatement = connection.prepareStatement(sql);
			preparedstatement.setTimestamp(1, ServerConfig.getSystemTime());
			if (type == 1)
				preparedstatement.setTimestamp(2, ServerConfig.getSystemTime());
			ResultSet resultset = preparedstatement.executeQuery();
			out.println("<center>");
			out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=80% border=1 background=images/table_back.jpg>");
			out.println("<tr><td align=center width=10%>ID</td><td align=center width=20%>Title</td>");
			if (type == 1)
				out.println("<td width=20% align=center>Start Time</td><td width=20% align=center>End Time</td><td width=15% align=center>Status</td>");
			else
				out.println("<td width=50% align=center>Status</td>");
			out.println("<td width=15% align=center>Type</td></tr>");
			for (; resultset.next(); out.println("</tr>"))
			{
				String str = resultset.getString("contest_id");
				Timestamp timestamp = resultset.getTimestamp("start_time");
				boolean flag1 = timestamp.getTime() < System.currentTimeMillis();
				out.println((new StringBuilder()).append("<tr><td align=center><a href=showcontest?contest_id=" + str + "><font color=black>" + str + "</font></a></td><td align=center><a href=showcontest?contest_id=").append(str).append(">").append(resultset.getString("title")).append("</a></td>").toString());
				if (flag1)
				{
					if (type == 1)
						out.println("<td align=center>" + timestamp + "</td><td align=center>" + resultset.getTimestamp("end_time") + "</td><td align=center><a href=showcontest?contest_id=" + str + ">RUNNING</a></td>");
					else
						out.println("<td align=center><a href=showcontest?contest_id=" + str + ">RUNNING</a></td>");
				} else
					out.println((new StringBuilder()).append("<td align=center><font color=red>Start at ").append(timestamp).append("</font></td>").toString());
				int ii = resultset.getInt("private");
				boolean flagc = (boolean) (ii > 0);
				out.println("<td align=center><font color=blue>" + (flagc ? (flag ? "<a href=admin.contestuser?contest_id=" + str + " title=Add user to the contest.><font color=red>" : "") + "Private" + (flag ? "</font></a>" : "") : "Public") + "</font></td>");
			}
			long l1 = System.currentTimeMillis();
			out.println("<tr><td align=center colspan=6></td></tr><tr><td align=center colspan=6><br>Current System Time:<font color=#993399><span id=\"cur_time\">" + (new Timestamp(l1)) + "</span></font></td></tr></table></center>");

			out.println("<script type=\"text/javascript\" language=\"javascript\">");
			out.println("var timeDiff = " + l1 + " - new Date().valueOf();");
			out.println("	");
			out.println("	function updateTime() {");
			out.println("		$(\"#cur_time\").html(new Date(new Date().valueOf() + timeDiff).format(\"yyyy-MM-dd hh:mm:ss\"));");
			out.println("	}");
			out.println("	updateTime();");
			out.println("	setInterval(updateTime, 1000);");
			out.println("</script>");

			preparedstatement.close();
			connection.close();
			FormattedOut.printBottom(request, out);
			out.close();
		} catch (Exception exception)
		{
			exception.printStackTrace(out);
		}
	}

	public void destroy()
	{
	}
}
