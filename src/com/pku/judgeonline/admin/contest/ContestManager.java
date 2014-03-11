package com.pku.judgeonline.admin.contest;

import com.pku.judgeonline.admin.servlet.LoginServlet;
import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.problemset.Judge;

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

public class ContestManager extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Object contest_mute = new Object();

	public void init() throws ServletException
	{
	}

	public void service(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		if (!UserModel.isAdminLoginned(paramHttpServletRequest))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, paramHttpServletResponse);
			return;
		}
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
		String str1 = "";
		str1 = paramHttpServletRequest.getParameter("action");
		String user_id = paramHttpServletRequest.getParameter("user_id");
		// System.out.println(user_id);
		if ((str1 == null) || ((!str1.equals("add")) && (!str1.equals("del")) && (!str1.equals("user"))))
			return;
		// System.out.println(str1+" user "+user_id);
		Connection localConnection = null;
		PreparedStatement localPreparedStatement1 = null;
		PreparedStatement localPreparedStatement2 = null;
		ResultSet localResultSet = null;
		try
		{
			boolean bool = str1.equals("add");
			boolean flagdel = str1.equals("del");
			// System.out.println("flag:"+flagdel+""+bool);
			long l1 = Long.parseLong(paramHttpServletRequest.getParameter("contest_id"));
			long l2 = -1L;
			long l3 = 0L;
			String problem = paramHttpServletRequest.getParameter("problem_id");
			if (problem != null && !problem.equals(""))
				l3 = Long.parseLong(problem);
			long l4 = System.currentTimeMillis();
			localConnection = DBConfig.getConn();
			localPreparedStatement1 = localConnection.prepareStatement("select start_time,private from contest where contest_id=? and end_time>?");
			localPreparedStatement1.setLong(1, l1);
			localPreparedStatement1.setTimestamp(2, new Timestamp(l4));
			localResultSet = localPreparedStatement1.executeQuery();

			if (!localResultSet.next())
			{
				ErrorProcess.Error("No such contest or contest has ended", localPrintWriter);
				localPreparedStatement1.close();
				localConnection.close();
				return;
			}
			int ii = localResultSet.getInt("private");
			boolean flagc = (boolean) (ii > 0);
			int i = localResultSet.getTimestamp("start_time").getTime() <= l4 ? 1 : 0;
			localPreparedStatement1.close();
			synchronized (contest_mute)
			{

				if (bool)
				{
					localPreparedStatement1 = localConnection.prepareStatement("select num from contest_problem where contest_id=?  and problem_id=?");
					localPreparedStatement1.setLong(1, l1);
					localPreparedStatement1.setLong(2, l3);
					localResultSet = localPreparedStatement1.executeQuery();
					if (localResultSet.next())
					{
						ErrorProcess.Error("The problem already in this contest.", localPrintWriter);
						localPreparedStatement1.close();
						localConnection.close();
						return;
					}
					localPreparedStatement1 = localConnection.prepareStatement("select max(num)+1 as num from contest_problem where contest_id=?");
					localPreparedStatement1.setLong(1, l1);
					localResultSet = localPreparedStatement1.executeQuery();
					if (localResultSet.next())
					{
						if(localResultSet.getInt("num") >= Judge.MAX_CONTEST_PROBLEM_NUM)
						{
							ErrorProcess.Error("There are too many problems in this contest.", localPrintWriter);
							localPreparedStatement1.close();
							localConnection.close();
							return;
						}
					}
					localPreparedStatement1 = localConnection.prepareStatement("select title from problem where problem_id=?  and UPPER(defunct) = 'N'");
					localPreparedStatement1.setLong(1, l3);
					localResultSet = localPreparedStatement1.executeQuery();
					if (!localResultSet.next())
					{
						ErrorProcess.Error("No such problem", localPrintWriter);
						localPreparedStatement1.close();
						localConnection.close();
						return;
					}
					String str2 = paramHttpServletRequest.getParameter("ptitle");
					localPreparedStatement1.close();
					localPreparedStatement1 = localConnection.prepareStatement("update problem set contest_id=? where problem_id=?");
					localPreparedStatement1.setLong(1, l1);
					localPreparedStatement1.setLong(2, l3);
					localPreparedStatement1.executeUpdate();
					localPreparedStatement1.close();
					int j = 0;
					localPreparedStatement1 = localConnection.prepareStatement("insert into contest_problem (contest_id,problem_id,title,num) values(?,?,?,?)");
					j = 1;
					localPreparedStatement1.setLong(j++, l1);
					localPreparedStatement1.setLong(j++, l3);
					localPreparedStatement1.setString(j++, str2);
					localPreparedStatement1.setInt(j++, 999);
					localPreparedStatement1.executeUpdate();
					localPreparedStatement1.close();
					localPreparedStatement1 = localConnection.prepareStatement("select problem_id from contest_problem where contest_id=? order by num");
					localPreparedStatement1.setLong(1, l1);
					localResultSet = localPreparedStatement1.executeQuery();
					j = 0;
					while (localResultSet.next())
					{
						localPreparedStatement2 = localConnection.prepareStatement("update contest_problem set num=? where contest_id=? and problem_id=?");
						localPreparedStatement2.setInt(1, j++);
						localPreparedStatement2.setLong(2, l1);
						localPreparedStatement2.setLong(3, localResultSet.getLong("problem_id"));
						localPreparedStatement2.executeUpdate();
						localPreparedStatement2.close();
					}
					localPreparedStatement1.close();
				} else if (flagdel)
				{
					if (i != 0)
					{
						ErrorProcess.Error("Contest has started,can't delete problem now", localPrintWriter);
						localConnection.close();
						return;
					}
					localPreparedStatement1 = localConnection.prepareStatement("update problem set contest_id=null where problem_id=?");
					localPreparedStatement1.setLong(1, l3);
					localPreparedStatement1.executeUpdate();
					localPreparedStatement1.close();
					localPreparedStatement1 = localConnection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
					localPreparedStatement1.setLong(1, l1);
					localPreparedStatement1.setLong(2, l3);
					localResultSet = localPreparedStatement1.executeQuery();
					if (localResultSet.next())
					{
						l2 = localResultSet.getLong("num");
					} else
					{
						ErrorProcess.Error("Problem " + l3 + " is not in this contest!", localPrintWriter);
						localResultSet.close();
						localPreparedStatement1.close();
						localConnection.close();
						return;
					}
					localPreparedStatement1.close();
					localPreparedStatement1 = localConnection.prepareStatement("delete from contest_problem where contest_id=? and num=?");
					localPreparedStatement1.setLong(1, l1);
					localPreparedStatement1.setLong(2, l2);
					localPreparedStatement1.executeUpdate();
					localPreparedStatement1.close();
					localPreparedStatement1 = localConnection.prepareStatement("update contest_problem set num=num-1 where contest_id=? and num>?");
					localPreparedStatement1.setLong(1, l1);
					localPreparedStatement1.setLong(2, l2);
					localPreparedStatement1.executeUpdate();
					localPreparedStatement1.close();
					localResultSet.close();
				} else
				{
					// System.out.println(user_id);
					if (!flagc)
					{
						ErrorProcess.Error("It's not a private contest.", localPrintWriter);
						localConnection.close();
						return;
					}
					localPreparedStatement1 = localConnection.prepareStatement("select * from users where user_id=?");
					localPreparedStatement1.setString(1, user_id);
					localResultSet = localPreparedStatement1.executeQuery();
					if (!localResultSet.next())
					{
						ErrorProcess.Error("Can't find the user_id.", localPrintWriter);
						localResultSet.close();
						localPreparedStatement1.close();
						localConnection.close();
						return;
					}
					localResultSet.close();
					localPreparedStatement1.close();
					
					localPreparedStatement1 = localConnection.prepareStatement("select * from private where user_id=? and contest_id=?");
					localPreparedStatement1.setString(1, user_id);
					localPreparedStatement1.setLong(2, l1);
					localResultSet = localPreparedStatement1.executeQuery();
					if (localResultSet.next())
					{
						ErrorProcess.Error("The user is already has permission.", localPrintWriter);
						localConnection.close();
						return;
					}
					localPreparedStatement1 = localConnection.prepareStatement("insert into private (contest_id,user_id) values(?,?)");
					localPreparedStatement1.setLong(1, l1);
					localPreparedStatement1.setString(2, user_id);
					localPreparedStatement1.executeUpdate();
					localPreparedStatement1.close();
					ErrorProcess.Error("<font color=red size=16>The user can attend this contest now.</font>", localPrintWriter);
					localResultSet.close();
					localPreparedStatement1.close();
					localConnection.close();
					return;
				}
			}
			localConnection.close();
			Tool.GoToURL("admin.contestmanagerpage?contest_id=" + l1, paramHttpServletResponse);
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
		}
		localPrintWriter.close();
	}

	public void destroy()
	{
	}
}
