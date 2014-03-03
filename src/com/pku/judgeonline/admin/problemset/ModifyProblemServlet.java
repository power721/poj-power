package com.pku.judgeonline.admin.problemset;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ModifyProblemServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyProblemServlet()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		long l;
		try
		{
			l = Integer.parseInt(request.getParameter("problem_id"));
		} catch (NumberFormatException numberformatexception)
		{
			l = -1L;
		}
		String s = request.getParameter("title");
		if (s == null)
			s = "";
		int i;
		try
		{
			i = Integer.parseInt(request.getParameter("time_limit"));
		} catch (NumberFormatException numberformatexception1)
		{
			i = 0;
		}
		int j;
		try
		{
			j = Integer.parseInt(request.getParameter("case_time_limit"));
		} catch (NumberFormatException numberformatexception2)
		{
			j = 0;
		}
		int k;
		try
		{
			k = Integer.parseInt(request.getParameter("memory_limit"));
		} catch (NumberFormatException numberformatexception3)
		{
			k = 0;
		}
		String s1 = request.getParameter("description");
		if (s1 == null)
			s1 = "";
		String s2 = request.getParameter("input");
		if (s2 == null)
			s2 = "";
		String s3 = request.getParameter("output");
		if (s3 == null)
			s3 = "";
		String s4 = request.getParameter("contest_id");
		if (s4 != null && s4.trim().equals(""))
			s4 = null;
		String s5 = request.getParameter("sample_input");
		if (s5 == null)
			s5 = "";
		String s6 = request.getParameter("sample_output");
		if (s6 == null)
			s6 = "";
		String s7 = request.getParameter("source");
		if (s7 == null)
			s7 = "";
		String s8 = request.getParameter("hint");
		if (s8 == null)
			s8 = "";
		String s99 = request.getParameter("hide");
		if (s99 == null)
			s99 = "N";
		String s9 = null;
		synchronized (AddProblemServlet.prob_mute)
		{
			try
			{
				Connection connection = DBConfig.getConn();
				PreparedStatement preparedstatement = connection.prepareStatement("select contest_id from problem where problem_id=?");
				preparedstatement.setLong(1, l);
				ResultSet resultset = preparedstatement.executeQuery();
				if (resultset.next())
					s9 = resultset.getString("contest_id");
				preparedstatement.close();
				preparedstatement = connection.prepareStatement("UPDATE problem SET title=?,description=?,input=?,output=?,sample_input=?,sample_output=?,hint=?,source=?,time_limit=?,memory_limit=?,case_time_limit=?,contest_id=?,defunct=? WHERE problem_id=?");
				int i1 = 1;
				preparedstatement.setString(i1++, s);
				preparedstatement.setString(i1++, s1);
				preparedstatement.setString(i1++, s2);
				preparedstatement.setString(i1++, s3);
				preparedstatement.setString(i1++, s5);
				preparedstatement.setString(i1++, s6);
				preparedstatement.setString(i1++, s8);
				preparedstatement.setString(i1++, s7);
				preparedstatement.setInt(i1++, i);
				preparedstatement.setInt(i1++, k);
				preparedstatement.setInt(i1++, j);
				preparedstatement.setString(i1++, s4);
				preparedstatement.setString(i1++, s99);
				preparedstatement.setLong(i1++, l);
				preparedstatement.executeUpdate();
				preparedstatement.close();
				boolean flag = true;
				if (s9 != null && !s9.equals(s4))
				{
					PreparedStatement preparedstatement1 = connection.prepareStatement("select contest_id,start_time from contest where contest_id=? and UPPER(defunct)='N'");
					preparedstatement1.setString(1, s9);
					ResultSet resultset1 = preparedstatement1.executeQuery();
					if (resultset1.next())
					{
						Timestamp timestamp = resultset1.getTimestamp("start_time");
						flag = timestamp.getTime() < System.currentTimeMillis();
					}
					preparedstatement1.close();
					if (!flag)
					{
						PreparedStatement preparedstatement4 = connection.prepareStatement("delete from contest_problem where contest_id=? and problem_id=?");
						preparedstatement4.setString(1, s9);
						preparedstatement4.setLong(2, l);
						preparedstatement4.executeUpdate();
						preparedstatement4 = connection.prepareStatement("select problem_id from contest_problem where contest_id=? order by num");
						preparedstatement4.setString(1, s9);
						ResultSet resultset3 = preparedstatement4.executeQuery();
						int k1 = 0;
						PreparedStatement preparedstatement5;
						for (; resultset3.next(); preparedstatement5.close())
						{
							preparedstatement5 = connection.prepareStatement("update contest_problem set num=? where contest_id=? and problem_id=?");
							preparedstatement5.setInt(1, k1++);
							preparedstatement5.setString(2, s9);
							preparedstatement5.setLong(3, resultset3.getLong("problem_id"));
							preparedstatement5.executeUpdate();
						}

						preparedstatement4.close();
					}
				}
				if (s4 != null)
				{
					PreparedStatement preparedstatement2 = connection.prepareStatement("update contest_problem set title=? where problem_id=? and contest_id=?");
					preparedstatement2.setString(1, s);
					preparedstatement2.setLong(2, l);
					preparedstatement2.setString(3, s4);
					preparedstatement2.executeUpdate();
					preparedstatement2.close();
					if (s9 == null || !s4.equals(s9))
					{
						PreparedStatement preparedstatement3 = connection.prepareStatement("select contest_id,start_time from contest where contest_id=? and UPPER(defunct)='N'");
						preparedstatement3.setString(1, s4);
						ResultSet resultset2 = preparedstatement3.executeQuery();
						if (resultset2.next())
						{
							Timestamp timestamp1 = resultset2.getTimestamp("start_time");
							flag = timestamp1.getTime() < System.currentTimeMillis();
						}
						preparedstatement3.close();
						preparedstatement3 = connection.prepareStatement("select * from contest_problem where contest_id=? and problem_id=?");
						preparedstatement3.setString(1, s4);
						preparedstatement3.setLong(2, l);
						resultset2 = preparedstatement3.executeQuery();
						if (!resultset2.next() && !flag)
						{
							preparedstatement3.close();
							preparedstatement3 = connection.prepareStatement("insert into contest_problem (contest_id,problem_id,title,num) values(?,?,?,?)");
							int j1 = 1;
							preparedstatement3.setString(j1++, s4);
							preparedstatement3.setLong(j1++, l);
							preparedstatement3.setString(j1++, s);
							preparedstatement3.setInt(j1++, 999);
							preparedstatement3.executeUpdate();
							preparedstatement3.close();
							preparedstatement3 = connection.prepareStatement("select problem_id from contest_problem where contest_id=? order by num");
							preparedstatement3.setString(1, s4);
							ResultSet resultset4 = preparedstatement3.executeQuery();
							j1 = 0;
							PreparedStatement preparedstatement6;
							for (; resultset4.next(); preparedstatement6.close())
							{
								preparedstatement6 = connection.prepareStatement("update contest_problem set num=? where contest_id=? and problem_id=?");
								preparedstatement6.setInt(1, j1++);
								preparedstatement6.setString(2, s4);
								preparedstatement6.setLong(3, resultset4.getLong("problem_id"));
								preparedstatement6.executeUpdate();
							}

						}
						preparedstatement3.close();
					}
				}
				connection.close();
				FormattedOut.printHead(out, "Congratulations");
				out.println("<p>");
				out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
				out.println("<font size=\"4\">Congratulations</font></p>");
				out.println("<ul>");
				out.println((new StringBuilder()).append("<li>You have modified a problem, the id is <font color=\"#CC9900\">").append(l).append("</font></li>").toString());
				out.println("</ul>");
				out.println("<p>");
				out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=admin.problemlist>Problem List</a>").toString());
				out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"showproblem?problem_id=").append(l).append("\">See This Problem</a>").toString());
				out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.upload?problem_id=").append(l).append("\">Modify Data File</a>").toString());
				FormattedOut.printBottom(out);
			} catch (Exception exception)
			{
				ErrorProcess.ExceptionHandle(exception, out);
			}
		}
	}

	public void destroy()
	{
	}
}
