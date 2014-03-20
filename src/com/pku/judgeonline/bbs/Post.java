package com.pku.judgeonline.bbs;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Post extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
		long l1 = 0L;
		long l2 = 0L;
		long l5 = 0L;
		long l6 = 0L;
		long cid = 0l;
		long pid = 0l;
		Timestamp localTimestamp = new Timestamp(System.currentTimeMillis());
		String str3 = paramHttpServletRequest.getParameter("user_id");
		if (str3 == null)
			str3 = "";
		String str4 = paramHttpServletRequest.getParameter("password");
		if (str4 == null)
			str4 = "";
		if (UserModel.isLoginned(paramHttpServletRequest))
		{
			str3 = UserModel.getCurrentUser(paramHttpServletRequest).getUser_id();
			str4 = UserModel.getCurrentUser(paramHttpServletRequest).getPassword();
		} else if (!UserModel.login(str3, str4, null, false, paramHttpServletRequest))
		{
			ErrorProcess.Error("No such user OR password is not correct", localPrintWriter);
			return;
		}
		String str1 = paramHttpServletRequest.getParameter("title");
		if ((str1 == null) || (str1.trim().equals("")))
		{
			ErrorProcess.Error("Title can't be empty", localPrintWriter);
			return;
		}
		String str2 = paramHttpServletRequest.getParameter("content");
		if (str2 == null)
			str2 = "";
		str2 = str2.trim();
		try
		{
			pid = l1 = Integer.parseInt(paramHttpServletRequest.getParameter("problem_id"));
		} catch (NumberFormatException localNumberFormatException1)
		{
			pid = l1 = 0L;
		}
		try
		{
			cid = Integer.parseInt(paramHttpServletRequest.getParameter("contest_id"));
		} catch (NumberFormatException localNumberFormatException1)
		{
			cid = 0L;
		}
		try
		{
			l2 = Integer.parseInt(paramHttpServletRequest.getParameter("parent_id"));
		} catch (NumberFormatException localNumberFormatException2)
		{
			l2 = 0L;
		}
		Connection localConnection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		ResultSet resultset;
		if (cid != 0l && l1 < 26)
		{
			try
			{
				preparedstatement = localConnection.prepareStatement("select problem_id from contest_problem where contest_id=? and num=?");
				preparedstatement.setLong(1, cid);
				preparedstatement.setLong(2, l1);
				resultset = preparedstatement.executeQuery();
				if (!resultset.next())
				{
					ErrorProcess.Error((new StringBuilder()).append("Can not find contest problem (ID:").append(pid).append(")<br><br>").toString(), localPrintWriter);
					preparedstatement.close();
					localConnection.close();
					return;
				}
				l1 = resultset.getInt("problem_id");
			} catch (Exception exception)
			{

			}
		}
		if ((l1 != 0L) && (!Tool.isProblemExists(l1, localConnection)))
		{
			ErrorProcess.Error("No such problem", localPrintWriter);
			return;
		}
		synchronized (this)
		{
			try
			{
				long l4 = ServerConfig.getNextMessagetId();
				long l3;
				if (l2 == 0L)
				{
					l3 = l4;
				} else
				{
					PreparedStatement localPreparedStatement = localConnection.prepareStatement("select orderNum,depth,thread_id from message where message_id=?");
					localPreparedStatement.setLong(1, l2);
					ResultSet localResultSet = localPreparedStatement.executeQuery();
					if (!localResultSet.next())
					{
						ErrorProcess.Error("No such parent message", localPrintWriter);
						return;
					}
					l6 = localResultSet.getLong("orderNum");
					long l7 = localResultSet.getLong("depth");
					l3 = localResultSet.getLong("thread_id");
					localPreparedStatement.close();
					localPreparedStatement = localConnection.prepareStatement("select depth,orderNum from message where thread_id=? and orderNum>? order by orderNum");
					localPreparedStatement.setLong(1, l3);
					localPreparedStatement.setLong(2, l6);
					localResultSet = localPreparedStatement.executeQuery();
					while (localResultSet.next())
					{
						l5 = localResultSet.getLong("depth");
						if (l5 <= l7)
							break;
						l6 = localResultSet.getLong("orderNum");
					}
					localPreparedStatement.close();
					l5 = l7 + 1L;
					localPreparedStatement = localConnection.prepareStatement("update message set orderNum=orderNum+1 where thread_id=? and orderNum>?");
					localPreparedStatement.setLong(1, l3);
					localPreparedStatement.setLong(2, l6);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					localPreparedStatement = localConnection.prepareStatement("update message set thread_id=? where thread_id=?");
					localPreparedStatement.setLong(1, l4);
					localPreparedStatement.setLong(2, l3);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					l3 = l4;
					l6 += 1L;
				}
				PreparedStatement localPreparedStatement = localConnection.prepareStatement("insert into message (thread_id,message_id,parent_id,orderNum,problem_id,depth,user_id,title,content,in_date) values(?,?,?,?,?,?,?,?,?,?)");
				localPreparedStatement.setLong(1, l3);
				localPreparedStatement.setLong(2, l4);
				localPreparedStatement.setLong(3, l2);
				localPreparedStatement.setLong(4, l6);
				localPreparedStatement.setLong(5, l1);
				localPreparedStatement.setLong(6, l5);
				localPreparedStatement.setString(7, str3);
				localPreparedStatement.setString(8, str1);
				localPreparedStatement.setString(9, str2);
				localPreparedStatement.setTimestamp(10, localTimestamp);
				localPreparedStatement.executeUpdate();
				localPreparedStatement.close();
				localConnection.close();
			} catch (Exception localException1)
			{
				ErrorProcess.ExceptionHandle(localException1, localPrintWriter);
				try
				{
					localConnection.close();
				} catch (Exception localException2)
				{
				}
				return;
			}
		}
		String s4 = "bbs";
		if (l1 != 0L)
		{
			if (cid != 0l)
				s4 += "?problem_id=" + pid + "&contest_id=" + cid;
			else
				s4 = (String) s4 + "?problem_id=" + l1;
		}
		Tool.GoToURL((String) s4, paramHttpServletResponse);
	}

	public void destroy()
	{
	}
}
