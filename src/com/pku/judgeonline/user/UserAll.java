package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class UserAll extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserAll()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		String s;
		String s1;
		String s2;
		Connection connection;
		int i;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		s = null;
		s1 = null;
		s2 = null;
		s = request.getParameter("uid1");
		s1 = request.getParameter("uid2");
		s2 = request.getParameter("uid3");
		if (s == null || s1 == null)
			return;
		connection = null;
		i = ServerConfig.MAX_PROBLEM_ID;
		PreparedStatement preparedstatement;

		connection = DBConfig.getConn();
		try
		{
			preparedstatement = connection.prepareStatement("select * from users where user_id=?");
			preparedstatement.setString(1, s);
			ResultSet resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error((new StringBuilder()).append("No such user:").append(s).toString(), out);
				preparedstatement.close();
				connection.close();
				return;
			}
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select * from users where user_id=?");
			preparedstatement.setString(1, s1);
			ResultSet resultset1 = preparedstatement.executeQuery();
			if (!resultset1.next())
			{
				ErrorProcess.Error((new StringBuilder()).append("No such user:").append(s1).toString(), out);
				preparedstatement.close();
				connection.close();
				return;
			}

			preparedstatement.close();
			char ac[] = new char[i];
			char ac1[] = new char[i];
			char ac2[] = new char[i];

			preparedstatement = connection.prepareStatement("select problem_id,min(result) as res from solution where user_id=? group by problem_id");
			preparedstatement.setString(1, s);
			ResultSet resultset2 = preparedstatement.executeQuery();
			for (int j = 0; j < i; j++)
				ac[j] = ac1[j] = ac2[j] = '\0';

			while (resultset2.next())
			{
				int i2 = resultset2.getInt("problem_id");
				int k2 = resultset2.getInt("res");

				if (k2 == 0)
					ac[i2] = '\001';
				else
					ac[i2] = '\002';
			}
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select problem_id,min(result) as res from solution where user_id=? group by problem_id");
			preparedstatement.setString(1, s1);
			for (ResultSet resultset3 = preparedstatement.executeQuery(); resultset3.next();)
			{
				int j2 = resultset3.getInt("problem_id");
				int l2 = resultset3.getInt("res");
				if (l2 == 0)
					ac1[j2] = '\001';
				else
					ac1[j2] = '\002';
			}

			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select problem_id,min(result) as res from solution where user_id=? group by problem_id");
			preparedstatement.setString(1, s2);
			for (ResultSet resultset4 = preparedstatement.executeQuery(); resultset4.next();)
			{
				int j3 = resultset4.getInt("problem_id");
				int l3 = resultset4.getInt("res");
				if (l3 == 0)
					ac2[j3] = '\001';
				else
					ac2[j3] = '\002';
			}
			preparedstatement.close();
			FormattedOut.printHead(out, request, connection, (new StringBuilder()).append(s).append(" vs ").append(s1).toString());
			out.print((new StringBuilder()).append("<div align=center><font size=5 color=blue><a href=\"userstatus?user_id=").append(s).append("\">").append(s).append("</a> vs <a href=\"userstatus?user_id=").append(s1).append("\">").append(s1).append("</a></font></div>").toString());
			out.println("<TABLE align=center cellSpacing=0 cellPadding=0 width=600 border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.print("<tr valign=bottom><td><br>");
			out.print("<form action=usercmp method=get>");
			out.print((new StringBuilder()).append("Compare <input type=text size=10 name=uid1 value=").append(s).append(">").toString());
			out.print((new StringBuilder()).append("and <input type=text size=10 name=uid2 value=").append(s1).append(">").toString());
			out.print("<input type=submit value=GO></form>");
			out.print("</td></tr>");
			s = (new StringBuilder()).append("<a href=\"userstatus?user_id=").append(s).append("\">").append(s).append("</a>").toString();
			s1 = (new StringBuilder()).append("<a href=\"userstatus?user_id=").append(s1).append("\">").append(s1).append("</a>").toString();

			out.println((new StringBuilder()).append("</td></tr><tr bgcolor=#6589D1><td>Problems both ").append(s).append(" and ").append(s1).append(" accepted:</td></tr><tr><td>").toString());
			for (int i1 = 0; i1 < i; i1++)
				if (ac[i1] == '\001' && ac1[i1] == '\001' && ac2[i1] == '\001')
				{
					out.print((new StringBuilder()).append("<a href=showproblem?problem_id=").append(i1).append(">").append(i1).append(" </a>").toString());
					PreparedStatement preparedstatement1 = connection.prepareStatement("select title,accepted from problem where problem_id=?");
					preparedstatement1.setInt(1, i1);
					ResultSet resultset5 = preparedstatement1.executeQuery();
					if (resultset5.next())
						out.println(resultset5.getString("title") + "  " + resultset5.getString("accepted"));
					out.println("<br>");
				}

			// preparedstatement5.close();
			out.println("</td></tr></table>");
			FormattedOut.printBottom(request, out);
			connection.close();
		} catch (Exception exception)
		{
			try
			{
				connection.close();
			} catch (Exception exception1)
			{
			}
			ErrorProcess.ExceptionHandle(exception, out);
		}
		out.close();
		return;
	}

	public void destroy()
	{
	}
}
