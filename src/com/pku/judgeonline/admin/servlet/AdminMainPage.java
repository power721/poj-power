package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AdminMainPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdminMainPage()
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
			FormattedOut.printHead(out, "Welcome to admin model");
			out.println("<p align=left><font size=4 color=#333399>What can I do:</font></p>");
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.probmanagerpage title=添加题目>Add a problem</a>&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"admin.probmanagerpage?editor=1\" title=用编辑器添加题目><u>Editor</u></a>\n</p>");
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.problemlist title=题目列表>See the problem list</a>\n</p>");
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.addcontestpage title=添加比赛>Add a contest</a>\n</p>");
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.contestlist title=比赛列表>See the contest list</a>\n</p>");
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=announcement title=公告列表>Announcement List</a>\n</p>");
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=onlineuserslist title=在线用户>Online Users</a>\n</p>");
			if (UserModel.isRoot(request))
			{
				out.println("<p>&nbsp;&nbsp;&nbsp;");
				out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.password title=找回密码>Find Password</a>\n</p>");
			}
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			if (UserModel.isRoot(request))
			{
				out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.roles title=用户组管理>Roles List</a><br></p>");
				out.println("<p>&nbsp;&nbsp;&nbsp;");
				out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.task title=进程管理>Task List</a><br></p>");
			} else
				out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.sourcebrowser title=代码浏览者管理>SourceBrowser</a><br></p>");
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.help title=帮助>Help</a><br></p>");
			out.println("<p>&nbsp;&nbsp;&nbsp;");
			out.println("<img src=images/j0293844.wmf width=25 height=23> <a href=admin.logout title=注销>Logout</a><br></p>");
			/*
			 * out.println("<form action=admin.tools?op=setsysteminfo method=post>"
			 * ); out.println("<input type=text size=100 name=sinfo>");
			 * out
			 * .println("<input type=submit value=set name=ss></form>");
			 */
			// out.print("<form action=admin.tools?op=addsysteminfo method=post>");
			// out.print("<input type=text size=100 name=addsinfo>");
			// out.print("<input type=submit value=add name=add></form>");
			// out.println((new
			// StringBuilder()).append("<td align=center><a href=admin.tools?op=resetsysteminfo>reset</a></td>").toString());

			// out.print("<form action=admin.tools?op=resetsysteminfo method=post>");
			// out.print("<input type=submit value=reset name=clear></form>");
			// out.print("<input type='button' value='reset' onClick='location='admin.tools?op=resetsysteminfo''>");

			FormattedOut.printBottom(out);
			out.close();
			return;
		}
	}

	public void destroy()
	{
	}
}
