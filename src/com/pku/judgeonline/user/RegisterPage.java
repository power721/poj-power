package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class RegisterPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegisterPage()
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
		FormattedOut.printHead(out, request, null, "Register Information");
		if (UserModel.isLoginned(request))
			Tool.GoToURL("", response);
		out.println("<form method=POST action=register>");
		out.println("<TABLE align=center cellSpacing=3 cellPadding=3 width=600 border=0 background=images/table_back.jpg>");
		out.println("<tr><td colspan=2 width=600 height=40>");
		out.println("<p align=center>Register Information</td></tr>");
		out.println("<tr><td width=25%>User ID:</td>");
		out.println("<td width=75%><input type=text name=user_id size=20></td></tr>");
		out.println("<tr><td>Nick:</td>");
		out.println("<td><input type=text name=nick size=50></td></tr>");
		out.println("<tr><td>Password:</td>");
		out.println("<td><input type=password name=password size=20></td>");
		out.println("</tr><tr>");
		out.println("<td>Repeat Password:</td>");
		out.println("<td>");
		out.println("<input type=password name=rptPassword size=20></td></tr>");
		out.println("<tr><td>School:</td>");
		out.println("<td><input type=text name=school size=30></td></tr>");
		out.println("<tr><td>Email:</td>");
		out.println("<td><input type=text name=email size=30></td>");
		out.println("</tr><tr><td>QQ:</td>");
		out.println("<td><input type=text name=qq size=20></td>");
		out.println("</tr><tr><td>&nbsp;</td><td align=left>");
		out.println("<input type=submit value=Submit name=submit  onclick='return checkQQ()'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=reset value=Reset name=reset >");
		out.println("</td></tr></table></form>");
		FormattedOut.printBottom(request, out);
	}

	public void destroy()
	{
	}
}
