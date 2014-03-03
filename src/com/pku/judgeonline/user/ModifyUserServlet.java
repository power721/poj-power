package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ModifyUserServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ModifyUserServlet()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		String s;
		String s1;
		String s2;
		String s3;
		String s4;
		String s5;
		String s6;
		Connection connection;
		PreparedStatement preparedstatement;
		int qq = 0;
		s = request.getParameter("user_id");
		s1 = request.getParameter("oldPassword");
		s2 = request.getParameter("newPassword");
		s3 = request.getParameter("rptPassword");
		s4 = request.getParameter("email");
		s5 = request.getParameter("nick");
		s6 = request.getParameter("school");
		// String s7 = request.getParameter("display");
		try
		{
			qq = Integer.parseInt(request.getParameter("qq"));
		} catch (Exception exception)
		{
			qq = 0;
		}
		// if (!s7.equals("&nbsp;"))
		// i = Integer.parseInt(s7);
		if (s6 == null || s6.trim().equals(""))
			s6 = "";
		connection = DBConfig.getConn();
		try
		{

			preparedstatement = connection.prepareStatement("SELECT * FROM users WHERE UPPER(user_id) = UPPER(?) AND password = encode(?,?) AND UPPER(defunct) = 'N'");
			preparedstatement.setString(1, s);
			preparedstatement.setString(2, s1);
			preparedstatement.setString(3, ServerConfig.ENCODE_STRING);
			ResultSet resultset = preparedstatement.executeQuery();
			if (!resultset.first())
			{
				ErrorProcess.Error((new StringBuilder()).append("The ID( ").append(s).append(" ) is not existed or password is not correct").toString(), out);
				preparedstatement.close();
				connection.close();
				return;
			}
			if (s5 == null || s5.trim().equals(""))
				s5 = s;
			if (s2 == null || s2.equals(""))
				s2 = s3 = s1;
			if (!ValueCheck.checkPassword(s2, out) || !s2.equals(s3))
			{
				connection.close();
				ErrorProcess.Error("Passwords are not match", out);
				return;
			}
			try
			{
				try
				{
					preparedstatement.close();
					if (s5.length() > 100)
						s5 = s5.substring(0, 98);
					preparedstatement = connection.prepareStatement("UPDATE users SET password = encode(?,?) , email = ?,nick=?,school=?,qq=? WHERE user_id = ?");
					preparedstatement.setString(1, s2);
					preparedstatement.setString(2, ServerConfig.ENCODE_STRING);
					preparedstatement.setString(3, s4);
					preparedstatement.setString(4, s5);
					preparedstatement.setString(5, s6);
					preparedstatement.setInt(6, qq);
					preparedstatement.setString(7, s);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					long l = 0L;
					preparedstatement = connection.prepareStatement("select contest_id from contest where start_time<? and end_time>?");
					Timestamp timestamp = ServerConfig.getSystemTime();
					preparedstatement.setTimestamp(1, timestamp);
					preparedstatement.setTimestamp(2, timestamp);
					ResultSet resultset1 = preparedstatement.executeQuery();
					if (resultset1.next())
						l = resultset1.getLong("contest_id");
					preparedstatement.close();
					preparedstatement = connection.prepareStatement("update attend set nick=? where user_id=? and contest_id=?");
					preparedstatement.setString(1, s5);
					preparedstatement.setString(2, s);
					preparedstatement.setLong(3, l);
					preparedstatement.executeUpdate();
					preparedstatement.close();
					FormattedOut.printHead(out, request, connection, "Congratulations");
					connection.close();
					out.println("<p>");
					out.println((new StringBuilder()).append("<img border=\"0\" src=\"").append(ServerConfig.getValue("RootPath")).append("images/j0293240.wmf\" width=\"50\" height=\"36\">").toString());
					out.println("<font size=\"4\">Congratulations</font></p>");
					out.println("<ul>");
					out.println((new StringBuilder()).append("  <li>ID:\t\t").append(s).append("</li>").toString());
					out.println("  <li>Password:\t********</li>");
					out.println((new StringBuilder()).append("  <li>Email:\t\t").append(s4).append("</li>").toString());
					out.println("</ul>");
					FormattedOut.printBottom(request, out);
				} catch (Exception exception1)
				{
					ErrorProcess.ExceptionHandle(exception1, out);
				}
			} catch (Exception exception2)
			{
				ErrorProcess.ExceptionHandle(exception2, out);
			}
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
		}
		return;
	}

	public void destroy()
	{
	}
}
