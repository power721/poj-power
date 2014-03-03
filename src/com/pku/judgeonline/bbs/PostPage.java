package com.pku.judgeonline.bbs;

import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.common.UserModel;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class PostPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PostPage()
	{
	}

	public void init() throws ServletException
	{
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		FormattedOut.printHead(out, request, null, "Post New Message");
		int cid = 0;
		int pid = 0;
		try
		{
			pid = Integer.parseInt(request.getParameter("problem_id"));
		} catch (Exception exception)
		{
			pid = 0;
		}
		try
		{
			cid = Integer.parseInt(request.getParameter("contest_id"));
		} catch (Exception exception)
		{
			cid = 0;
		}

		out.print("<h2>Post new message");
		if (pid != 0)
			out.print((new StringBuilder()).append(" for problem ").append(pid).toString());
		out.println("</h2>");
		out.println("<form method=POST action=post>");
		out.println((new StringBuilder()).append("<input type=hidden name=problem_id value=").append(pid).append(">").toString());
		out.println((new StringBuilder()).append("<input type=hidden name=contest_id value=").append(cid).append(">").toString());
		if (!UserModel.isLoginned(request))
		{
			out.println("User ID: <input type=text name=user_id size=22><br>");
			out.println("Password:<input type=password name=password size=22><br>");
		}
		out.println("Title:<br><input type=text name=title size=100><br>");
		out.println("Content:<br>");
		out.println("<textarea rows=20 name=content cols=100></textarea><br>");
		out.println("<input type=Submit value=Post name=B1>");
		out.println("</form>");
		FormattedOut.printBottom(request, out);
	}

	public void destroy()
	{
	}
}
