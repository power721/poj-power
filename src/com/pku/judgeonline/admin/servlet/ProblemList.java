package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ProblemList extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProblemList()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		int i = 0;
		int j = 100;
		String order = request.getParameter("order");
		try
		{
			i = Integer.parseInt(request.getParameter("start"));
		} catch (Exception exception)
		{
			i = 0;
		}
		try
		{
			j = Integer.parseInt(request.getParameter("size"));
		} catch (Exception exception1)
		{
			j = 100;
		}
		FormattedOut.printHead(out, "Problem List");
		Connection connection = DBConfig.getConn();
		try
		{
			PreparedStatement preparedstatement = connection.prepareStatement("SELECT COUNT(*) AS total FROM problem");
			ResultSet resultset = preparedstatement.executeQuery();
			resultset.first();
			int k = resultset.getInt("total");
			resultset.close();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("SELECT problem_id,title,source,defunct,in_date,contest_id FROM problem order by problem_id " + (order == null || order.equals("0") ? "desc" : "") + " limit ?,?");
			preparedstatement.setLong(1, i);
			preparedstatement.setLong(2, j);
			resultset = preparedstatement.executeQuery();
			out.println("<p align=center><font size=4 color=#333399>Problem List</font></p>");
			out.println("<table border=1 cellpadding=0 cellspacing=0 bordercolor=#111111 width=100%>");
			out.println("<tr align=center><td  width=8%><a href=admin.problemlist?order=" + (order == null || order.equals("0") ? "1" : "0") + ">Id</a></td><td  width=25%>Title</td><td width=10%>Contest</td>");
			out.println("<td width=14%>Edit</td><td width=16%>Date</td><td width=8%>Defunct</td></tr>");
			for (; resultset.next(); out.print("</tr>"))
			{
				String s = resultset.getString("problem_id");
				String s1 = resultset.getString("contest_id");
				String s2 = resultset.getString("defunct");
				// String s3 = resultset.getString("source");
				out.print((new StringBuilder()).append("<tr align=center><td>").append(s).append("</td>").toString());
				// boolean flags3 = s3 == null || s3.equals("");
				out.print((new StringBuilder()).append("<td align=left><a href=admin.showproblem?problem_id=").append(s).append(">").append(resultset.getString("title")).append("</a>" /*
																																																 * +
																																																 * (
																																																 * flags3
																																																 * ?
																																																 * ""
																																																 * :
																																																 * (
																																																 * "-"
																																																 * +
																																																 * s3
																																																 * )
																																																 * )
																																																 */+ "</td>").toString());
				if (s1 != null)
					out.print((new StringBuilder()).append("<td><a href=admin.contestmanagerpage?contest_id=").append(s1).append(">").append(s1).append("</a></td>").toString());
				else
					out.print("<td>&nbsp;</td>");
				out.print((new StringBuilder()).append("<td><a href=admin.probmanagerpage?problem_id=").append(s).append(">Edit</a>").toString());
				out.print((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;&nbsp;<a href=admin.probmanagerpage?problem_id=").append(s).append("&editor=1>CKEdit</a>").toString());
				out.print((new StringBuilder()).append("&nbsp;&nbsp;&nbsp;&nbsp;<a href=admin.upload?problem_id=").append(s).append(">Data</a></td>").toString());
				out.print((new StringBuilder()).append("<td>").append(resultset.getTimestamp("in_date")).append("</td>").toString());
				if (s2 != null && s2.equals("N"))
					out.print((new StringBuilder()).append("<td><a href=admin.deleteproblem?problem_id=").append(s).append(">Delete</a></td>").toString());
				else
					out.print((new StringBuilder()).append("<td><a href=admin.resumeproblem?problem_id=").append(s).append(">Resume</a></td>").toString());
			}

			preparedstatement.close();
			connection.close();
			out.println("</table>");
			out.println("<p align=center>");
			int l = 0;
			for (int i1 = 0; i1 < k; i1 += j)
				out.print((new StringBuilder()).append("<a href=admin.problemlist?start=").append(i1).append("&size=").append(j).append("&order=" + (order == null || order.equals("0") ? "0" : "1") + ">").append(++l).append("</a>&nbsp;").toString());

			out.println("</p>");
			FormattedOut.printBottom(out);
		} catch (Exception exception2)
		{
			ErrorProcess.ExceptionHandle(exception2, out);
			try
			{
				connection.close();
			} catch (Exception exception3)
			{
			}
			return;
		}
		out.close();
	}

	public void destroy()
	{
	}
}
