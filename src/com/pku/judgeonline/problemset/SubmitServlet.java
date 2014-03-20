package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.LanguageType;
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

public class SubmitServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String DATA_EXT_IN = ".in";
	public static final String DATA_EXT_OUT = ".out";
	public static Object attend_mute = new Object();
	public static boolean contest_only = false;
	public static Judge judge;

	public void init() throws ServletException
	{
	}

	public void doPost(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse) throws ServletException, IOException
	{
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		PrintWriter localPrintWriter = paramHttpServletResponse.getWriter();
		long pid = 0, l1, cid = 0L;
		String str;
		try
		{
			String s_p = paramHttpServletRequest.getParameter("problem_id");
			char c = s_p.charAt(0);
			if (c >= 'A' && c <= 'Z')
			{
				l1 = c - 'A';
			} else
				pid = l1 = Integer.parseInt(s_p);
			str = paramHttpServletRequest.getParameter("contest_id");
			if (str != null)
				cid = Integer.parseInt(str);
		} catch (Exception localException1)
		{
			ErrorProcess.Error("Please choose problem", localPrintWriter);
			return;
		}
		int i;
		try
		{
			i = Integer.parseInt(paramHttpServletRequest.getParameter("language"));
		} catch (Exception localException2)
		{
			ErrorProcess.Error("Please choose language", localPrintWriter);
			return;
		}
		if (!LanguageType.isLanguage(i))
		{
			ErrorProcess.Error("Please choose language", localPrintWriter);
			return;
		}
		String str1 = "test";
		String str2 = paramHttpServletRequest.getParameter("source");
		if (str2 == null)
			str2 = "";
		if (str2.length() > 30000)
		{
			ErrorProcess.Error("Source code too long,submit FAILED;if you really need submit this source please contact administrator", localPrintWriter);
			return;
		}
		if (str2.length() < 10)
		{
			ErrorProcess.Error("Source code too short,submit FAILED;if you really need submit this source please contact administrator", localPrintWriter);
			return;
		}
		paramHttpServletResponse.setContentType("text/html; charset=UTF-8");
		paramHttpServletRequest.setCharacterEncoding("UTF-8");
		Connection localConnection = null;
		if (!UserModel.isLoginned(paramHttpServletRequest))
		{
			ErrorProcess.Error("Please login first.", localPrintWriter);
			return;
		}
		UserModel localUserModel = UserModel.getCurrentUser(paramHttpServletRequest);
		Timestamp localTimestamp1 = ServerConfig.getSystemTime();
		if (localUserModel.getLastSubmitTime() + 3000L > localTimestamp1.getTime())
		{
			ErrorProcess.Error("Sorry,please don't submit again within 3 seconds.", localPrintWriter);
			return;
		}
		localUserModel.setLastSubmitTime(localTimestamp1.getTime());
		localConnection = DBConfig.getConn();
		try
		{
			PreparedStatement localPreparedStatement;
			ResultSet localResultSet;
			if (cid != 0 && l1 <= 26)
			{
				localPreparedStatement = localConnection.prepareStatement("select problem_id from contest_problem where contest_id=? and num=?");
				localPreparedStatement.setLong(1, cid);
				localPreparedStatement.setLong(2, l1);
				localResultSet = localPreparedStatement.executeQuery();
				if (!localResultSet.next())
				{
					ErrorProcess.Error((new StringBuilder()).append("Can not find contest problem (ID:").append(l1).append(")<br><br>").toString(), localPrintWriter);
					localPreparedStatement.close();
					localConnection.close();
					return;
				}
				pid = localResultSet.getInt("problem_id");
			}
			String str3 = localUserModel.getUser_id();
			String str4 = localUserModel.getNick();
			localPreparedStatement = localConnection.prepareStatement("select problem.* from problem where problem_id = ? AND UPPER(problem.defunct) = 'N'");
			localPreparedStatement.setLong(1, pid);
			localResultSet = localPreparedStatement.executeQuery();
			if (!localResultSet.next())
			{
				localPreparedStatement.close();
				localConnection.close();
				ErrorProcess.Error("No such problem.", localPrintWriter);
				return;
			}
			String str5 = localResultSet.getString("contest_id");
			String str6 = localResultSet.getString("input_path");
			String str7 = localResultSet.getString("output_path");
			long l2 = localResultSet.getLong("memory_limit");
			long l3 = localResultSet.getLong("time_limit");
			long l4 = localResultSet.getLong("case_time_limit");
			localPreparedStatement.close();
			if (str5 != null)
				str5 = str5.trim().equals("") ? null : str5;
			int j = 1;
			int k = 1;
			int m = 0;
			int n = -1;
			Timestamp localTimestamp2 = new Timestamp(System.currentTimeMillis());
			localTimestamp1 = ServerConfig.getSystemTime();
			if (str5 != null)
			{
				localPreparedStatement = localConnection.prepareStatement("select * from contest where contest_id=? and UPPER(defunct)='N'");
				localPreparedStatement.setInt(1, Integer.parseInt(str5));
				localResultSet = localPreparedStatement.executeQuery();
				if (localResultSet.next())
				{
					localTimestamp2 = localResultSet.getTimestamp("start_time");
					j = localResultSet.getTimestamp("start_time").getTime() <= localTimestamp1.getTime() ? 1 : 0;
					k = localResultSet.getTimestamp("end_time").getTime() < localTimestamp1.getTime() ? 1 : 0;
					m = localResultSet.getInt("private");
					if (j == 0)
					{
						localPreparedStatement.close();
						localConnection.close();
						ErrorProcess.Error("You cannot access this problem!", localPrintWriter);
						return;
					}
				}
				localPreparedStatement.close();
				if (k != 0)
				{
					localPreparedStatement = localConnection.prepareStatement("update problem set contest_id=null where problem_id=?");
					localPreparedStatement.setLong(1, pid);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					str5 = null;
					contest_only = false;
				} else
				{
					localPreparedStatement = localConnection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
					localPreparedStatement.setString(1, str5);
					localPreparedStatement.setLong(2, pid);
					localResultSet = localPreparedStatement.executeQuery();
					if (localResultSet.next())
						n = localResultSet.getInt("num");
					localPreparedStatement.close();
					synchronized (attend_mute)
					{
						localPreparedStatement = localConnection.prepareStatement("SELECT user_id FROM attend WHERE user_id = ? AND contest_id = ?");
						localPreparedStatement.setString(1, str3);
						localPreparedStatement.setInt(2, Integer.parseInt(str5));
						localResultSet = localPreparedStatement.executeQuery();
						if (!localResultSet.next())
						{
							localPreparedStatement.close();
							if (m != 0 && !Tool.permission(localConnection, paramHttpServletRequest, Integer.parseInt(str5)))
							{

								ErrorProcess.Error("<font size=4 color=red>Sorry,this contest isn't a public contest and you are not qualified to attend this contest.<br>If you sure it's wrong,please contact administrator.</font>", localPrintWriter);
								localConnection.close();
								return;
							}
							localPreparedStatement = localConnection.prepareStatement("INSERT INTO attend (user_id,contest_id,nick) VALUES(?,?,?)");
							localPreparedStatement.setString(1, str3);
							localPreparedStatement.setInt(2, Integer.parseInt(str5));
							localPreparedStatement.setString(3, str4);
							localPreparedStatement.executeUpdate();
						}
						localPreparedStatement.close();
					}
				}
			}
			if ((contest_only) && (str5 == null))
			{
				ErrorProcess.Error("<font size=4 color=red>Sorry,only contest problem can submit.Wait until contest ended.<br>If you sure it's wrong,please contact administrator.</font>", localPrintWriter);
				localConnection.close();
				return;
			}
			int i1 = 1;
			long l5 = ServerConfig.getNextSolutionId();

			localPrintWriter.println("<font color=red>" + l5 + "</font>");
			RunRecord localRunRecord = new RunRecord();
			localRunRecord.solution_id = l5;
			localRunRecord.contest_id = str5;
			localRunRecord.ip = paramHttpServletRequest.getRemoteAddr();
			localRunRecord.problem_id = pid;
			localRunRecord.memory_limit = l2;
			localRunRecord.time_limit = l3;
			localRunRecord.case_time_limit = l4;
			localRunRecord.language = i;
			localRunRecord.input_path = str6;
			localRunRecord.output_path = str7;
			localRunRecord.source = str2;
			localRunRecord.start_time = localTimestamp2;
			localRunRecord.submit_time = localTimestamp1;
			localRunRecord.user_id = str3;
			localRunRecord.isRejudge = false;
			localPreparedStatement = localConnection.prepareStatement("INSERT INTO solution (solution_id,problem_id,user_id,in_date,code_length,className,result,language,ip,contest_id,valid,num) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
			int i2 = 1;
			localPreparedStatement.setLong(i2++, l5);
			localPreparedStatement.setLong(i2++, pid);
			localPreparedStatement.setString(i2++, str3);
			localPreparedStatement.setTimestamp(i2++, localRunRecord.submit_time);
			localPreparedStatement.setLong(i2++, str2.length());
			localPreparedStatement.setString(i2++, str1);
			localPreparedStatement.setInt(i2++, 10000);
			localPreparedStatement.setInt(i2++, i);
			localPreparedStatement.setString(i2++, paramHttpServletRequest.getRemoteAddr());
			localPreparedStatement.setString(i2++, str5);
			localPreparedStatement.setInt(i2++, i1);
			localPreparedStatement.setInt(i2++, n);
			localPreparedStatement.executeUpdate();
			localPreparedStatement.close();
			try
			{
				localPreparedStatement = localConnection.prepareStatement("insert into source_code (solution_id,source) values(?,compress(?))");
				localPreparedStatement.setLong(1, l5);
				localPreparedStatement.setString(2, str2);
				localPreparedStatement.executeUpdate();
				localPreparedStatement.close();
			} catch (Exception localException5)
			{
				ErrorProcess.ExceptionHandle(localException5, localPrintWriter);
				try
				{
					localPreparedStatement = localConnection.prepareStatement("delete from solution where solution_id=?");
					localPreparedStatement.setLong(1, l5);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					localConnection.close();
				} catch (Exception localException6)
				{
					ErrorProcess.ExceptionHandle(localException6, localPrintWriter);
				}
				return;
			}
			localConnection.close();
			localConnection = null;
			synchronized (Judge.list)
			{
				Judge.list.add(localRunRecord);
				if (Judge.threads < 1)
				{
					Judge.threads += 1;
					judge = new Judge();
				}
			}
			if (str5 == null)
				Tool.GoToURL("status", paramHttpServletResponse);
			else
				Tool.GoToURL("conteststatus?contest_id=" + str5 + "&user_id=" + str3, paramHttpServletResponse);
			return;
		} catch (Exception localException3)
		{
			ErrorProcess.ExceptionHandle(localException3, localPrintWriter);
			try
			{
				if (localConnection != null)
					localConnection.close();
			} catch (Exception localException4)
			{
			}
		}
	}

	public void destroy()
	{
	}
}
