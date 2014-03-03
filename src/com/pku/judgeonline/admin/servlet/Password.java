package com.pku.judgeonline.admin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;

public class Password extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public Password()
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

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		if (!UserModel.isRoot(request))
		{
			ErrorProcess.Error("You have no permission.", out);
			return;
		}
		FormattedOut.printHead(out, "Find Password");
		String user = request.getParameter("user_id");
		String password = null;
		Connection Connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			if (user != null)
			{
				preparedstatement = Connection.prepareStatement("select * from privilege where user_id=? and rightstr='root'");
				preparedstatement.setString(1, user);
				resultset = preparedstatement.executeQuery();
				if (resultset.next())
				{
					ErrorProcess.Error("You have no  permission.", out);
					Connection.close();
					preparedstatement.close();
					return;
				}
				preparedstatement = Connection.prepareStatement("select decode(password,'" + ServerConfig.ENCODE_STRING + "')as password from users where user_id=?");
				preparedstatement.setString(1, user);
				resultset = preparedstatement.executeQuery();
				if (!resultset.next())
				{
					ErrorProcess.Error("Can't find the user(" + user + ").", out);
					Connection.close();
					preparedstatement.close();
					return;
				}
				password = resultset.getString("password");
				Connection.close();
				preparedstatement.close();
			}
			out.println("<center><TABLE cellSpacing=0 cellPadding=0 width=35% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.println("<tr><td><form method=get action=admin.mysql>User ID:<input type=text name=user_id size=20 " + (user != null ? "value=" + user : "") + "></td><td><input type=Submit value=\"Find\">&nbsp;&nbsp;<input type=button value=Reset onclick=javascript:window.location.href=\"admin.mysql\"></form></td></tr>");
			out.println("<tr><td>Password </td><td>" + (password == null ? "******" : password) + "</td></tr></table></center>");
		} catch (Exception Exception)
		{
			ErrorProcess.ExceptionHandle(Exception, out);
		}
		FormattedOut.printBottom(out);
		out.flush();
		out.close();
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
