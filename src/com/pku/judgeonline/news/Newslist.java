package com.pku.judgeonline.news;

import com.pku.judgeonline.common.DBConfig;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Newslist extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Newslist()
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
		int i = 0;
		int j = 20;
		try
		{
			i = Integer.parseInt(request.getParameter("start"));
		} catch (Exception exception)
		{
			i = 0;
		}
		if (i < 0)
			i = 0;
		try
		{
			j = Integer.parseInt(request.getParameter("size"));
		} catch (Exception exception1)
		{
			j = 20;
		}
		if (j < 0)
			j = 20;
		if (j > 100)
			j = 100;
		Connection connection = DBConfig.getConn();
		try
		{
			PreparedStatement preparedstatement = connection.prepareStatement("select * from news order by importance desc,news_id desc limit ?,?");
			preparedstatement.setInt(1, i);
			preparedstatement.setInt(2, j);
			preparedstatement.close();
			connection.close();
			connection = null;
		} catch (Exception exception2)
		{
			try
			{
				if (connection != null)
					connection.close();
				connection = null;
			} catch (Exception exception3)
			{
			}
			exception2.printStackTrace(System.err);
		}
		out.close();
	}

	public void destroy()
	{
	}
}
