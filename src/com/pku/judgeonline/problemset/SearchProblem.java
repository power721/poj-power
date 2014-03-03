package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class SearchProblem extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SearchProblem()
	{
	}

	public void init() throws ServletException
	{
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		String s;
		int i = 0;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		s = request.getParameter("sstr");
		String s1 = request.getParameter("manner");

		if (!(s == null || s.trim().equals("") || s1 == null || s1.trim().equals("")))
			i = Integer.parseInt(s1);
		if (!Guard.Guarder(request, response, out))
			return;
		try
		{
			Connection connection;
			connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, (new StringBuilder()).append("Search Problem ").append(s).toString());
			if (s == null || s.trim().equals(""))
			{
				connection.close();
				connection = null;
				FormattedOut.printBottom(request, out);
				return;
			}
			PreparedStatement preparedstatement;
			ResultSet resultset;
			PreparedStatement preparedstatement3;
			ResultSet resultset3;
			int j;
			preparedstatement = connection.prepareStatement("SELECT max(problem_id) as total FROM solution");
			resultset = preparedstatement.executeQuery();
			resultset.next();
			j = resultset.getInt("total") + 10;
			resultset.close();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("SELECT max(problem_id) as total FROM problem");
			resultset = preparedstatement.executeQuery();
			resultset.next();
			int k = resultset.getInt("total") + 10;
			resultset.close();
			preparedstatement.close();
			if (k > j)
				j = k;
			if (i == 0)
				preparedstatement = connection.prepareStatement("select problem_id,title,source from problem WHERE (title like ? or source like ? ) and contest_id is null and UPPER(defunct) = 'N' ORDER BY problem_id,title,source");
			else if (i == 1)
				preparedstatement = connection.prepareStatement("select problem_id,title,source from problem WHERE title like ?  and contest_id is null and UPPER(defunct) = 'N' ORDER BY problem_id,title,source");
			else if (i == 2)
				preparedstatement = connection.prepareStatement("select problem_id,title,source from problem  WHERE  source like ?  and contest_id is null and UPPER(defunct) = 'N' ORDER BY source,problem_id");
			else if (i == 3)
			{
				preparedstatement = connection.prepareStatement("select problem_id from tag  WHERE  tag like ? ");
			}

			preparedstatement.setString(1, (i == 3 ? "%" : "%") + s + (i == 3 ? "%" : "%"));
			if (i == 0)
				preparedstatement.setString(2, (new StringBuilder()).append("%").append(s).append("%").toString());
			resultset = preparedstatement.executeQuery();
			/*if (!resultset.next())
			{
				out.println("<div id=search class=error align=center>Sorry,the Problem doesn't exist.</div>");
				FormattedOut.printBottom(request, out);
				preparedstatement.close();
				connection.close();
				return;
			}*/

			try
			{
				int l = 0;
				char ac[] = new char[j];
				for (int i1 = 0; i1 < j; i1++)
					ac[i1] = '\0';

				UserModel usermodel = UserModel.getCurrentUser(request);
				String s2 = null;
				if (usermodel != null)
					s2 = usermodel.getUser_id();
				if (s2 != null)
				{
					PreparedStatement preparedstatement1 = connection.prepareStatement("select problem_id,min(result) as res from solution where user_id=? group by problem_id");
					preparedstatement1.setString(1, s2);
					for (ResultSet resultset1 = preparedstatement1.executeQuery(); resultset1.next();)
					{
						int k1 = resultset1.getInt("problem_id");
						int l1 = resultset1.getInt("res");
						if (l1 == 0)
							ac[k1] = '\001';
						else
							ac[k1] = '\002';
					}

					preparedstatement1.close();
				}
				out.println("<center>");
				out.println("<font size=5 color=blue>Search Result</font><br>");
				out.println("</center>");
				out.println("<img src='./images/search.gif' alt='search'><font color=blue size=5><b>Search:</b></font>");
				out.println((new StringBuilder()).append("<form method=get action=searchproblem><input type=text name=sstr size=25 value='").append(s).append("'><select size=1 name=manner><option value=0 " + (i == 0 ? " selected" : "") + ">All</option><option value=1 " + (i == 1 ? " selected" : "") + ">Title</option><option value=2 " + (i == 2 ? " selected" : "") + ">Source</option><option value=3 " + (i == 3 ? " selected" : "") + ">Tag</option></select><input type=submit value=Search></p>").toString());
				out.println("</form>");
				out.println("<center>");
				out.println("<TABLE cellSpacing=0 cellPadding=0 width=99% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
				out.println("<tr bgcolor=#6589D1><td width=5%></td><td width=10%>Problem Id</td><td width=25%>Title</td>");
				out.println("<td>Source</td></tr>");
				while (resultset.next())
				{

					preparedstatement3 = connection.prepareStatement("select problem_id,title,source from problem WHERE problem_id=?  and contest_id is null and UPPER(defunct) = 'N'");
					preparedstatement3.setInt(1, resultset.getInt("problem_id"));
					resultset3 = preparedstatement3.executeQuery();
					if (resultset3.next())
					{
						l++;
						out.println("<tr>");
						out.println("<td align=center>");
						int j1 = resultset.getInt("problem_id");
						char c = ac[j1];
						if (c == 0)
							out.print("&nbsp;");
						else if (c == '\001')
							out.print("<img border=0 src=images/accepted.gif>");
						else
							out.print("<img border=0 src=images/wrong.gif>");
						out.println("</td>");
						out.println((new StringBuilder()).append("<td>").append(j1).append("</td>").toString());
						out.println((new StringBuilder()).append("<td><a href=showproblem?problem_id=").append(j1).append(">").append(i == 3 ? resultset3.getString("title") : resultset.getString("title")).append("</a></td>").toString());
						out.println((new StringBuilder()).append("<td>").append(i == 3 ? resultset3.getString("source") : resultset.getString("source")).append("</td></tr>").toString());
					}
				}
				out.println("</table>");
				out.println("</center>");
				out.println((new StringBuilder()).append("<font color=blue size=5>Total ").append(l).append(" Problems match!</font>").toString());
				preparedstatement.close();
				connection.close();
			} catch (SQLException sqlexception1)
			{
				ErrorProcess.ExceptionHandle(sqlexception1, out);
			}
		} catch (SQLException sqlexception)
		{
			ErrorProcess.ExceptionHandle(sqlexception, out);
		}
		FormattedOut.printBottom(request, out);
		return;
	}

	public void destroy()
	{
	}
}
