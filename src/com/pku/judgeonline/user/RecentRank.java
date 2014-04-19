package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class RecentRank extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RecentRank()
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
		long l = 10000L;
		try
		{
			l = Long.parseLong(request.getParameter("count"));
		} catch (Exception exception)
		{
			l = 10000L;
		}
		if (l > 10000L || l < 0L)
			l = 10000L;
		long l1 = ServerConfig.getCurrSolutionId() - l - 1L;
		String date = request.getParameter("date");
		Connection connection = DBConfig.getConn();
		try
		{
			PreparedStatement preparedstatement;
			FormattedOut.printHead(out, request, connection, "Recent Rank");
			out.print("<div align=center><form action=recentrank method=get>");
			out.print((new StringBuilder()).append("<font color=blue size=5>Rank of last <input type=text name=count size=5 value=").append(l).append("> submits.</font>").toString());
			out.print("<input type=submit value=Go>");
			out.print("</form>");
			out.print("<div align=center><b><a href=recentrank?date=day>[Day]</a> <a href=recentrank?date=week>[Week]</a> <a href=recentrank?date=month>[Month]</a></b></div>");
			out.print("</div>");
			if(date != null && !date.equals(""))
			{
				Calendar calendar = Calendar.getInstance();
				//calendar.clear();
				int year=calendar.get(Calendar.YEAR);//得到年
				int month=calendar.get(Calendar.MONTH);//得到月，从0开始
				int day=calendar.get(Calendar.DAY_OF_MONTH);//得到天
				calendar.set(year, month, day, 0, 0, 0);
				long t = calendar.getTimeInMillis();
				if("week".equals(date))
					t -= 86400000L*7;
				else if("month".equals(date))
					t -= 86400000L*30;
				//System.out.println(t);
				Timestamp timestamp = new Timestamp(t);
				//System.out.println(timestamp.toString());
				preparedstatement = connection.prepareStatement("select temp.user_id,sol,sub,nick from (select user_id,count(if(result=0 and valid=1,1,null)) as sol,count(*) as sub from solution where in_date>=? group by user_id order by sol desc,sub asc limit 50) as temp,users where temp.user_id=users.user_id");
				preparedstatement.setTimestamp(1, timestamp);
			}
			else
			{
				preparedstatement = connection.prepareStatement("select temp.user_id,sol,sub,nick from (select user_id,count(if(result=0 and valid=1,1,null)) as sol,count(*) as sub from solution where solution_id>? group by user_id order by sol desc,sub asc limit 50) as temp,users where temp.user_id=users.user_id");
				preparedstatement.setLong(1, l1);
			}
			ResultSet resultset = preparedstatement.executeQuery();
			out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=600 border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.print("<tr><th>No.</th><th>User Id</th><th>Nick</th><th>Sovled</th><th>Submit</th></tr>");
			int i = 1;
			for (; resultset.next(); out.println((new StringBuilder()).append("<td>").append(resultset.getLong("sub")).append("</td></tr>").toString()))
			{
				String s = resultset.getString("user_id");
				out.println((new StringBuilder()).append("<tr align=center><td>").append(i++).append("</td>").toString());
				out.print((new StringBuilder()).append("<td><a href=\"userstatus?user_id=").append(s).append("\">").append(s).append("</a></td>").toString());
				out.print((new StringBuilder()).append("<td><font color=green>").append(resultset.getString("nick")).append("</font></td>").toString());
				out.print((new StringBuilder()).append("<td>").append(resultset.getLong("sol")).append("</td>").toString());
			}

			preparedstatement.close();
			connection.close();
			out.println("</table>");
			FormattedOut.printBottom(request, out);
		} catch (Exception exception1)
		{
			try
			{
				connection.close();
			} catch (Exception exception2)
			{
			}
			ErrorProcess.ExceptionHandle(exception1, out);
		}
		out.close();
	}

	public void destroy()
	{
	}
}
