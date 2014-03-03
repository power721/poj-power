package com.pku.judgeonline.mail;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Send extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Send()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		if (!UserModel.isLoginned(request))
		{
			ErrorProcess.Error("Please login first .", out);
			return;
		}
		String s = request.getParameter("title");
		if (s != null)
			s.trim();
		String s1 = request.getParameter("to");
		if (s1 != null)
			s1.trim();
		String s2 = UserModel.getCurrentUser(request).getUser_id();
		boolean flags = UserModel.isAdminLoginned(request);
		String s3 = (request.getParameter("content"));
		if (!flags)
			s3 = Tool.titleEncode(s3);
		if (s3 != null)
			s3.trim();
		else
			s3 = "";
		if (s == null || s.equals(""))
			s = "No Topic";
		if (s3.length() > 40000)
		{
			ErrorProcess.Error("Sorry,content too long", out);
			return;
		}
		long reply = 0L;
		try
		{
			reply = Integer.parseInt(request.getParameter("reply"));
		} catch (NumberFormatException localNumberFormatException)
		{
			reply = 0L;
		}
		Connection connection;
		PreparedStatement preparedstatement;
		ResultSet resultset;
		//Timestamp timestamp;
		//long l;
		try
		{
			connection = DBConfig.getConn();
			/*preparedstatement = connection.prepareStatement("select user_id from users where user_id=?");
			preparedstatement.setString(1, s1);
			ResultSet resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error((new StringBuilder()).append("Sorry,no such user:").append(s1).toString(), out);
				return;
			} else*/
			//{
				String ss = ServerConfig.getValue("Mail");
				if (ss != null && ss.equals("FALSE") && !UserModel.isAdminLoginned(request))
				{
					preparedstatement = connection.prepareStatement("select rightstr from privilege where user_id=? and upper(defunct)='N'");
					preparedstatement.setString(1, s1);
					resultset = preparedstatement.executeQuery();
					boolean flag4 = true;
					if (resultset.next())
					{
						String s9 = resultset.getString("rightstr");
						if (s9.equalsIgnoreCase("administrator"))
							flag4 = false;
					}
					if (flag4)
					{
						ErrorProcess.Error("You can only send mail to administrators now.", out);
						return;
					}
				}
			//}
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		try
		{
			//preparedstatement.close();
			/*timestamp = new Timestamp(System.currentTimeMillis());
			preparedstatement = connection.prepareStatement("insert into mail (mail_id,from_user,to_user,title,content,in_date) values(?,?,?,?,?,?)");
			l = ServerConfig.getNextMailId();
			preparedstatement.setLong(1, l);
			preparedstatement.setString(2, s2);
			preparedstatement.setString(3, s1);
			preparedstatement.setString(4, s);
			preparedstatement.setString(5, s3);
			preparedstatement.setTimestamp(6, timestamp);
			preparedstatement.executeUpdate();
			preparedstatement.close();*/
			Tool.sendMail(connection, s2, s1, s, s3, reply);
			FormattedOut.printHead(out, request, connection, "Send mail");
			connection.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		out.println("<div id=send_mail class=mail align=center>Send successfully<br>");
		out.println("<a href=mail><font color=blue>Go Back</font></a> to mail list</a></a>");
		FormattedOut.printBottom(request, out);
	}

	public void destroy()
	{
	}
}
