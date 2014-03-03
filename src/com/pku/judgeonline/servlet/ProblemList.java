package com.pku.judgeonline.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.common.Page;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;

public class ProblemList extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public ProblemList()
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
		if(!Guard.Guarder(request, response, out))
			return;
		Connection connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		String s = request.getParameter("p");
		String s1 = request.getParameter("order");
		String order = "problem_id";
		String s2 = request.getParameter("asc");
		String asc = s2;
		if(s2 == null || s2.trim().equals(""))
			asc = "";
		String s3 = request.getParameter("size");
		String user = "";
		
		if("title".equals(s1))
			order = new StringBuilder().append("title ").append(asc).append(",problem_id").toString();
		else if("ratio".equals(s1))
			order = new StringBuilder().append("ratio ").append(asc).append(",problem_id").toString();
		else if("accepted".equals(s1))
			order = new StringBuilder().append("accepted ").append(asc).append(",problem_id").toString();
		else if("submit".equals(s1))
			order = new StringBuilder().append("submit ").append(asc).append(",problem_id").toString();
		else if("difficulty".equals(s1))
			order = new StringBuilder().append("difficulty ").append(asc).append(",problem_id").toString();
		else if("date".equals(s1))
			order = new StringBuilder().append("CAST(in_date as signed) ").append(asc).append(",problem_id").toString();
		int p = 1;
		int size = 100;
		int total = 0;
		int pages = 1;
		boolean logined = UserModel.isLoginned(request);
		if (logined)
			user = UserModel.getCurrentUser(request).getUser_id();
		try
		{
			p = Integer.parseInt(s);
		} catch (NumberFormatException numberformatexception)
		{
			p = 1;
			//HttpSession session = request.getSession(true);
			//Object page = session.getAttribute("page");
			Cookie page = Tool.getCookieByName(request, "oj_problem_page");
			if(page != null)
				p = Integer.parseInt(page.getValue());
			if(p < 1)
				p = 1;
			//System.out.println("page: "+page.getValue()+"  "+p);
			/*try
			{
				if (logined)
				{
					preparedstatement = connection.prepareStatement("select volume from users where user_id=?");
					preparedstatement.setString(1, user);
					resultset = preparedstatement.executeQuery();
					if (resultset.next())
						p = resultset.getInt("volume");
					preparedstatement.close();
				}
			} catch (Exception exception)
			{
				p = 1;
				exception.printStackTrace(System.err);
			}*/
		}
		try
		{
			size = Integer.parseInt(s3);
		} catch (NumberFormatException numberformatexception)
		{
			size = 100;
		}
		try
		{
			if(s1 == null || "".equals(s1))
				preparedstatement = connection.prepareStatement("SELECT max(problem_id)-1000 as total FROM problem where UPPER(defunct) = 'N'");
			else
				preparedstatement = connection.prepareStatement("SELECT count(problem_id) as total FROM problem where UPPER(defunct) = 'N'");
			resultset = preparedstatement.executeQuery();
			resultset.next();
			total = resultset.getInt("total");
			Page page = new Page(total, size, request.getQueryString());
			page.setCurrentPage(p);
			pages = page.getTotalPages();
			FormattedOut.printHead(out, request, connection, (new StringBuilder()).append("Problem List ").append(p).toString());
			out.println((new StringBuilder()).append("<p align=center><font size=5>Problem List</font><br/>").toString());
			for (int j = 1; j <= pages; j++)
				out.println((new StringBuilder()).append(" <a href=\"problemlist?").append(page.getQueryString(j)).append("\"><font size=5").append(p != j ? " " : " color=red").append(">").append(j).append("</font></a>").toString());
			out.println("</p>");
			out.println("<img src='./images/search.gif' alt='search'><font color=blue size=5><b>Search:</b></font>");
			out.println((new StringBuilder()).append("<form method=get action=searchproblem><input type=text name=sstr size=25 value='").append("'><select size=1 name=manner><option value=0 selected>All</option><option value=1 >Title</option><option value=2 >Source</option><option value=3 >Tag</option></select><input type=submit value=Search></p>").toString());
			out.println("</form>");
			out.println("<TABLE cellSpacing=0 cellPadding=0 align=center width=99% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.println("<tr bgcolor=#6589D1>");
			
			//HttpSession session = request.getSession(true);
			//session.setAttribute("page", p);
			Tool.addCookie(response, "oj_problem_page", String.valueOf(p), 604800);
			if (logined)
			{
				/*preparedstatement = connection.prepareStatement("update users set volume=? where user_id=?");
				preparedstatement.setLong(1, p);
				preparedstatement.setString(2, user);
				preparedstatement.executeUpdate();
				*/
				out.println("<td width=5% align=center>Solved</td>");
				out.println((new StringBuilder()).append("<td width=10% align=center><a href=\"problemlist?p=").append(p).append("\"><b><font color=white>ID</font></b></a></td>").toString());
				out.println(new StringBuilder().append("<td width=50% align=center><a href=\"problemlist?order=title&asc=").append(asc.equals("")?"desc":"").append("&p=").append(p).append("\"><b><font color=black>Title</font></b></a></td>").toString());
			} else
			{
				out.println((new StringBuilder()).append("<td width=10% align=center><a href=\"problemlist?p=").append(p).append("\"><b><font color=white>ID</font></b></a></td>").toString());
				out.println(new StringBuilder().append("<td width=55% align=center><a href=\"problemlist?order=title&asc=").append(asc.equals("")?"desc":"").append("&p=").append(p).append("\"><b><font color=black>Title</font></b></a></td>").toString());
			}
			out.println((new StringBuilder()).append("<td width=15% align=center><b><a href=\"problemlist?order=ratio&asc=").append(asc.equals("")?"desc":"").append("&p=").append(p).append("\"><font color=white>Ratio</font></a>(<a href=\"problemlist?order=accepted&asc=").append(asc.equals("")?"desc":"").append("&p=").append(p).append("\"><font color=red>AC</font></a>/<a href=\"problemlist?order=submit&asc=").append(asc.equals("")?"desc":"").append("&p=").append(p).append("\"><font color=white>submit</font></a>)</b></td>").toString());
			out.println((new StringBuilder()).append("<td width=8% align=center><a href=\"problemlist?order=difficulty&asc=").append(asc.equals("")?"desc":"").append("&p=").append(p).append("\"><b><font color=white>Difficulty</font></b></a></td>").toString());
			out.println(new StringBuilder().append("<td width=12% align=center><a href=\"problemlist?order=date&asc=").append(asc.equals("")?"desc":"").append("&p=").append(p).append("\"><font color=black>Date</font></a></td>").toString());
			out.println("</tr>");
			preparedstatement = connection.prepareStatement((new StringBuilder()).append("select problem_id,source,title,contest_id,ratio,accepted,submit,difficulty,in_date,defunct from problem ").append("problem_id".equals(order)?"":"where UPPER(defunct) = 'N'").append("order by ").append(order).append(" limit ").append(page.getLimit()).append(",").append(size).toString());
			resultset = preparedstatement.executeQuery();
			while(resultset.next())
			{
				if (!"N".equalsIgnoreCase(resultset.getString("defunct")))
					continue;
				boolean contest_end = true;
				boolean permission = true;
				long pid = resultset.getLong("problem_id");
				String cid = resultset.getString("contest_id");
				if (cid != null)
				{
					PreparedStatement preparedstatement2 = connection.prepareStatement("select start_time,end_time,private from contest where contest_id=?");
					preparedstatement2.setInt(1, Integer.parseInt(cid));
					ResultSet resultset2 = preparedstatement2.executeQuery();
					if (resultset2.next())
					{
						contest_end = resultset2.getTimestamp("end_time").getTime() < System.currentTimeMillis();
						permission = Tool.permission(connection, request, Integer.parseInt(cid));
					}
					
					if (contest_end)
					{
						preparedstatement2 = connection.prepareStatement("update problem set contest_id=null where problem_id=?");
						preparedstatement2.setLong(1, pid);
						preparedstatement2.executeUpdate();
					}
					preparedstatement2.close();
				}
				if (permission)
				{
					out.println("<tr>");
					if (logined)
					{
						out.println("<td align=center>");
						int k = -1;
						PreparedStatement preparedstatement1 = connection.prepareStatement("select min(result)as status from solution where user_id=? and problem_id=?");
						preparedstatement1.setString(1, user);
						preparedstatement1.setLong(2, pid);
						ResultSet resultset1 = preparedstatement1.executeQuery();
						if (resultset1.next())
						{
							k = resultset1.getInt("status");
							if (resultset1.wasNull())
								out.print("&nbsp;");
							else if (k == 0)
								out.print("<img border=0 src=images/accepted.gif>");
							else
								out.print("<img border=0 src=images/wrong.gif>");
						}
						out.println("</td>");
					}

					out.println((new StringBuilder()).append("<td align=center>").append(pid).append("</td>").toString());
					out.println((new StringBuilder()).append("<td ><a href=\"showproblem?problem_id=").append(pid).append("\">").append(resultset.getString("title")).append("</a></td>").toString());
					long l4 = resultset.getLong("submit");
					long l5 = resultset.getLong("accepted");
					int i1 = resultset.getInt("ratio");
					String s5;
					if (l4 != 0L)
						s5 = (new StringBuilder()).append(i1).append("%(<a href=\"problemstatus?problem_id=").append(pid).append("\">").append(l5).append("</a>/<a href=\"status?problem_id=").append(pid).append("\">").append(l4).append("</a>)").toString();
					else
						s5 = (new StringBuilder()).append(l5).append("/").append(l4).toString();
					out.println((new StringBuilder()).append("<td align=center>").append(s5).append("</td>").toString());
					out.println((new StringBuilder()).append("<td align=center>").append(resultset.getInt("difficulty")).append("%</td>").toString());
					Timestamp timestamp = resultset.getTimestamp("in_date");
					if (timestamp != null)
						out.println((new StringBuilder()).append("<td align=center>").append(DateFormat.getDateInstance().format(timestamp)).append("</td>").toString());
					else
						out.println("<td >&nbsp;</td>");
					out.println("</tr>");
				}
			}
			out.println("</table>");
			out.println((new StringBuilder()).append("<p align=center>").toString());
			for (int j = 1; j <= pages; j++)
				out.println((new StringBuilder()).append(" <a href=\"problemlist?p=").append(j).append("\"><font size=5").append(p != j ? " " : " color=red").append(">").append(j).append("</font></a>").toString());
			out.println("</p>");
			preparedstatement.close();
			connection.close();
		}
		catch(Exception e)
		{
			ErrorProcess.ExceptionHandle(e, out);
		}
		FormattedOut.printBottom(request, out);
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
