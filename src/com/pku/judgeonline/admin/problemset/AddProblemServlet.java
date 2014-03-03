package com.pku.judgeonline.admin.problemset;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.*;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AddProblemServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Object prob_mute = new Object();

	public AddProblemServlet()
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
		String s = request.getParameter("title");
		if (s == null)
			s = "";
		int i;
		try
		{
			i = Integer.parseInt(request.getParameter("time_limit"));
		} catch (NumberFormatException numberformatexception)
		{
			i = 0;
		}
		int j;
		try
		{
			j = Integer.parseInt(request.getParameter("case_time_limit"));
		} catch (NumberFormatException numberformatexception1)
		{
			j = 0;
		}
		if (j == 0)
			j = i;
		int k;
		try
		{
			k = Integer.parseInt(request.getParameter("memory_limit"));
		} catch (NumberFormatException numberformatexception2)
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
		synchronized (prob_mute)
		{
			try
			{
				long l = ServerConfig.getNextProblemId();
				String s9 = Tool.fixPath(ServerConfig.getValue("DataFilesPath"));
				String s10 = (new StringBuilder()).append(s9).append(l).toString();
				String s11 = (new StringBuilder()).append(s9).append(l).toString();
				File file = new File(s10);
				if (!file.isDirectory())
					file.mkdirs();
				Connection connection = DBConfig.getConn();
				UserModel usermodel = UserModel.getCurrentUser(request);
				String user = usermodel.getUser_id();
				PreparedStatement preparedstatement = connection.prepareStatement("INSERT INTO problem (problem_id,title,description,input,output,input_path,output_path,sample_input,sample_output,hint,source,in_date,time_limit,memory_limit,case_time_limit,contest_id,Recommend)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				int i1 = 1;
				preparedstatement.setLong(i1++, l);
				preparedstatement.setString(i1++, s);
				preparedstatement.setString(i1++, s1);
				preparedstatement.setString(i1++, s2);
				preparedstatement.setString(i1++, s3);
				preparedstatement.setString(i1++, s10);
				preparedstatement.setString(i1++, s11);
				preparedstatement.setString(i1++, s5);
				preparedstatement.setString(i1++, s6);
				preparedstatement.setString(i1++, s8);
				preparedstatement.setString(i1++, s7);
				preparedstatement.setTimestamp(i1++, ServerConfig.getSystemTime());
				preparedstatement.setInt(i1++, i);
				preparedstatement.setInt(i1++, k);
				preparedstatement.setInt(i1++, j);
				preparedstatement.setString(i1++, s4);
				preparedstatement.setString(i1++, user);
				preparedstatement.executeUpdate();
				preparedstatement.close();
				if (s4 != null)
				{
					PreparedStatement preparedstatement1 = connection.prepareStatement("insert into contest_problem (contest_id,problem_id,title,num) values(?,?,?,?)");
					int j1 = 1;
					preparedstatement1.setString(j1++, s4);
					preparedstatement1.setLong(j1++, l);
					preparedstatement1.setString(j1++, s);
					preparedstatement1.setInt(j1++, 999);
					preparedstatement1.executeUpdate();
					preparedstatement1.close();
					preparedstatement1 = connection.prepareStatement("select problem_id from contest_problem where contest_id=? order by num");
					preparedstatement1.setString(1, s4);
					ResultSet resultset = preparedstatement1.executeQuery();
					j1 = 0;
					PreparedStatement preparedstatement2;
					for (; resultset.next(); preparedstatement2.close())
					{
						preparedstatement2 = connection.prepareStatement("update contest_problem set num=? where contest_id=? and problem_id=?");
						preparedstatement2.setInt(1, j1++);
						preparedstatement2.setString(2, s4);
						preparedstatement2.setLong(3, resultset.getLong("problem_id"));
						preparedstatement2.executeUpdate();
					}

					preparedstatement1.close();
				}
				connection.close();
				FormattedOut.printHead(out, "Congratulations");
				out.println("<p>");
				out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
				out.println("<font size=\"4\">Congratulations</font></p>");
				out.println("<ul>");
				out.println((new StringBuilder()).append("<li>You have added a problem, the id is <font color=\"#CC9900\">").append(l).append("</font></li>").toString());
				out.println((new StringBuilder()).append("<li>Data path:<font color=\"#CC9900\">").append(file.getAbsolutePath()).append("</font></li>").toString());
				out.println("</ul>");
				out.println("<p>");
				out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.probmanagerpage\">Add Another Problem</a>").toString());
				out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.showproblem?problem_id=").append(l).append("\">See This Problem</a>").toString());
				out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.upload?problem_id=").append(l).append("\">Add the data</a>").toString());
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
