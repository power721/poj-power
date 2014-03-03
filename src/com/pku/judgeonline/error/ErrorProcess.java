package com.pku.judgeonline.error;

import com.pku.judgeonline.common.*;
import java.io.PrintWriter;

public class ErrorProcess
{

	public ErrorProcess()
	{
	}

	public static void Error(String s, PrintWriter out)
	{
		if (out == null)
		{
			return;
		} else
		{
			FormattedOut.printSimpleHead(out, "Error");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
			out.println("<font size=\"4\">Error Occurred</font></p>");
			out.println("<ul>");
			out.println((new StringBuilder()).append("  <li>").append(s).append("</li>").toString());
			out.println("</ul>");
			out.println("<p>");
			FormattedOut.printBottom(null, out);
			return;
		}
	}

	public static void ExceptionHandle(Exception exception, PrintWriter out)
	{
		if (out == null)
		{
			return;
		} else
		{
			out.println("<html>");
			out.println("<head>");
			out.println("<meta http-equiv=\"Content-Language\" content=\"zh-cn\">");
			out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			out.println("<title>Exception Occurred</title>");
			out.println("</head>");
			out.println("<body leftmargin=\"30\">");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
			out.println("<font size=\"4\">Exception Occurred</font></p>");
			out.println("<ul>");
			out.println((new StringBuilder()).append("  <li>").append(Tool.htmlEncode(exception.getMessage())).append("</li>").toString());
			out.println("  <li>Exception stack</li>");
			out.println("<pre>");
			exception.printStackTrace(out);
			out.println("</pre>");
			out.println("</ul>");
			out.println("<p>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293234.wmf\" width=\"40\" height=\"29\"> ").toString());
			out.println("<a href=\"javascript:history.go(-1)\">Go Back</a>");
			out.println((new StringBuilder()).append("<img border=\"0\" src=\"images/j0293236.wmf\" width=\"40\" height=\"29\">").toString());
			out.println((new StringBuilder()).append("<a href=\"mailto:").append(ServerConfig.getValue("AdminEmail")).append("\">Contact Administrator</a></p>").toString());
			out.println("</body>");
			out.println("</html>");
			exception.printStackTrace(System.err);
			return;
		}
	}
}
