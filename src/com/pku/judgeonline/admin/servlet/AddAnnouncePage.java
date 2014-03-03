package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AddAnnouncePage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddAnnouncePage()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		} else
		{
			response.setContentType("text/html; charset=UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			FormattedOut.printHead(out, "Add a announce");
			Calendar calendar = Calendar.getInstance();
			int i = calendar.get(1);
			int j = calendar.get(2) + 1;
			int k = calendar.get(5);
			int l = calendar.get(11);
			int i1 = calendar.get(12);
			out.println("<form method=POST action=admin.addannounce>");
			out.println("<p align=center><font size=4 color=#333399>Add a Announce</font></p>");
			out.println("<p align=left>Title:<textarea class=ckeditor rows=1 name=title cols=71></textarea></p>");
			out.println("<p align=left>Content:<br><textarea rows=20 name=content id=content cols=76></textarea></p>");
			out.println("<script type=\"text/javascript\">CKEDITOR.replace('content',{toolbarStartupExpanded:'true'});</script>");
			out.println("<p align=left>Start Time:<br>&nbsp;&nbsp;&nbsp;");
			out.println((new StringBuilder()).append("Year:<input type=text name=syear value=").append(i).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Month:<input type=text name=smonth value=").append(j).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Day:<input type=text name=sday value=").append(k).append("  size=7 >&nbsp;").toString());
			out.println((new StringBuilder()).append("Hour:<input type=text name=shour value=").append(l).append("  size=7 >&nbsp;").toString());
			out.println((new StringBuilder()).append("Minute:<input type=text name=sminute value=").append(i1).append(" size=7 ></p>").toString());
			out.println("<p align=left>End Time:<br>&nbsp;&nbsp;&nbsp;");
			out.println((new StringBuilder()).append("Year:<input type=text name=eyear value=").append(i).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Month:<input type=text name=emonth value=").append(j).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Day:<input type=text name=eday value=").append(k).append("  size=7 >&nbsp;").toString());
			out.println((new StringBuilder()).append("Hour:<input type=text name=ehour value=").append(l).append("  size=7 >&nbsp;").toString());
			out.println((new StringBuilder()).append("Minute:<input type=text name=eminute value=").append(i1).append(" size=7 ></p>").toString());
			out.println("<div align=center>");
			out.println("<p><input type=submit value=Submit name=submit><input type=reset value=Reset name=reset></p>");
			out.println("</div></form>");
			FormattedOut.printBottom(out);
			out.close();
			return;
		}
	}

	public void destroy()
	{
	}
}
