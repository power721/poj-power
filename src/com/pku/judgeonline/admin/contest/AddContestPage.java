package com.pku.judgeonline.admin.contest;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AddContestPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddContestPage()
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
			FormattedOut.printHead(out, "Add a contest");
			Calendar calendar = Calendar.getInstance();
			int i = calendar.get(1);
			int j = calendar.get(2) + 1;
			int k = calendar.get(5);
			int l = calendar.get(11);
			out.println("<form method=POST action=admin.addcontest>");
			out.println("<p align=center><font size=4 color=#333399>Add a Contest</font></p>");
			out.println("<p align=left>Title:<input type=text name=title size=71></p>");
			out.println("<p align=left>Description:<br><textarea rows=8 name=description cols=76></textarea></p>");
			out.println("<p align=left>Start Time:<br>&nbsp;&nbsp;&nbsp;");
			out.println((new StringBuilder()).append("Year:<input type=text name=syear value=").append(i).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Month:<input type=text name=smonth value=").append(j).append(" size=7 >").toString());
			out.println("Day:<input type=text name=sday size=7 value=" + k + ">&nbsp;");
			out.println("Hour:<input type=text name=shour size=7  value=" + l + ">&nbsp;");
			out.println("Minute:<input type=text name=sminute value=0 size=7 ></p>");
			out.println("<p align=left>End Time:<br>&nbsp;&nbsp;&nbsp;");
			out.println((new StringBuilder()).append("Year:<input type=text name=eyear value=").append(i).append(" size=7 >").toString());
			out.println((new StringBuilder()).append("Month:<input type=text name=emonth value=").append(j).append(" size=7 >").toString());
			out.println("Day:<input type=text name=eday size=7 value=" + k + ">&nbsp;");
			out.println("Hour:<input type=text name=ehour size=7  value=" + (l + 5) + ">&nbsp;");
			out.println("Minute:<input type=text name=eminute value=0 size=7 >");
			out.println("<br>Type<br/>&nbsp;&nbsp;<input type=\"radio\" name=\"private\" checked  value=\"0\">Public<input type=\"radio\" name=\"private\"  value=\"1\">Private<input type=\"radio\" name=\"private\"  value=\"2\">Strict Private");
			out.println("<br>Freeze Board?<input type=\"checkbox\" name=\"freeze\" value=\"1\" />");
			out.println("</p><div align=center>");
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
