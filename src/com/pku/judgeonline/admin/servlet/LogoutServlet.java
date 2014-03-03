package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.UserModel;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class LogoutServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LogoutServlet()
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
		UserModel.logout(request);
		FormattedOut.printHead(out, "You have logged out!");
		out.println("<p>You have logged out, Thanks a lot!</p>");
		FormattedOut.printBottom(out);
	}

	public void destroy()
	{
	}
}
