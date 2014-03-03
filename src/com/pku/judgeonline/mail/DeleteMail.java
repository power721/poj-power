package com.pku.judgeonline.mail;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class DeleteMail extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeleteMail()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		java.io.PrintWriter out = response.getWriter();
		if (!UserModel.isLoginned(request))
		{
			ErrorProcess.Error("Please login first .", out);
			return;
		}
		long l = 0L;
		try
		{
			l = Integer.parseInt(request.getParameter("mail_id"));
		} catch (Exception exception)
		{
			ErrorProcess.Error("No such mail", out);
			return;
		}
		Connection connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			preparedstatement = connection.prepareStatement("select mail_id,to_user from mail where mail_id=? and UPPER(defunct)='N'");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error("No such mail", out);
				connection.close();
				return;
			}

			if (!UserModel.isUser(request, resultset.getString("to_user")))
			{
				ErrorProcess.Error("Sorry,invalid access", out);
				connection.close();
				return;
			}
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("update mail set defunct='Y' where mail_id=?");
			preparedstatement.setLong(1, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			connection.close();
			Tool.GoToURL("mail", response);
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
	}

	public void destroy()
	{
	}
}
