package com.pku.judgeonline.bbs;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class DelMessage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DelMessage()
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
		long l2 = 0l;
		long l3 = 0l;

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
			PreparedStatement preparedstatement = connection.prepareStatement("select * from message where message_id=?");
			preparedstatement.setLong(1, l);
			ResultSet resultset = preparedstatement.executeQuery();

			if (!resultset.next())
			{
				preparedstatement.close();
				connection.close();
				ErrorProcess.Error("No such message", out);
				return;
			} else
				l2 = resultset.getLong("thread_id");
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		try
		{
			if (!flag)
			{
				ErrorProcess.Error("The requested message does not exist or you are not privileged to edit it. ", out);
				return;
			}
		} catch (Exception exception2)
		{
			ErrorProcess.ExceptionHandle(exception2, out);
			return;
		}

		try
		{
			PreparedStatement preparedstatement = null;
			ResultSet resultset = null;
			Connection connection = null;
			connection = DBConfig.getConn();
			preparedstatement = connection.prepareStatement(" delete from message where message_id=?");
			preparedstatement.setInt(1, i);
			preparedstatement.executeUpdate();
			preparedstatement.close();

			preparedstatement = connection.prepareStatement("select max(message_id) as new_id from message where thread_id=?");
			preparedstatement.setLong(1, l2);
			resultset = preparedstatement.executeQuery();
			if (resultset.next())
				l3 = resultset.getLong("new_id");
			preparedstatement.close();
			if (l3 != 0l)
			{
				preparedstatement = connection.prepareStatement("update message set thread_id=? where thread_id=?");
				preparedstatement.setLong(1, l3);
				preparedstatement.setLong(2, l2);
				preparedstatement.executeUpdate();
				preparedstatement.close();
			}
			connection.close();
		} catch (Exception exception1)
		{
			ErrorProcess.ExceptionHandle(exception1, out);
			return;
		}
		Tool.GoToURL("bbs", response);
		return;
	}

	public void destroy()
	{
	}
}
