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
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;

public class Roles extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public Roles()
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

		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!UserModel.isRoot(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		FormattedOut.printHead(out, "Roles List");
		String str1 = request.getParameter("op");
		if (str1 == null)
			str1 = "";
		String str2 = request.getParameter("user");
		if (str2 == null)
			str2 = "";
		Connection Connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		// synchronized (prob_mute)
		{
			try
			{
				if (str1.trim().equals("add"))
				{
					String str3 = request.getParameter("role");
					if (str3 == null)
					{
						ErrorProcess.Error("System Error.", out);
						Connection.close();
						return;
					}
					preparedstatement = Connection.prepareStatement("select user_id from users where user_id=?");
					preparedstatement.setString(1, str2);
					ResultSet resultset = preparedstatement.executeQuery();
					if (!resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") not existed", out);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("select * from privilege where user_id=? and rightstr=?");
					preparedstatement.setString(1, str2);
					preparedstatement.setString(2, str3);
					resultset = preparedstatement.executeQuery();
					if (resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") existed", out);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("insert into privilege (user_id,rightstr) values (?,?)");
					preparedstatement.setString(1, str2);
					preparedstatement.setString(2, str3);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					Tool.GoToURL("admin.roles", response);
				} else if (str1.trim().equals("del"))
				{
					String str3 = request.getParameter("role");
					if ("root".equalsIgnoreCase(str3))
					{
						str2 = "Cannot deleted!";
					}
					preparedstatement = Connection.prepareStatement("select * from privilege where user_id=? and rightstr=?");
					preparedstatement.setString(1, str2);
					preparedstatement.setString(2, str3);
					ResultSet resultset = preparedstatement.executeQuery();

					if (!resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") not existed", out);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("delete from privilege where user_id=? and rightstr=?");
					preparedstatement.setString(1, str2);
					preparedstatement.setString(2, str3);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					Tool.GoToURL("admin.roles", response);
				} else
				{
					// preparedstatement =
					// Connection.prepareStatement("SELECT COUNT(*) AS total FROM privilege"
					// + (UserModel.isRoot(request) ? " order by rightstr" :
					// " WHERE rightstr='source_browser'"));
				}
				/*
				 * ResultSet resultset = preparedstatement.executeQuery();
				 * resultset.first(); resultset.close();
				 * preparedstatement.close();
				 */
				preparedstatement = Connection.prepareStatement("SELECT * FROM privilege" + (UserModel.isRoot(request) ? " order by rightstr" : " WHERE rightstr='source_browser'"));
				ResultSet resultset = preparedstatement.executeQuery();
				out.println("<center><img src='./images/user.gif' alt='user'></center><br>");
				out.println("<p align=center><font size=5 color=#333399>Roles List</font></p>");
				out.println("<form method=get action=admin.roles><center><input type=hidden name=op value=add><input type=text name=user size=20>");
				out.println("<select name=role size=1>");
				out.println("<option selected=\"selected\" value=source_browser>Source Browser</option>");
				out.println("<option value=member>Member</option>");
				out.println("<option value=Administrator>Administrator</option>");
				out.println("</select>");
				out.println("<input type=Submit value=Add></form></center>");
				out.println("<TABLE cellSpacing=0 cellPadding=0 width=99% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
				out.println("<tr align=center bgcolor=#6589D1>");
				out.println("<td width=\"5%\" ><b>No.</b></td>");
				out.println("<td width=\"10%\" ><b>User ID</b></td>");
				out.println("<td width=\"55%\" ><b>Rightstr</b></td>");
				out.println("<td width=\"55%\" ><b>Operator</b></td>");
				out.println("</tr>");
				int i = 1;
				for (; resultset.next(); out.println("</tr>"))
				{
					out.println("<tr align=center>");
					long l2 = i++;
					String str3 = resultset.getString("user_id");
					String str4 = resultset.getString("rightstr");
					out.println("<td width=\"5%\" align=\"center\">" + l2 + "</td>");
					out.println("<td><a href=\"userstatus?user_id=" + str3 + "\">" + str3 + "</a></td>");
					out.println("<td><font color=green>" + Tool.titleEncode(str4) + "</font></td>");
					boolean flag4 = str4.equalsIgnoreCase("source_browser") || str4.equalsIgnoreCase("title") || (!str4.equalsIgnoreCase("root") && UserModel.isRoot(request));
					out.println("<td>" + (flag4 ? "<a href=javascript:roledel(\"" + str3 + "\",\"" + str4 + "\")>" : "") + "Del" + (flag4 ? "</a>" : "") + "</td>");
				}

				out.println("</table>");
				out.println("<p align=\"center\">");
				out.println("</p>");
				Connection.close();
				preparedstatement.close();
			} catch (Exception Exception)
			{
				ErrorProcess.ExceptionHandle(Exception, out);
			}
			FormattedOut.printBottom(out);
		}
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
