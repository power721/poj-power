package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
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

public class SourceBrowser extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// public static Object prob_mute = new Object();

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
		FormattedOut.printHead(PrintWriter, "SourceBrowser List");
		String str1 = paramHttpServletRequest.getParameter("op");
		if (str1 == null)
			str1 = "";
		String str2 = paramHttpServletRequest.getParameter("user");
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
					preparedstatement = Connection.prepareStatement("select user_id from users where user_id=?");
					preparedstatement.setString(1, str2);
					ResultSet resultset = preparedstatement.executeQuery();
					if (!resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") not existed", PrintWriter);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("select * from privilege where user_id=? and rightstr='source_browser'");
					preparedstatement.setString(1, str2);
					resultset = preparedstatement.executeQuery();
					if (resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") existed", PrintWriter);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("insert into privilege (user_id,rightstr) values (?,'source_browser')");
					preparedstatement.setString(1, str2);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					Tool.GoToURL("admin.sourcebrowser", paramHttpServletResponse);
				} else if (str1.trim().equals("del"))
				{
					preparedstatement = Connection.prepareStatement("select * from privilege where user_id=?" + (UserModel.isRoot(paramHttpServletRequest) ? "" : "and rightstr='source_browser'"));
					preparedstatement.setString(1, str2);
					ResultSet resultset = preparedstatement.executeQuery();

					if (!resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") not existed", PrintWriter);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("delete from privilege where user_id=?" + (UserModel.isRoot(paramHttpServletRequest) ? "" : "and rightstr='source_browser'"));
					preparedstatement.setString(1, str2);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					Tool.GoToURL("admin.sourcebrowser", paramHttpServletResponse);
				} else
				{
					preparedstatement = Connection.prepareStatement("SELECT COUNT(*) AS total FROM privilege" + (UserModel.isRoot(paramHttpServletRequest) ? " order by rightstr" : " WHERE rightstr='source_browser'"));
				}
				ResultSet resultset = preparedstatement.executeQuery();
				resultset.first();
				resultset.close();
				preparedstatement.close();
				preparedstatement = Connection.prepareStatement("SELECT * FROM privilege" + (UserModel.isRoot(paramHttpServletRequest) ? " order by rightstr" : " WHERE rightstr='source_browser'"));
				resultset = preparedstatement.executeQuery();
				PrintWriter.println("<center><img src='./images/user.gif' alt='user'></center><br>");
				PrintWriter.println("<p align=center><font size=5 color=#333399>SourceBrowser List</font></p>");
				PrintWriter.println("<form method=get action=admin.sourcebrowser><center><input type=hidden name=op value=add><input type=text name=user size=20><input type=Submit value=Add></form></center>");
				PrintWriter.println("<TABLE cellSpacing=0 cellPadding=0 width=99% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
				PrintWriter.println("<tr align=center bgcolor=#6589D1>");
				PrintWriter.println("<td width=\"5%\" ><b>No.</b></td>");
				PrintWriter.println("<td width=\"10%\" ><b>User ID</b></td>");
				PrintWriter.println("<td width=\"55%\" ><b>Rightstr</b></td>");
				PrintWriter.println("<td width=\"55%\" ><b>Operator</b></td>");
				PrintWriter.println("</tr>");
				int i = 1;
				for (; resultset.next(); PrintWriter.println("</tr>"))
				{
					PrintWriter.println("<tr align=center>");
					long l2 = i++;
					String str3 = resultset.getString("user_id");
					String str4 = resultset.getString("rightstr");
					PrintWriter.println("<td width=\"5%\" align=\"center\">" + l2 + "</td>");
					PrintWriter.println("<td><a href=\"userstatus?user_id=" + str3 + "\">" + str3 + "</a></td>");
					PrintWriter.println("<td><font color=green>" + Tool.titleEncode(str4) + "</font></td>");
					boolean flag4 = str4.equalsIgnoreCase("source_browser") || str4.equalsIgnoreCase("title") || (!str4.equalsIgnoreCase("root") && UserModel.isRoot(paramHttpServletRequest));
					PrintWriter.println("<td>" + (flag4 ? "<a href=javascript:sbdel(\"" + str3 + "\")>" : "") + "Del" + (flag4 ? "</a>" : "") + "</td>");
				}

				PrintWriter.println("</table>");
				PrintWriter.println("<p align=\"center\">");
				PrintWriter.println("</p>");
				Connection.close();
				preparedstatement.close();
			} catch (Exception Exception)
			{
				ErrorProcess.ExceptionHandle(Exception, PrintWriter);
			}
			FormattedOut.printBottom(PrintWriter);
		}
	}

	public void destroy()
	{
	}
}
