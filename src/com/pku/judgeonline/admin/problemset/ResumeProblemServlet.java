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

public class ResumeProblemServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResumeProblemServlet()
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
			i = 0;
		}
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("UPDATE problem SET defunct = 'N' WHERE problem_id=?");
			preparedstatement.setInt(1, i);
			preparedstatement.executeUpdate();
			preparedstatement = connection.prepareStatement("select user_id from solution WHERE problem_id=? and result=0 group by user_id");
			preparedstatement.setInt(1, i);
			ResultSet resultset = preparedstatement.executeQuery();
			for (; resultset.next();)
			{
				PreparedStatement preparedstatement1 = connection.prepareStatement("UPDATE users SET solved=solved+1 WHERE user_id=?");
				preparedstatement1.setString(1, resultset.getString("user_id"));
				preparedstatement1.executeUpdate();
				preparedstatement1.close();
			}
			preparedstatement.close();

			FormattedOut.printHead(out, "Congratulations");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
			out.println("<font size=\"4\">Congratulations</font></p>");
			out.println("<ul>");
			out.println((new StringBuilder()).append("<li>You have resumed a problem, the id is <font color=\"#CC9900\">").append(i).append("</font></li>").toString());
			out.println("</ul>");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.showproblem?problem_id=").append(i).append("\">See This Problem</a>").toString());
			FormattedOut.printBottom(out);
			connection.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
	}

	public void destroy()
	{
	}
}
