package com.pku.judgeonline.admin.common;

import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.ServerConfig;
import java.io.PrintWriter;
import javax.servlet.jsp.JspWriter;

public class FormattedOut
{

	public static String DEFAULT_TITLE = "Welcome To Administrator's Page of Judge Online of ACM ICPC, SWUST";

	public FormattedOut()
	{
	}

	public static void printHead(PrintWriter out, String s)
	{
		if (s == null)
			s = DEFAULT_TITLE;
		out.println("<html>");
		out.println("  <head>");
		out.println("    <meta http-equiv=\"Pragma\" content=\"no-cache\">");
		out.println("    <meta http-equiv=\"Cache-Control\" content=\"no-cache\">");
		out.println("    <meta http-equiv=\"Cache-Control\" max-age=\"0\">");
		out.println("    <META HTTP-EQUIV=\"Expires\" CONTENT=\"000\">");
		out.println("    <meta http-equiv=\"Content-Language\" content=\"zh-cn\">");
		out.println("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		out.println((new StringBuilder()).append("    <title>").append(s).append("</title>").toString());
		out.println("    <link href=\"css/admin.css\" type=\"text/css\" rel=\"Stylesheet\" rev=\"Stylesheet\" media=\"all\"/>");
		out.println("   <script type=\"text/javascript\">var beginTime = new Date().getTime();</script>");
		out.println("    <script type=\"text/javascript\" src=\"js/jquery-1.10.2.min.js\"></script>");
		out.println("    <script type=\"text/javascript\" src=\"js/oj.js\"></script>");
		out.println("    <script type=\"text/javascript\" src=\"ckeditor/ckeditor.js\"></script>");
		out.println("  </head>\n");
		out.println("  <body leftmargin=\"30\">");
		out.println("    <div align=\"center\">");
		out.println("    <center>");
		out.println("    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse\"  width=\"99%\" height=\"50\">");
		out.println("<tr>");
		out.println("<td width=\"100\">");
		out.println((new StringBuilder()).append("<a href=.><img border=\"0\" src=\"images/swust_logol.jpg\" width=\"65\" height=\"65\"/></a></td>").toString());
		out.println("<td>");
		out.println((new StringBuilder()).append("<p align=\"center\"><font color=\"#333399\" size=\"4\"><a href=\"admin\" title=\"·µ»Ø¹ÜÀíÖ÷Ò³\">").append(DEFAULT_TITLE).append("</a></font></td>").toString());
		out.println("<td width=\"100\"></td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</center>");
		out.println("</div><hr>");
	}

	public static void printBottom(PrintWriter out)
	{
		out.println((new StringBuilder()).append("    <p><img height=\"29\" src=\"images/j0293234.wmf\" width=\"40\" border=\"0\">").toString());
		out.println((new StringBuilder()).append("    <font  size=\"3\"><a href=\"").append(LoginServlet.Admin_Index).append("\">Admin's Home Page</a> </font><br>").toString());
		out.println((new StringBuilder()).append("    <p><img height=\"29\" src=\"images/j0293234.wmf\" width=\"40\" border=\"0\">").toString());
		out.println((new StringBuilder()).append("    <font  size=\"3\"><a href=\"").append(".").append("\">Home Page</a> </font><br>").toString());
		out.println("    <hr>");
		out.println("    <p align=\"center\"><font  size=\"3\">All Copyright Reserved 2010-2014<br>");
		out.println((new StringBuilder()).append("Any problem, Please <a href=\"mailto:").append(ServerConfig.getValue("AdminEmail")).append("\">Contact Administrator</a></font></p>").toString());
		out.println("  </body>\n</html>");
	}

	public static void printHead(JspWriter jspwriter, String s)
	{
		if (s == null)
			s = DEFAULT_TITLE;
		try
		{
			jspwriter.println("<html>");
			jspwriter.println("<head>");
			jspwriter.println("<meta http-equiv=\"Pragma\" content=\"no-cache\">");
			jspwriter.println("<meta http-equiv=\"Cache-Control\" content=\"no-cache\">");
			jspwriter.println("<meta http-equiv=\"Cache-Control\" max-age=\"0\">");
			jspwriter.println("<META HTTP-EQUIV=\"Expires\" CONTENT=\000\">");
			jspwriter.println("<meta http-equiv=\"Content-Language\" content=\"zh-cn\">");
			jspwriter.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			jspwriter.println((new StringBuilder()).append("<title>").append(s).append("</title>").toString());
			jspwriter.println("</head>\n");
			jspwriter.println("<body leftmargin=\"30\">");
			jspwriter.println("<div align=\"center\">");
			jspwriter.println("<center>");
			jspwriter.println("<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse\"  width=\"99%\" height=\"50\">");
			jspwriter.println("<tr>");
			jspwriter.println("<td width=\"100\">");
			jspwriter.println((new StringBuilder()).append("<img border=\"0\"src=\"images/pku_logol.jpg\"width=\"65\"height=\"65\"></td>").toString());
			jspwriter.println("<td>");
			jspwriter.println((new StringBuilder()).append("<p align=\"center\"><font color=\"#333399\" size=\"4\">").append(DEFAULT_TITLE).append("</font></td>").toString());
			jspwriter.println("<td width=\"100\"></td>");
			jspwriter.println("</tr>");
			jspwriter.println("</table>");
			jspwriter.println("</center>");
			jspwriter.println("</div><hr>");
		} catch (Exception exception)
		{
		}
	}

	public static void printBottom(JspWriter jspwriter)
	{
		try
		{
			jspwriter.println((new StringBuilder()).append("<p><img height=\"29\" src=\"images/j0293234.wmf\" width=\"40\" border=\"0\">").toString());
			jspwriter.println((new StringBuilder()).append("<font  size=\"3\"><a href=\"").append(LoginServlet.Admin_Index).append("\">Admin's Home Page</a> </font><br>").toString());
			jspwriter.println("<hr>");
			jspwriter.println("<p align=\"center\"><font  size=\"3\">All Copyright Reserved 2010<br>");
			jspwriter.println((new StringBuilder()).append("Any problem, Please <a href=\"mailto:").append(ServerConfig.getValue("AdminEmail")).append("\">Contact Administrator</a></font></p>").toString());
			jspwriter.println("</body></html>");
		} catch (Exception exception)
		{
		}
	}

}
