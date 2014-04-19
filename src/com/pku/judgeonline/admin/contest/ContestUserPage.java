package com.pku.judgeonline.admin.contest;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.admin.servlet.LoginServlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ContestUserPage extends HttpServlet
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
		FormattedOut.printHead(PrintWriter, "ContestUserPage List");
		String str1 = paramHttpServletRequest.getParameter("op");
		if (str1 == null)
			str1 = "";
		String str2 = paramHttpServletRequest.getParameter("user");
		if (str2 == null)
			str2 = "";
		int id = 0;
		try
		{
			id = Integer.parseInt(paramHttpServletRequest.getParameter("contest_id"));
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, PrintWriter);
			return;
		}
		String title = "";
		Connection Connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		synchronized (prob_mute)
		{
			try
			{
				preparedstatement = Connection.prepareStatement("select * from contest where contest_id=?");
				preparedstatement.setInt(1, id);
				ResultSet resultset = preparedstatement.executeQuery();
				if (!resultset.first())
				{
					ErrorProcess.Error("The contest is not existed.", PrintWriter);
					preparedstatement.close();
					Connection.close();
					return;
				}
				if (resultset.getInt("private") == 0)
				{
					ErrorProcess.Error("The contest is not private.", PrintWriter);
					preparedstatement.close();
					Connection.close();
					return;
				}
				title = resultset.getString("title");
				if (str1.trim().equals("add"))
				{
					preparedstatement = Connection.prepareStatement("select user_id from users where user_id=?");
					preparedstatement.setString(1, str2);
					resultset = preparedstatement.executeQuery();
					if (!resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") not existed.", PrintWriter);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("select * from private where user_id=? and contest_id=?");
					preparedstatement.setString(1, str2);
					preparedstatement.setInt(2, id);
					resultset = preparedstatement.executeQuery();
					if (resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") existed in this contest.", PrintWriter);
						preparedstatement.close();
						Connection.close();
						return;
					}
					if (Tool.isAdmin(Connection, str2))
					{
						ErrorProcess.Error("The administrator( " + str2 + ") have access to this contest.", PrintWriter);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("insert into private (user_id,contest_id) values (?,?)");
					preparedstatement.setString(1, str2);
					preparedstatement.setInt(2, id);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					Tool.GoToURL("admin.contestuser?contest_id=" + id, paramHttpServletResponse);
					return;
				} else if (str1.trim().equals("del"))
				{
					preparedstatement = Connection.prepareStatement("select * from private where user_id=? and contest_id=?");
					preparedstatement.setString(1, str2);
					preparedstatement.setInt(2, id);
					resultset = preparedstatement.executeQuery();

					if (!resultset.first())
					{
						ErrorProcess.Error("The user( " + str2 + ") not existed in this contest.", PrintWriter);
						preparedstatement.close();
						Connection.close();
						return;
					}
					preparedstatement.close();
					preparedstatement = Connection.prepareStatement("delete from private where user_id=? and contest_id=?");
					preparedstatement.setString(1, str2);
					preparedstatement.setInt(2, id);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					Tool.GoToURL("admin.contestuser?contest_id=" + id, paramHttpServletResponse);
					return;
				} else
				{
					preparedstatement = Connection.prepareStatement("SELECT COUNT(*) AS total FROM private WHERE contest_id=?");
					preparedstatement.setInt(1, id);
				}
				resultset = preparedstatement.executeQuery();
				resultset.first();
				resultset.close();
				preparedstatement.close();
				preparedstatement = Connection.prepareStatement("SELECT * FROM private WHERE contest_id=?");
				preparedstatement.setInt(1, id);
				resultset = preparedstatement.executeQuery();
				PrintWriter.println("<center><img src='./images/user.gif' alt='user'></center><br>");
				PrintWriter.println("<p align=center><font size=5 color=#333399>PrivateContestUser List</font></p>");
				PrintWriter.println("<p align=center><font size=5 >Contest " + id + ":<a href=showcontest?contest_id=" + id + ">" + title + "</a></font></p>");
				PrintWriter.println("<form method=get action=admin.contestuser><center><input type=hidden name=contest_id value=" + id + "><input type=hidden name=op value=add><input type=text name=user size=20><input type=Submit value=Add></form></center>");
				PrintWriter.println("<TABLE cellSpacing=0 cellPadding=0 width=750 align=center border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
				PrintWriter.println("<tr align=center bgcolor=#6589D1>");
				PrintWriter.println("<td width=\"20%\" ><b>No.</b></td>");
				PrintWriter.println("<td width=\"50%\" ><b>User ID</b></td>");
				// PrintWriter.println("<td width=\"55%\" ><b>Rightstr</b></td>");
				PrintWriter.println("<td width=\"30%\" ><b>Operator</b></td>");
				PrintWriter.println("</tr>");
				int i = 1;
				for (; resultset.next(); PrintWriter.println("</tr>"))
				{
					PrintWriter.println("<tr align=center>");
					long l2 = i++;
					String str3 = resultset.getString("user_id");
					PrintWriter.println("<td width=\"5%\" align=\"center\">" + l2 + "</td>");
					PrintWriter.println("<td><a href=\"userstatus?user_id=" + str3 + "\">" + str3 + "</a></td>");
					// PrintWriter.println("<td><font color=green>" +
					// Tool.titleEncode(str4) + "</font></td>");
					PrintWriter.println("<td><a href=javascript:pcdel(\"" + str3 + "\"," + id + ")>Del</a></td>");
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
