package com.pku.judgeonline.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.common.DBConfig;

public class Ajax extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public Ajax()
	{
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy()
	{
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		// response.setContentType("application/x-javascript;charset=UTF-8");
		response.setContentType("text/plain; charset=UTF-8");
		// response.setHeader("charset", "UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String name = request.getParameter("name");
		String val = request.getParameter("val");
		String json = "";
		Connection connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			if ("pid".equals(name))
			{
				long pid = 0L;
				String cid = request.getParameter("cid");
				if(cid != null && !cid.equals(""))
				{
					char c = val.charAt(0);
					if (c >= 'A' && c <= 'Z')
					{
						pid  = c - 'A';
					} else
						pid  = Integer.parseInt(val);
					preparedstatement = connection.prepareStatement("select problem_id from contest_problem where contest_id=? and num=?");
					preparedstatement.setString(1, cid);
					preparedstatement.setLong(2, pid);
					resultset = preparedstatement.executeQuery();
					if (resultset.next())
					{
						pid = resultset.getInt("problem_id");
					}
				}
				else
					pid = Long.parseLong(val);
				preparedstatement = connection.prepareStatement("select title from problem where problem_id=?");
				preparedstatement.setLong(1, pid);
				resultset = preparedstatement.executeQuery();
				if (resultset.next())
				{
					json = (new StringBuilder()).append("<a href=showproblem?problem_id=").append(val).append(">").append(resultset.getString("title")).append("</a>").toString();
				} else
				{
					json = "<font color=\"red\">No such problem.</font>";
				}
			} else if ("ptitle".equals(name))
			{
				preparedstatement = connection.prepareStatement("select title from problem where problem_id=?");
				preparedstatement.setLong(1, Long.parseLong(val));
				resultset = preparedstatement.executeQuery();
				if (resultset.next())
					json = resultset.getString("title");
			}
		} catch (Exception exception)
		{
			json = "No such problem.";
		}
		out.println(json);
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException
	{
		// Put your code here
	}

}
