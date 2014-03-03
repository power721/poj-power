package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.Tool;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class GotoProblem extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GotoProblem()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		long l = 0L;
		try
		{
			l = Integer.parseInt(request.getParameter("pid"));
		} catch (Exception exception)
		{
			String str = request.getParameter("pid");
			if (str != null && !str.trim().equals(""))
			{
				Tool.GoToURL((new StringBuilder()).append("searchproblem?sstr=").append(str).toString(), response);
				return;
			} else
			{
				l = 1000;
				try
				{
					Connection connection = DBConfig.getConn();
					PreparedStatement preparedstatement = connection.prepareStatement("SELECT problem_id FROM problem where UPPER(defunct) = 'N' ORDER BY RAND() LIMIT 1");
					ResultSet resultset = preparedstatement.executeQuery();
					resultset.next();
					l = resultset.getInt("problem_id");
					resultset.close();
					preparedstatement.close();
				} catch (Exception exception2)
				{
				}
			}
		}
		Tool.GoToURL((new StringBuilder()).append("showproblem?problem_id=").append(l).toString(), response);
	}

	public void destroy()
	{
	}
}
