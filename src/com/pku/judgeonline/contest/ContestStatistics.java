package com.pku.judgeonline.contest;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ContestStatistics extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ContestStatistics()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		long l;
		Connection connection;
		PreparedStatement preparedstatement;
		ResultSet resultset;
		long l1;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		try
		{
			l = Integer.parseInt(request.getParameter("contest_id"));
		} catch (Exception exception)
		{
			ErrorProcess.Error("No such contest", out);
			return;
		}
		String str = request.getParameter("contest_id");
		connection = DBConfig.getConn();
		preparedstatement = null;
		resultset = null;
		l1 = System.currentTimeMillis();
		FormattedOut.printContestHead(out, l, "Contest Statistics", request);
		int k2;
		boolean flag2;
		try
		{
			preparedstatement = connection.prepareStatement("select title,end_time,private from contest where contest_id=? and start_time<? and UPPER(defunct)='N'");
			preparedstatement.setLong(1, l);
			preparedstatement.setTimestamp(2, new Timestamp(l1));
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
			try
			{
				connection.close();
			} catch (Exception exception3)
			{
			}
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
		String as[];
		String as1[];
		long al[];
		long al1[];
		try
		{
			k2 = resultset.getInt("private");
			flag2 = k2 > 0;
			if (flag2 && !Tool.permission(connection, request, l))
			{
				ErrorProcess.Error("It's a private contest.You have no permission.", out);
				return;
			}

			long l2 = resultset.getTimestamp("end_time").getTime();
			String s1 = resultset.getString("title");
			out.print((new StringBuilder()).append("<p align=center><font size=5 color=blue>Contest Statistics--").append(s1).toString());
			if (l2 > l1)
				out.print((new StringBuilder()).append("<br>Time to go:").append(Tool.formatTime((l2 - l1) / 1000L)).toString());
			preparedstatement.close();
			out.print("</font></p>");
			out.print("<TABLE align=center cellSpacing=0 cellPadding=0 width=600 border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.print("<tr bgcolor=#6589D1><th>&nbsp;</th><th>AC</th><th>PE</th><th>WA</th><th>TLE</th><th>MLE</th><th>RE</th><th>OLE</th><th>CE</th><th>Others</th><th>Total</th>");
			as = LanguageType.GetDescriptions();
			String s2 = "select ";
			for (int i = 0; i < as.length; i++)
				s2 = (new StringBuilder()).append(s2).append("count(if(language=").append(i).append(",1,null)),").toString();

			s2 = (new StringBuilder()).append(s2).append("problem_id,num,count(if(result=0,1,null)) as AC,count(if(result=1,1,null)) as PE,count(if(result=2,1,null)) as TLE,count(if(result=3,1,null)) as MLE,count(if(result=4,1,null)) as WA,count(if(result=5,1,null)) as RE,count(if(result=6,1,null)) as OLE,count(if(result=7,1,null)) as CE,count(if(result>7,1,null)) as Others,count(*) as Total from solution where contest_id=? group by problem_id order by num").toString();
			preparedstatement = connection.prepareStatement(s2);
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			out.print("<th>&nbsp;</th>");
			for (int j = 0; j < as.length; j++)
				out.print((new StringBuilder()).append("<th>").append(as[j]).append("</th>").toString());

			out.println("</tr>");
			as1 = (new String[]
			{ "AC", "PE", "WA", "TLE", "MLE", "RE", "OLE", "CE", "Others", "Total" });
			al = new long[as1.length];
			al1 = new long[as.length];
			for (int k = 0; k < al.length; k++)
				al[k] = 0L;

			for (int i1 = 0; i1 < al1.length; i1++)
				al1[i1] = 0L;

			for (; resultset.next(); out.println("</tr>"))
			{
				//long l4 = resultset.getLong("problem_id");
				long l3 = resultset.getLong("num");
				out.print((new StringBuilder()).append("<tr><th><a href=showproblem?problem_id=").append(l3).append("&contest_id=" + str + ">").append((char) (int) (65L + l3)).append("</a></th>").toString());
				for (int j1 = 0; j1 < as1.length; j1++)
				{
					long l5 = resultset.getLong(as1[j1]);
					al[j1] += l5;
					if (l5 == 0L)
					{
						out.print("<td>&nbsp;</td>");
						continue;
					}
					if (j1 == as1.length - 1)
						out.print((new StringBuilder()).append("<th><a href=conteststatus?contest_id=").append(l).append("&problem_id=").append(l3).append(">").append(l5).append("</a></th>").toString());
					else
						out.print((new StringBuilder()).append("<td>").append(l5).append("</td>").toString());
				}

				out.print("<td>&nbsp;</td>");
				for (int k1 = 0; k1 < as.length; k1++)
				{
					long l6 = resultset.getLong(k1 + 1);
					al1[k1] += l6;
					if (l6 == 0L)
						out.print("<td>&nbsp;</td>");
					else
						out.print((new StringBuilder()).append("<td>").append(l6).append("</td>").toString());
				}

			}

			preparedstatement.close();
			connection.close();
		} catch (Exception exception2)
		{
			ErrorProcess.ExceptionHandle(exception2, out);
			return;
		}
		out.print("<tr><th>Total</th>");
		for (int i2 = 0; i2 < as1.length; i2++)
		{
			if (al[i2] == 0L)
			{
				out.print("<td>&nbsp;</td>");
				continue;
			}
			if (i2 == as1.length - 1)
				out.print((new StringBuilder()).append("<th>").append(al[i2]).append("</th>").toString());
			else
				out.print((new StringBuilder()).append("<td>").append(al[i2]).append("</td>").toString());
		}

		out.print("<td>&nbsp;</td>");
		for (int j2 = 0; j2 < as.length; j2++)
			if (al1[j2] == 0L)
				out.print("<td>&nbsp;</td>");
			else
				out.print((new StringBuilder()).append("<td>").append(al1[j2]).append("</td>").toString());

		out.println("</tr></table>");
		FormattedOut.printBottom(request, out);
		out.close();
		out.close();
		return;
	}

	public void destroy()
	{
	}
}
