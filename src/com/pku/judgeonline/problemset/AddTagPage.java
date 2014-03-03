package com.pku.judgeonline.problemset;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.admin.servlet.*;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AddTagPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AddTagPage()
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
			FormattedOut.printHead(out, "Add a tag");
			out.println("<form method=get action=addtag>");
			out.println("<p align=center><font size=4 color=#333399>Add a Tag</font></p>");
			out.println("<p align=left>problem_id:<input type=text name=problem_id size=10></p>");
			out.println("<p align=left>Tag:<input type=text name=tag size=30></p>");
			out.println("<div align=center>");
			out.println("<p><input type=submit value=Submit ><input type=reset value=Reset ></p>");
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
