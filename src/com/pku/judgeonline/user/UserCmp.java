package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class UserCmp extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserCmp()
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
		Connection connection;
		int i;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		try
		{
			if (!Guard.Guarder(request, response, out))
				return;
			s = null;
			s1 = null;
			s = request.getParameter("uid1");
			s1 = request.getParameter("uid2");
			if (s == null || s1 == null)
				return;
			connection = null;
			i = ServerConfig.MAX_PROBLEM_ID;
			PreparedStatement preparedstatement;
			connection = DBConfig.getConn();
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
			preparedstatement = connection.prepareStatement("select problem_id,min(result) as res from solution where user_id=? group by problem_id");
			preparedstatement.setString(1, s);
			ResultSet resultset2 = preparedstatement.executeQuery();
			for (int j = 0; j < i; j++)
				ac[j] = ac1[j] = '\0';

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
			connection.close();
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
			out.println((new StringBuilder()).append("<tr bgcolor=#6589D1><td>Problems only ").append(s).append(" accepted:</td></tr><tr><td>").toString());
			for (int k = 0; k < i; k++)
				if (ac[k] == '\001' && ac1[k] != '\001')
					out.print((new StringBuilder()).append("<a href=showproblem?problem_id=").append(k).append(">").append(k).append(" </a>").toString());

			out.println((new StringBuilder()).append("</td></tr><tr bgcolor=#6589D1><td>Problems only ").append(s1).append(" accepted:</td></tr><tr><td>").toString());
			for (int l = 0; l < i; l++)
				if (ac[l] != '\001' && ac1[l] == '\001')
					out.print((new StringBuilder()).append("<a href=showproblem?problem_id=").append(l).append(">").append(l).append(" </a>").toString());

			out.println((new StringBuilder()).append("</td></tr><tr bgcolor=#6589D1><td>Problems both ").append(s).append(" and ").append(s1).append(" accepted:</td></tr><tr><td>").toString());
			for (int i1 = 0; i1 < i; i1++)
				if (ac[i1] == '\001' && ac1[i1] == '\001')
					out.print((new StringBuilder()).append("<a href=showproblem?problem_id=").append(i1).append(">").append(i1).append(" </a>").toString());

			out.println((new StringBuilder()).append("</td></tr><tr bgcolor=#6589D1><td>Problems only ").append(s).append(" tried but failed:</td></tr><tr><td>").toString());
			for (int j1 = 0; j1 < i; j1++)
				if (ac[j1] == '\002' && ac1[j1] != '\002')
					out.print((new StringBuilder()).append("<a href=showproblem?problem_id=").append(j1).append(">").append(j1).append(" </a>").toString());

			out.println((new StringBuilder()).append("</td></tr><tr bgcolor=#6589D1><td>Problems only ").append(s1).append(" tried but failed:</td></tr><tr><td>").toString());
			for (int k1 = 0; k1 < i; k1++)
				if (ac[k1] != '\002' && ac1[k1] == '\002')
					out.print((new StringBuilder()).append("<a href=showproblem?problem_id=").append(k1).append(">").append(k1).append(" </a>").toString());

			out.println((new StringBuilder()).append("</td></tr><tr bgcolor=#6589D1><td>Problems both ").append(s).append(" and ").append(s1).append(" tried but failed:</td></tr><tr><td>").toString());
			for (int l1 = 0; l1 < i; l1++)
				if (ac[l1] == '\002' && ac1[l1] == '\002')
					out.print((new StringBuilder()).append("<a href=showproblem?problem_id=").append(l1).append(">").append(l1).append(" </a>").toString());

			out.println("</td></tr></table>");
			FormattedOut.printBottom(request, out);
			connection.close();
		} catch (Exception exception)
		{
			try
			{

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
