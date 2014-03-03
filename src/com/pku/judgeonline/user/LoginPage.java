package com.pku.judgeonline.user;

import com.pku.judgeonline.common.FormattedOut;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class LoginPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginPage()
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
		String s = request.getParameter("url");
		if (s == null || s.trim().equals(""))
			s = ".";
		//HttpSession session = null;
		FormattedOut.printSimpleHead(out, "Login Here");
		out.println("<br><br><center><font color=blue size=5>Log in</font></center>");
		// out.println("<table align=center><tr><td onMouseOver='scbg(this,1)' onMouseOut='scbg(this,0)'>");
		out.println("<table align=center><tr><td>");
		out.println("<form method=POST action=login?action=login>");
		out.println("User  &nbsp;&nbsp;ID:&nbsp;<input type=text name=user_id1 size=10><br>");
		out.println("Password:<input type=password name=password1 size=10><br>");
		out.println("<input type=Submit value=Login name=B1>&nbsp;&nbsp;&nbsp;&nbsp;");
		out.println("<input type=button value=Register name=B2 onclick=\"location.href='registerpage'\" />");
		out.println((new StringBuilder()).append("<input type=hidden name=url value=").append(s).append(">").toString());
		out.println("</form>");
		//String username = request.getParameter("user_id1");
		//if (username != null)
		//	session.setAttribute("username", username);
		out.println("</td></tr></table>");
		FormattedOut.printBottom(request, out);
		out.close();
	}

	public void destroy()
	{
	}
}
