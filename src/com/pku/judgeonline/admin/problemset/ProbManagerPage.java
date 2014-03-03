package com.pku.judgeonline.admin.problemset;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.LoginServlet;
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

public class ProbManagerPage extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		if (!UserModel.isAdminLoginned(paramHttpServletRequest))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, paramHttpServletResponse);
			return;
		}
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter PrintWriter = paramHttpServletResponse.getWriter();
		String str1 = "modify";
		int i = 0;
		try
		{
			i = Integer.parseInt(paramHttpServletRequest.getParameter("problem_id"));
		} catch (Exception Exception1)
		{
			str1 = "new";
		}
		if (str1.equals("modify"))
		{
			FormattedOut.printHead(PrintWriter, "Modify " + i);
			PrintWriter.println("<h1>Modify problem " + i + "</h1>");
		} else
		{
			FormattedOut.printHead(PrintWriter, "New Problem");
			PrintWriter.println("<h1>Add New problem</h1>");
		}
		Connection Connection = null;
		PreparedStatement PreparedStatement1 = null;
		ResultSet ResultSet1 = null;
		boolean flags = false;
		String ss = paramHttpServletRequest.getParameter("editor");
		if (ss != null && ss.equals("1") == true)
			flags = true;
		if (ss != null && ss.equals("0") == true)
			flags = false;
		String str13 = "";
		String str12 = "";
		String str11 = "";
		String str10 = "";
		String str9 = "";
		String str8 = "";
		String str7 = "";
		String str6 = "";
		String str2 = "";
		String str4 = "65536";
		String str3 = "1000";
		String str5 = "30000";
		String str14 = "New Problem";
		String str99 = "";
		Connection = DBConfig.getConn();
		if (str1.equalsIgnoreCase("modify"))
		{
			try
			{
				PreparedStatement1 = Connection.prepareStatement("SELECT *  FROM problem WHERE problem_id=?");
				PreparedStatement1.setInt(1, i);
				ResultSet1 = PreparedStatement1.executeQuery();
				if (!ResultSet1.next())
				{
					ResultSet1.close();
					PreparedStatement1.close();
					Connection.close();
					ErrorProcess.Error("No such problem, ID:" + i, PrintWriter);
					return;
				}

				str14 = ResultSet1.getString("problem_id");
				str2 = ResultSet1.getString("title");
				str3 = ResultSet1.getString("time_limit");
				str4 = ResultSet1.getString("memory_limit");
				str5 = ResultSet1.getString("case_time_limit");
				str6 = ResultSet1.getString("description").replaceAll("&lt;", "&#38;lt;");
				str7 = ResultSet1.getString("input").replaceAll("&lt;", "&#38;lt;");
				str8 = ResultSet1.getString("output").replaceAll("&lt;", "&#38;lt;");
				str9 = ResultSet1.getString("sample_input").replaceAll("&lt;", "&#38;lt;");
				str10 = ResultSet1.getString("sample_output").replaceAll("&lt;", "&#38;lt;");
				str11 = ResultSet1.getString("hint").replaceAll("&lt;", "&#38;lt;");
				str12 = ResultSet1.getString("source");
				str13 = ResultSet1.getString("contest_id");
				str99 = ResultSet1.getString("defunct");
				PreparedStatement1.close();
			} catch (Exception Exception2)
			{
				ErrorProcess.ExceptionHandle(Exception2, PrintWriter);
				try
				{
					Connection.close();
				} catch (Exception Exception4)
				{
				}
				return;
			}
		}
		if (str1.equalsIgnoreCase("modify"))
		{
			PrintWriter.println("<form method=POST action=admin.modifyproblem>");
			PrintWriter.println("<p align=center><font size=4 color=#333399>Modify Problem</font></p>");
		} else
		{
			PrintWriter.println("<form method=POST action=admin.addproblem>");
			PrintWriter.println("<p align=center><font size=4 color=#333399>Add a Problem</font></p>");
		}

		PrintWriter.println("<input type=hidden name=problem_id value=" + str14 + ">");
		PrintWriter.println("<p align=left>Problem Id:&nbsp;&nbsp;" + str14 + "</p>");
		PrintWriter.println("<p align=left>Title:<textarea name=title rows=1 cols=71>" + str2 + "</textarea></p>");
		PrintWriter.println("<p align=left>Time Limit:<input type=text name=time_limit size=20 value=" + str3 + ">MS</p>");
		PrintWriter.println("<p align=left>Memory Limit:<input type=text name=memory_limit size=20 value=" + str4 + ">KByte</p>");
		PrintWriter.println("<p align=left>Case Time Limit:<input type=text name=case_time_limit size=20 value=" + str5 + ">MS</p>");
		PrintWriter.println("<center><a href=admin.help target='_blank'>Help</a></center>");
		PrintWriter.println("<p align=left>Description:<br><textarea  rows=13 name=description id=description cols=120>" + str6 + "</textarea></p>");
		if (flags)
			PrintWriter.println("<script type=\"text/javascript\">CKEDITOR.replace('description',{toolbarStartupExpanded:'true'});</script>");

		PrintWriter.println("<p align=left>Input:<br><textarea " + (flags ? " class=ckeditor " : "") + " rows=13 name=input cols=120>" + str7 + "</textarea></p>");
		PrintWriter.println("<p align=left>Output:<br><textarea " + (flags ? " class=ckeditor " : "") + " rows=13 name=output cols=120>" + str8 + "</textarea></p>");
		PrintWriter.println("<p align=left>Sample Input:<br><textarea " + (flags ? " class=ckeditor " : "") + " rows=13 name=sample_input cols=120>" + str9 + "</textarea></p>");
		PrintWriter.println("<p align=left>Sample Output:<br><textarea " + (flags ? " class=ckeditor " : "") + " rows=13 name=sample_output cols=120>" + str10 + "</textarea></p>");
		PrintWriter.println("<p align=left>Hint:<br><textarea " + (flags ? " class=ckeditor " : "") + " rows=13 name=hint cols=120>" + str11 + "</textarea></p>");
		PrintWriter.println("<p align=left>Source:<br><textarea name=source rows=1 cols=70>" + str12 + "</textarea></p>");
		PrintWriter.println("<p align=left>contest:<select  name=contest_id><option value=\"\">no contest</option>");
		try
		{
			PreparedStatement PreparedStatement2;
			if ((str13 == null) || (str13.equals("")))
				PreparedStatement2 = Connection.prepareStatement("select contest_id,title from contest where upper(defunct)='N' and start_time>now()");
			else
				PreparedStatement2 = Connection.prepareStatement("select contest_id,title from contest where upper(defunct)='N' and end_time>now()");
			for (ResultSet ResultSet2 = PreparedStatement2.executeQuery(); ResultSet2.next();)
			{
				String str15 = ResultSet2.getString("contest_id");
				if (str15.equals(str13))
					PrintWriter.print("<option value=" + str15 + " selected>" + ResultSet2.getString("title") + "</option>\n");
				else
				{
					PrintWriter.print("<option value=" + str15 + ">" + ResultSet2.getString("title") + "</option>\n");
				}
			}
			PrintWriter.println("</select> <a href=admin.addcontestpage target=blank>Add a contest</a></p>");
			PrintWriter.println("<p align=left>Hide:<br/>&nbsp;&nbsp;<input type=radio name=hide " + (str99.equals("N") ? "checked" : "") + "  value=N>No<input type=radio name=hide " + (str99.equals("N") ? "" : "checked") + "  value=Y>Yes</p>");
			PrintWriter.println("<div align=center>");
			PrintWriter.println("<input type=submit value=Submit name=submit>");
			PrintWriter.println("</div></form>");
			FormattedOut.printBottom(PrintWriter);
			PreparedStatement2.close();
			Connection.close();
		} catch (Exception Exception3)
		{
			try
			{
				Connection.close();
			} catch (Exception Exception5)
			{
			}
			ErrorProcess.ExceptionHandle(Exception3, PrintWriter);
			return;
		}
		PrintWriter.close();
	}

	public void destroy()
	{
	}
}
