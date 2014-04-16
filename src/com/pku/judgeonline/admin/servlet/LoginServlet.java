package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.common.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class LoginServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static String Admin_Index = "admin";
	public static String Admin_Login = "loginpage?url=admin";

	public LoginServlet()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		UserModel.logout(request);
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		String s = request.getParameter("Admin_id");
		if (s == null)
		{
			Tool.GoToURL(Admin_Login, response);
			return;
		}
		String s1 = request.getParameter("Password");
		if (s1 == null)
		{
			Tool.GoToURL(Admin_Login, response);
			return;
		}
		if (UserModel.login(s, s1, null, true, request))
		{
			Tool.GoToURL(Admin_Index, response);
		} else
		{
			Tool.GoToURL(Admin_Login, response);
		}
	}

	public void destroy()
	{
	}

}
