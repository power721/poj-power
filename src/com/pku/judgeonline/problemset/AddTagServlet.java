package com.pku.judgeonline.problemset;

import com.pku.judgeonline.admin.servlet.*;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AddTagServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddTagServlet()
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
		if (!UserModel.isAdminLoginned(request) && !UserModel.isSourceBrowser(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		String s = request.getParameter("problem_id");
		if (s == null || s == "")
		{
			ErrorProcess.Error("The problem_id cann't be empty.", out);
			return;
			// s = "";
		}
		String s1 = request.getParameter("tag");
		if (s1 == null || s1 == "")
		{
			ErrorProcess.Error("The tag cann't be empty.", out);
			return;
			// s1 = "";
		}

		String s2 = UserModel.getCurrentUser(request).getUser_id();
		long problem_id = 0l;
		try
		{
			problem_id = Integer.parseInt(s);
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}

		try
		{

			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement;
			ResultSet resultset;

			preparedstatement = connection.prepareStatement("SELECT * FROM tag  where problem_id=? and tag like ?");
			preparedstatement.setLong(1, problem_id);
			preparedstatement.setString(2, s1);
			resultset = preparedstatement.executeQuery();
			if (resultset.next())
			{
				ErrorProcess.Error("The tag existed", out);
				preparedstatement.close();
				connection.close();
				return;
			}
			preparedstatement = connection.prepareStatement("INSERT INTO tag (problem_id,user_id,tag) VALUES (?,?,?)");
			preparedstatement.setLong(1, problem_id);
			preparedstatement.setString(2, s2);
			preparedstatement.setString(3, s1);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			connection.close();
			Tool.GoToURL("showproblem?problem_id=" + problem_id + "#tag", response);
			/*
			 * FormattedOut.printHead(out, "Congratulations");
			 * out.println("<p>"); out.println((new
			 * StringBuilder
			 * ()).append("<img border=\"0\" src=\"").append(ServerConfig
			 * .getValue("RootPathOJ")).append(
			 * "images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
			 * out
			 * .println("<font size=\"4\">Congratulations</font></p>");
			 * out.println("<ul>"); out.println((new
			 * StringBuilder()).append(
			 * "<li>You have added a tag, the problem_id is <font color=\"#CC9900\">"
			 * ).append(problem_id).append("</font></li>").toString());
			 * 
			 * out.println("</ul>"); out.println("<p>");
			 * out.println((new
			 * StringBuilder()).append("<img border=\"0\" src=\""
			 * ).append(ServerConfig.getValue("RootPathOJ")).append(
			 * "images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"addtagpage\">Add Another Tag</a>"
			 * ).toString()); out.println((new
			 * StringBuilder()).append("<img border=\"0\" src=\""
			 * ).append(ServerConfig.getValue("RootPathOJ")).append(
			 * "images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"showproblem?problem_id="
			 * )
			 * .append(problem_id).append("\">See This Problem</a>").toString())
			 * ; FormattedOut.printBottom(out);
			 */
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
	}

	public void destroy()
	{
	}
}
