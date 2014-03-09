package com.pku.judgeonline.servlet;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Index extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Index()
	{
	}

	public void init() throws ServletException
	{
		ServerConfig.init();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		int i = 0;
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("SELECT count(problem_id) as num FROM problem where UPPER(defunct) = 'N'");
			ResultSet resultset = preparedstatement.executeQuery();
			resultset.next();
			i = resultset.getInt("num");
			resultset.close();
			preparedstatement.close();
			connection.close();
		} catch (Exception exception)
		{
		}
		String DefaultTitleOJ = new String(ServerConfig.getValue("DefaultTitleOJ").getBytes("ISO8859_1"), "UTF-8");
		String s = (new StringBuilder()).append("<br>&nbsp;&nbsp;OJ使用的相关帮助请参见<a href=\"faq.htm\">F.A.Q.</a><br>&nbsp;&nbsp;你可以在<a href=\"problemlist\"><b>题库</b></a>找到练习题目并<a href=\"submitpage\"><b>提交</b></a>代码通过本系统测试你的解答。<br>&nbsp;&nbsp;如果有什么疑问或建议请联系<a href=\"sendpage?to=root\">管理员</a>。<br>&nbsp;&nbsp;题目数量：").append(i).append("<br>").toString();
		FormattedOut.printHead(out, request, null, null);
		out.println("<table border=\"0\" align=\"center\" width=\"99%\" background=\"images/table_back.jpg\"><tr><td>");
		out.println((new StringBuilder()).append("<center><h1><font color=\"blue\">").append(DefaultTitleOJ).append("</font></h1></center>").toString());
		out.println("<center><a href=\"http://power-oj.com/contest/recent\" target=\"_blank\"><font size=5 color=\"red\"><u><b>Recent Contests</b></u></font></a></center>");

		out.println(s);
		out.println("<script  type=\"text/javascript\">myprint();</script><br>");
		out.println("&nbsp;&nbsp;<a href=\"http://www.oj.swust.edu.cn/gongju/link.html\" target=\"_blank\"><b><u><span class=\"red\">OJ大全</span></u></b></a><br>");
		out.println("&nbsp;&nbsp;<a href=\"http://www.oj.swust.edu.cn/gongju\" target=\"_blank\"><b><u><span class=\"red\">资料下载</span></u></b></a><br>");
		out.println("&nbsp;&nbsp;<a href=\"ACShare\" target=\"_blank\"><b><u><span class=\"red\">AC共享计划</sspan></u></b></a><br>");
		out.println("<p>&nbsp;&nbsp;Problem Set is the place where you can find large amount of problems from different programming contests.");
		out.println("Online Judge System allows you to test your solution for every problem.</p>");
		out.println("<p>&nbsp;&nbsp;First of all, read carefully <a href=\"faq.htm\" target=\"_blank\">Frequently Asked Questions</a>.<br>");
		out.println("&nbsp;&nbsp;Then, choose <a href=\"problemlist\">problem</a>, solve it and <a href=\"submitpage\">submit</a> it.</p>");
		out.println((new StringBuilder()).append("<p>&nbsp;&nbsp;If you want to publish your problems or setup your own online contest, just <a href=\"mailto:").append(ServerConfig.getValue("AdminEmail")).append("\">write us</a><br></p>").toString());
		out.println("</td></tr></table>");
		FormattedOut.printBottom(request, out);
	}

	public void destroy()
	{
	}
}
