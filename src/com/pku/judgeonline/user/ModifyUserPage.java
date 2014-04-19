package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ModifyUserPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyUserPage()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!UserModel.isLoginned(request))
		{
			Tool.forwardToUrl(request, response, "loginpage?url=modifyuserpage");
			return;
		}
		String s = UserModel.getCurrentUser(request).getUser_id();
		ResultSet resultset;
		Connection connection = DBConfig.getConn();
		try
		{
			PreparedStatement preparedstatement = connection.prepareStatement("select * from users where user_id=?");
			preparedstatement.setString(1, s);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				preparedstatement.close();
				connection.close();
				connection = null;
				ErrorProcess.Error("user not exists.", out);
				return;
			}

				String s1 = resultset.getString("nick");
				String s2 = resultset.getString("email");
				String s3 = resultset.getString("school");
				int qq = resultset.getInt("qq");
				FormattedOut.printHead(out, request, connection, "Modify Register Information");
				out.println("<form action=\"modifyuser\" onsubmit=\"return modifyValidate()\" method=\"post\">");
				out.println("<table align=center cellSpacing=3 cellPadding=3 width=600 border=0 background=\"images/table_back.jpg\">");
				out.println("<tr><td colspan=2 width=600 height=40>");
				out.println("<p align=center>Modify Register Information</td>");
				out.println("</tr>");
				out.println((new StringBuilder()).append("<input type=\"hidden\" name=\"user_id\" id=\"user_id\" size=20 value=\"").append(s).append("\" required>").toString());
				out.println("<tr><td>Nick Name:</td>");
				out.println((new StringBuilder()).append("<td><input type=text name=\"nick\" id=\"nick\" size=50 value=\"").append(Tool.titleEncode(s1)).append("\" required></td></tr>").toString());
				out.println("<tr><td>Old Password:</td>");
				out.println("<td><input type=password name=\"oldPassword\" id=\"password\" size=20 required></td></tr>");
				out.println("<tr><td>New Password:</td>");
				out.println("<td><input type=password name=\"newPassword\" id=\"newPassword\" size=20></td>");
				out.println("</tr><tr><td>Repeat Password:</td>");
				out.println("<td><input type=password name=\"rptPassword\" id=\"rptPassword\" size=20></td></tr>");
				out.println("<tr><td>School:</td>");
				out.println((new StringBuilder()).append("<td><input type=text name=\"school\" id=\"school\" size=30 value=\"").append(Tool.titleEncode(s3)).append("\"></td></tr>").toString());
				out.println("<tr><td>Email:</td>");
				out.println((new StringBuilder()).append("<td><input type=text name=\"email\" id=\"email\" size=30 value=\"").append(Tool.titleEncode(s2)).append("\"></td>").toString());
				out.println("</tr><tr><td>QQ:</td>");
				out.println("<td><input type=text name=\"qq\" id=\"qq\" size=20 value=" + qq + "></td>");
				out.println("</tr><tr><td>&nbsp;</td><td align=left>");
				out.println("<input type=submit value=Submit name=submit onclick='return checkQQ()'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=reset value=Reset name=reset>");
				out.println("</td></tr></table></form>");
				out.println("<font size=4 color=red>Please leave the new password blank if you don't want to change password.</font>");
				FormattedOut.printBottom(request, out);
				connection.close();
				connection = null;
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
			try
			{
				if (connection != null)
				{
					connection.close();
					connection = null;
				}
			} catch (Exception exception1)
			{
			}
		}
		return;
	}

	public void destroy()
	{
	}
}
