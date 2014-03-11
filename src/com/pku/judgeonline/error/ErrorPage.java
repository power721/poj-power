package com.pku.judgeonline.error;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;

public class ErrorPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public ErrorPage()
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
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Connection connection = DBConfig.getConn();
		String code = request.getParameter("code");
		String title = "Error! Page not found.";
		FormattedOut.printHead(out, request, connection, title);
		out.println("<br><br><br><br>\n");
		if("404".equals(code))
		{
			out.println("<div id=error-page align=center><br>\n");
			out.println(" <div class=\"source\" style=\"BACKGROUND-COLOR: #000000; FONT-FAMILY: '[object HTMLOptionElement]', 'Consolas', 'Lucida Console', 'Courier New'; COLOR: #c0c0c0\">\n");
			out.println(" <SPAN style=\"COLOR: #ffffff\">&nbsp;#include&lt;stdio.h&gt;</SPAN><BR><BR>\n");
			out.println(" <SPAN style=\"COLOR: #ffffff\">&nbsp;int</SPAN> <a href=\".\"><SPAN style=\"COLOR: #c0c0c0\">main</SPAN></a>()<BR>\n");
			out.println(" <SPAN style=\"COLOR: #c0c0c0\">&nbsp;{</SPAN><BR>&nbsp;&nbsp;&nbsp; \n");
			out.println(" <SPAN style=\"COLOR: #c0c0c0\">&nbsp;printf</SPAN>\n");
			out.println(" (<SPAN style=\"COLOR: #00bb00\">&quot;Sorry!\\n&quot;</SPAN>);<BR>&nbsp;&nbsp;&nbsp;\n");
			out.println(" <SPAN style=\"COLOR: #c0c0c0\">&nbsp;printf</SPAN>\n");
			out.println(" (<SPAN style=\"COLOR: #00bb00\">&quot;We search 404 pages, \\n&quot;</SPAN>);<BR>&nbsp;&nbsp;&nbsp; \n");
			out.println(" <SPAN style=\"COLOR: #c0c0c0\">&nbsp;printf</SPAN>\n");
			out.println(" (<SPAN style=\"COLOR: #00bb00\">&quot;but do not find what you want!\\n&quot;</SPAN>);<BR>&nbsp;&nbsp;&nbsp; \n");
			out.println(" &nbsp;<a href=\"javascript:history.go(-1)\"><SPAN style=\"COLOR: #00ff00; FONT-WEIGHT: bold\">return</SPAN>\n");
			out.println(" <SPAN style=\"COLOR: #c0c0c0\">1</SPAN></a>;<BR>\n");
			out.println(" <SPAN style=\"COLOR: #c0c0c0\">&nbsp;}</SPAN><BR>\n");
			out.println("</div><br><br>\n");
			out.println("</div>\n");
		}
		else if("500".equals(code))
		{
			
		}
		FormattedOut.printBottom(request, out);
		out.flush();
		out.close();
		try
		{
			connection.close();
		} catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			ErrorProcess.ExceptionHandle(e, out);
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
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
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException
	{
		// Put your code here
	}

}
