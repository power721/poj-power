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

public class ShowProblemServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowProblemServlet()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		int i;
		try
		{
			i = Integer.parseInt(request.getParameter("problem_id"));
		} catch (NumberFormatException numberformatexception)
		{
			ErrorProcess.ExceptionHandle(numberformatexception, out);
			return;
		}
		Connection connection;
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			connection = DBConfig.getConn();
			preparedstatement = connection.prepareStatement("SELECT * FROM problem WHERE problem_id = ?");
			preparedstatement.setInt(1, i);
			resultset = preparedstatement.executeQuery();
			if (!resultset.first())
			{
				ErrorProcess.Error((new StringBuilder()).append("Can not find problem (ID:").append(i).append(")").toString(), out);
				preparedstatement.close();
				connection.close();
				return;
			}
			try
			{
				PreparedStatement preparedstatement1 = connection.prepareStatement("SELECT COUNT(*) AS total FROM solution WHERE problem_id = ? AND result=?");
				preparedstatement1.setInt(1, i);
				preparedstatement1.setInt(2, 0);
				ResultSet resultset1 = preparedstatement1.executeQuery();
				resultset1.first();
				int j = resultset1.getInt("total");
				resultset1.close();
				preparedstatement1.close();
				preparedstatement1 = connection.prepareStatement("SELECT COUNT(*) AS total FROM solution WHERE problem_id = ?");
				preparedstatement1.setInt(1, i);
				resultset1 = preparedstatement1.executeQuery();
				resultset1.first();
				int k = resultset1.getInt("total");
				resultset1.close();
				preparedstatement1.close();
				FormattedOut.printHead(out, (new StringBuilder()).append("Problem -- ").append(resultset.getString("title")).toString());
				out.println((new StringBuilder()).append("<p align=\"center\"><font size=\"4\"><a href=showproblem?problem_id=" + i + ">").append(resultset.getString("title")).append("</a></font></p>").toString());
				out.println((new StringBuilder()).append("<p align=\"center\">Time Limit:").append(resultset.getInt("time_limit") / 1000).append("S&nbsp; Memory Limit:").append(resultset.getInt("memory_limit")).append("K<br>").toString());
				out.println((new StringBuilder()).append("Total Submit:").append(k).append(" Accepted:").append(j).append("</p>").toString());
				out.println("<p align=\"left\"><b><font color=\"#333399\" size=\"5\">Description</font>");
				out.println("</b>");
				out.println("<p><font face=\"Times New Roman\" size=\"3\">");
				out.print(Tool.gethtmlFormattedString(resultset.getString("description")));
				out.println("</font></p>");
				out.println("<p align=\"left\"><b><font color=\"#333399\" size=\"5\">Input</font>");
				out.println("</b>");
				out.println("<p><font face=\"Times New Roman\" size=\"3\">");
				out.print(Tool.gethtmlFormattedString(resultset.getString("input")));
				out.println("</font></p>");
				out.println("<p align=\"left\"><b><font color=\"#333399\" size=\"5\">Output </font>");
				out.println("</b>");
				out.println("<p><font face=\"Times New Roman\" size=\"3\">");
				out.print(Tool.gethtmlFormattedString(resultset.getString("output")));
				out.println("</font></p>");
				out.println("<p align=\"left\"><b><font color=\"#333399\" size=\"5\">Sample Input</font>");
				out.println("</b>");
				out.println("<p><font face=\"Times New Roman\" size=\"3\"><pre>");
				out.print(resultset.getString("sample_input"));
				out.println("</pre></font></p>");
				out.println("<p align=\"left\"><b><font color=\"#333399\" size=\"5\">Sample Output</font>");
				out.println("</b>");
				out.println("<p><font face=\"Times New Roman\" size=\"3\"><pre>");
				out.print(resultset.getString("sample_output"));
				out.println("</pre></font></p>");
				out.println("<p align=\"left\"><b><font color=\"#333399\" size=\"5\">Hint </font>");
				out.println("</b>");
				out.println("<p><font face=\"Times New Roman\" size=\"3\">");
				out.print(Tool.gethtmlFormattedString(resultset.getString("hint")));
				out.println("</font></p>");
				out.println("<p align=\"left\"><b><font color=\"#333399\" size=\"5\">Source </font>");
				out.println("</b>");
				out.println("<p>");
				String s = Tool.gethtmlFormattedString(resultset.getString("source"));
				out.println((new StringBuilder()).append("<a href=\"searchproblem?sstr=").append(s).append("&manner=2\">").append(s).append("</a>").toString());
				out.println("<p>");
				//out.println("<font color=\"#333399\" size=\"5\">");
				//out.println("</font>");
				out.println("<font color=\"#333399\" size=\"3\"><p align=\"center\">");
				String s1 = resultset.getString("defunct");
				if (s1 != null && s1.equals("N"))
					out.println((new StringBuilder()).append("[<a href=\"admin.deleteproblem?problem_id=").append(i).append("\">Delete</a>]&nbsp;&nbsp;").toString());
				else
					out.println((new StringBuilder()).append("[<a href=\"admin.resumeproblem?problem_id=").append(i).append("\">Resume</a>]&nbsp;&nbsp;").toString());
				out.println((new StringBuilder()).append(" [<a href=\"admin.probmanagerpage?problem_id=").append(i).append("\">Edit</a>]&nbsp;&nbsp;").toString());
				out.println("</font></p>");
				FormattedOut.printBottom(out);
				preparedstatement.close();
				connection.close();
			} catch (Exception exception1)
			{
				ErrorProcess.ExceptionHandle(exception1, out);
			}
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
		}
		return;
	}

	public void destroy()
	{
	}
}
