package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Login extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private static String _$3439 = "<form method=POST action=login?action=login>User ID:&nbsp;<input type=text name=user_id1 size=10><br>Password:<input type=password name=password1 size=10><input type=Submit value=login name=B1>&nbsp;&nbsp;<a href=registerpage target=_parent>Register</a></form>";
	ServletContext servletContext;
	HttpSession session;

	public Login()
	{
		servletContext = null;
		session = null;
	}

	public void init() throws ServletException
	{
	}

	public static void showMail(HttpServletRequest request, PrintWriter out, Connection connection, boolean flag)
	{
		try
		{
			String s = null;
			if (flag)
			{
				s = request.getRequestURI();
				String s1 = request.getQueryString();
				if (s1 != null)
					s = (new StringBuilder()).append(s).append("?").append(s1).toString();
			}
			UserModel usermodel = UserModel.getCurrentUser(request);
			if(usermodel == null)
			{
				ErrorProcess.Error("You session is invalidate.", out);
				return;
			}
			String s2 = usermodel.getUser_id();
			long l = 0L;
			long l1 = 0L;
			//out.print("    <div align=center>");
			out.print((new StringBuilder()).append("        <b><a href=\"userstatus?user_id=").append(s2).append("\" target=_parent>").append(s2).append("</a></b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n").toString());
			if (s != null)
				out.print((new StringBuilder()).append("        <a href=\"login?action=logout&url=").append(Tool.urlEncode2(s)).append("\">Log Out</a><br>\n").toString());
			else
				out.print("        <a href=\"login?action=logout\">Log Out</a><br>\n");
			boolean flag1 = UserModel.isAdminLoginned(request);

			PreparedStatement preparedstatement = connection.prepareStatement("select new_mail,count(*) as mails from mail where to_user=? and UPPER(defunct)='N' group by new_mail");
			preparedstatement.setString(1, s2);
			for (ResultSet resultset = preparedstatement.executeQuery(); resultset.next();)
			{
				int i = resultset.getInt("new_mail");
				if (i == 0)
					l1 = resultset.getLong("mails");
				else
					l = resultset.getLong("mails");
			}

			out.print((new StringBuilder()).append((new StringBuilder()).append("        <a href=mail target=_parent><font color=").append(l != 0L ? "red" : "blue").append(">Mail:").toString()).append(l + l1).append("(<b>").append(l).append("</b>)</font></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n").toString());
			if (flag1)
				out.print((new StringBuilder()).append("        <b><a href=admin target=_parent>").append("Admin").append("</a></b>\n").toString());
			out.print("        <br>\n        <a href=loginlog>Login Log</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href=Archive>Archive</a>\n");

			//out.print("    </div>");
			preparedstatement.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.print("<html><head><meta http-equiv=\"Pragma\" content=\"no-cache\"><title>Login</title><link href=\"css/oj.css\" type=\"text/css\" rel=\"Stylesheet\" rev=\"Stylesheet\" media=\"all\"/>   <script type=\"text/javascript\" src=\"js/oj.js\"></script></head>");
		String s = request.getParameter("action");
		String s1 = request.getParameter("url");
		if (s1 == null || s1.trim().equals(""))
			s1 = null;
		Connection connection = null;
		try
		{
			if (s != null && s.equals("login"))
			{
				String s2 = request.getParameter("user_id1");
				String s3 = request.getParameter("password1");
				if (!UserModel.login(s2, s3, null, false, request))
				{
					connection = DBConfig.getConn();
					String s5 = "The system has recorded an unsuccessful log-in attempt from ";
					s5 = (new StringBuilder()).append(s5).append(request.getRemoteAddr()).toString();
					String s6 = "System";
					String s7 = "Failed log-in attempt notice";
					/*PreparedStatement preparedstatement = connection.prepareStatement("insert into mail (mail_id,from_user,to_user,title,content,in_date) values(?,?,?,?,?,?)");
					long l = ServerConfig.getNextMailId();
					preparedstatement.setLong(1, l);
					preparedstatement.setString(2, s6);
					preparedstatement.setString(3, s2);
					preparedstatement.setString(4, s7);
					preparedstatement.setString(5, s5);
					preparedstatement.setTimestamp(6, timestamp);
					preparedstatement.executeUpdate();*/
					Tool.sendMail(connection, s6, s2, s7, s5, 0);
					PreparedStatement preparedstatement;
					preparedstatement = connection.prepareStatement("select max(log_id) as log_id from loginlog where user_id=?");
					preparedstatement.setString(1, s2);
					ResultSet resultset = preparedstatement.executeQuery();
					int log_id = 0;
					if (resultset.next())
						log_id = resultset.getInt("log_id");
					preparedstatement = connection.prepareStatement("update loginlog set succeed='N' where log_id=?");
					preparedstatement.setInt(1, log_id);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					out.print("<script language=javascript>");
					out.print("alert(\"Login failed!(Check User_id and Password please.)\")");
					out.print("</script>");
					connection.close();
				} else
				{
					/*servletContext = getServletContext();
					session = request.getSession(true);
					String s4 = request.getParameter("user_id1");
					if (s4 != null)
						session.setAttribute("username", s4);*/
				}
				if (s1 != null)
				{
					Tool.GoToURL(s1, response);
					return;
				}
			}
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		if (s != null && s.equals("logout"))
		{
			UserModel.logout(request);
			servletContext = getServletContext();
			session = request.getSession(true);
			session.invalidate();
			if (s1 != null)
			{
				Tool.GoToURL(s1, response);
				return;
			}
		}
		try
		{
			out.print("<body bgcolor=#F1F1FD link=blue alink=blue vlink=blue>");
			if (UserModel.isLoginned(request))
			{
				connection = DBConfig.getConn();
				showMail(request, out, connection, false);
				connection.close();
				connection = null;
			} else
			{
				out.print(_$3439);
			}
		} catch (Exception exception1)
		{
			exception1.printStackTrace(System.err);
			try
			{
				if (connection != null)
					connection.close();
				connection = null;
			} catch (Exception exception2)
			{
			}
		}
		out.print("</body></html>");
		if (s1 != null)
			Tool.GoToURL(s1, response);
	}

	public void destroy()
	{
	}

}
