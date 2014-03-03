package com.pku.judgeonline.bbs;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ResumeMessage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResumeMessage()
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
		boolean flag = UserModel.isAdminLoginned(request);
		int i;
		try
		{
			i = Integer.parseInt(request.getParameter("message_id"));
		} catch (NumberFormatException numberformatexception)
		{
			ErrorProcess.Error("No such message", out);
			i = 0;
			return;
		}
		long l = i;
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("select * from message where message_id=? and  UPPER(defunct) != 'N'");
			preparedstatement.setLong(1, l);
			ResultSet resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				preparedstatement.close();
				connection.close();
				ErrorProcess.Error("No such message", out);
				return;
			}
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		Connection connection1;
		PreparedStatement preparedstatement1;
		try
		{
			if (!flag)
			{
				ErrorProcess.Error("The requested message does not exist or you are not privileged to edit it. ", out);
				return;
			}

			connection1 = DBConfig.getConn();
			preparedstatement1 = connection1.prepareStatement(" update message set defunct='N' where message_id=?");
			preparedstatement1.setInt(1, i);
			preparedstatement1.executeUpdate();
			preparedstatement1.close();
			connection1.close();
			Tool.GoToURL("bbs", response);
			return;
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
