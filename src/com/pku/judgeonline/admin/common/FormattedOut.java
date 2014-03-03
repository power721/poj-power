package com.pku.judgeonline.admin.common;

import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.ServerConfig;

import java.io.PrintWriter;

public class FormattedOut
{

	public static String DEFAULT_TITLE = "Welcome To Administrator's Page of Judge Online of ACM ICPC, SWUST";

	public FormattedOut()
	{
	}

	public static void printHead(PrintWriter out, String s)
	{
		ServerConfig.startTimestamp = System.currentTimeMillis();
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
		//out.println("   </div><!-- div main -->\n");
		out.println((new StringBuilder()).append("    <p><img height=\"29\" src=\"images/j0293234.wmf\" width=\"40\" border=\"0\">").toString());
		out.println((new StringBuilder()).append("    <font  size=\"3\"><a href=\"").append(LoginServlet.Admin_Index).append("\">Admin's Home Page</a> </font><br>").toString());
		out.println((new StringBuilder()).append("    <p><img height=\"29\" src=\"images/j0293234.wmf\" width=\"40\" border=\"0\">").toString());
		out.println((new StringBuilder()).append("    <font  size=\"3\"><a href=\"").append(".").append("\">Home Page</a> </font><br>").toString());
		out.println("   <center><div class=\"footer\">");
		out.println("     <img height=29 src=\"images/home.jpg\" width=40 border=0><a href=.>Home Page</a>&nbsp;&nbsp;");
		out.println("     <img height=29 src=\"images/goback.jpg\" width=40 border=0><a href=\"javascript:history.go(-1)\">Go Back</a>&nbsp;&nbsp;");
		out.println("     <img height=29 width=40 border=0 src=\"images/top.jpg\"><a href=\"#top\" target=\"_self\">To top</a>");
		out.println("     <br><hr>");
		out.println("     <div class=\"copyright\">");
		out.println("       <a href=\"http://git.oschina.net/power/poj-power.git\" target=\"_balnk\">Power OJ Rev.20140303</a>|");
		out.println("       <a href=\"faq.htm\">F.A.Q</a>|");
		out.println("       <span id=\"divPageLoadTime\">");
		out.println(System.currentTimeMillis() - ServerConfig.startTimestamp);
		out.println(" ms</span><br>");
		out.println("       All Copyright Reserved 2010-2012 <a href=mailto:power0721@gmail.com><b>power721</b></a><br>");
		out.println(new StringBuilder().append("       Any problem, Please <a href=mailto:").append(ServerConfig.getValue("AdminEmail")).append(">Contact Administrator</a>").toString());
		out.println("     </div>");
		out.println("   </div></center><!-- div footer -->\n");
		out.println(" </body>");
		out.println("</html>");
	}

}
