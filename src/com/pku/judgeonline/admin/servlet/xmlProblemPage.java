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

public class xmlProblemPage extends HttpServlet
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
		PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
		FormattedOut.printHead(localPrintWriter, "New Problem");
		localPrintWriter.println("<h1>Add New problem</h1>");
		Connection localConnection = null;
		String str = "";
		String str13 = "";
		localConnection = DBConfig.getConn();

		localPrintWriter.println("<form method=GET action=admin.addXmlProblem>");
		localPrintWriter.println("<p align=center><font size=4 color=#333399>Add Problems from xmlfile.</font></p>");
		localPrintWriter.println("<p align=center><a href=http://code.google.com/p/freeproblemset/downloads/list target=blank>FPS(freeproblemset)</a></p>");
		localPrintWriter.println("<p align=left>FilePath:<textarea name=str rows=1 cols=71>" + str + "</textarea></p>");
		localPrintWriter.println("<p align=left>contest:<select  name=contest_id><option value=\"\">no contest</option>");
		try
		{
			PreparedStatement localPreparedStatement2;
			if ((str13 == null) || (str13.equals("")))
				localPreparedStatement2 = localConnection.prepareStatement("select contest_id,title from contest where upper(defunct)='N' and start_time>now()");
			else
				localPreparedStatement2 = localConnection.prepareStatement("select contest_id,title from contest where upper(defunct)='N' and end_time>now()");
			for (ResultSet localResultSet2 = localPreparedStatement2.executeQuery(); localResultSet2.next();)
			{
				String str15 = localResultSet2.getString("contest_id");
				if (str15.equals(str13))
					localPrintWriter.print("<option value=" + str15 + " selected>" + localResultSet2.getString("title") + "</option>\n");
				else
				{
					localPrintWriter.print("<option value=" + str15 + ">" + localResultSet2.getString("title") + "</option>\n");
				}
			}
			localPrintWriter.println("</select></p>");
			localPrintWriter.println("<div align=center>");
			localPrintWriter.println("<input type=submit value=Submit name=submit>");
			localPrintWriter.println("</div></form>");
			FormattedOut.printBottom(localPrintWriter);
			localPreparedStatement2.close();
			localConnection.close();
		} catch (Exception localException3)
		{
			try
			{
				localConnection.close();
			} catch (Exception localException5)
			{
			}
			ErrorProcess.ExceptionHandle(localException3, paramHttpServletResponse.getWriter());
			return;
		}
		localPrintWriter.close();
	}

	public void destroy()
	{
	}
}
