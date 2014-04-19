package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MySqlPage extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Object prob_mute = new Object();

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter PrintWriter = paramHttpServletResponse.getWriter();
		if (!UserModel.isAdminLoginned(paramHttpServletRequest))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, paramHttpServletResponse);
			return;
		}
		if (!UserModel.isRoot(paramHttpServletRequest))
		{
			ErrorProcess.Error("You have no permission.", PrintWriter);
			return;
		}
		FormattedOut.printHead(PrintWriter, "Find Password");
		String user = paramHttpServletRequest.getParameter("user_id");
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
					ErrorProcess.Error("You have no  permission.", PrintWriter);
					Connection.close();
					preparedstatement.close();
					return;
				}
				preparedstatement = Connection.prepareStatement("select decode(password,'" + ServerConfig.ENCODE_STRING + "')as password from users where user_id=?");
				preparedstatement.setString(1, user);
				resultset = preparedstatement.executeQuery();
				if (!resultset.next())
				{
					ErrorProcess.Error("Can't find the user(" + user + ").", PrintWriter);
					Connection.close();
					preparedstatement.close();
					return;
				}
				password = Tool.htmlEncode(resultset.getString("password"));
				Connection.close();
				preparedstatement.close();
			}
			PrintWriter.println("<center><TABLE cellSpacing=0 cellPadding=0 width=35% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			PrintWriter.println("<tr><td><form method=get action=admin.mysql>User ID:<input type=text name=user_id size=20 " + (user != null ? "value=\"" + user + "\"" : "") + "></td><td><input type=Submit value=\"Find\">&nbsp;&nbsp;<input type=button value=Reset onclick=javascript:window.location.href=\"admin.mysql\"></form></td></tr>");
			PrintWriter.println("<tr><td>Password </td><td>" + (password == null ? "******" : password) + "</td></tr></table></center>");
		} catch (Exception Exception)
		{
			ErrorProcess.ExceptionHandle(Exception, PrintWriter);
		}
		FormattedOut.printBottom(PrintWriter);
	}

	public void destroy()
	{
	}
}
