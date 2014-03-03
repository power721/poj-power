package com.pku.judgeonline.servlet;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ShowCompileInfo extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ShowCompileInfo()
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
		String s;
		try
		{
			s = request.getParameter("solution_id");
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		out.println("<html><head><title>Compile Error</title></head><body>");
		Connection connection = null;
		PreparedStatement preparedstatement = null;
		ResultSet resultset = null;
		try
		{
			connection = DBConfig.getConn();
			String s1 = "SELECT * FROM compileinfo  WHERE solution_id = ?";
			preparedstatement = connection.prepareStatement(s1);
			preparedstatement.setString(1, s);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error("No such solution", out);
				resultset.close();
				preparedstatement.close();
				connection.close();
				return;
			}

			out.println("<ul><li><font color=#333399 size=5>Compile Error</font></li>");
			out.println("<p><font face=Times New Roman size=3>");
			out.println(Tool.dealCompileInfo(resultset.getString("error")));
			out.println("</font></p></ul>");
			preparedstatement.close();
			connection.close();
			out.println("</body></html>");
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
