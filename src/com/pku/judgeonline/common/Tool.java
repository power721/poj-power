package com.pku.judgeonline.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Tool
{
	public static String getBlankString(Object paramObject)
	{
		if ((paramObject == null) || (paramObject.toString().trim().equals("")))
			return "&nbsp;";
		return paramObject.toString();
	}

	public static boolean isContestStarted(String paramString, Connection paramConnection)
	{
		PreparedStatement localPreparedStatement = null;
		ResultSet localResultSet = null;
		try
		{
			localPreparedStatement = paramConnection.prepareStatement("select contest_id from contest where contest_id=? and start_time<? and UPPER(defunct)='N'");
			localPreparedStatement.setString(1, paramString);
			localPreparedStatement.setTimestamp(2, ServerConfig.getSystemTime());
			localResultSet = localPreparedStatement.executeQuery();
			return localResultSet.next();
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
		}
		return false;
	}

	public static boolean isProblemExists(long paramLong, Connection paramConnection)
	{
		try
		{
			PreparedStatement localPreparedStatement1 = paramConnection.prepareStatement("SELECT *  FROM problem  WHERE problem_id = ? AND UPPER(defunct) = 'N'");
			localPreparedStatement1.setLong(1, paramLong);
			ResultSet localResultSet1 = localPreparedStatement1.executeQuery();
			if (!localResultSet1.next())
			{
				localPreparedStatement1.close();
				return false;
			}
			String str = localResultSet1.getString("contest_id");
			localPreparedStatement1.close();
			int i = 1;
			if (str != null)
			{
				PreparedStatement localPreparedStatement2 = paramConnection.prepareStatement("select * from contest where contest_id=? and UPPER(defunct)='N'");
				localPreparedStatement2.setInt(1, Integer.parseInt(str));
				ResultSet localResultSet2 = localPreparedStatement2.executeQuery();
				if (localResultSet2.next())
				{
					Timestamp localTimestamp = localResultSet2.getTimestamp("start_time");
					i = localTimestamp.getTime() < System.currentTimeMillis() ? 1 : 0;
				}
				localPreparedStatement2.close();
			}
			if (i == 0)
				return false;
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
			return false;
		}
		return true;
	}

	public static void GoToURL(String paramString, HttpServletResponse paramHttpServletResponse)
	{
		try
		{
			paramHttpServletResponse.sendRedirect(paramHttpServletResponse.encodeRedirectURL(paramString));
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
		}
	}

	public static void forwardToUrl(HttpServletRequest paramHttpServletRequest, HttpServletResponse paramHttpServletResponse, String paramString)
	{
		try
		{
			RequestDispatcher localRequestDispatcher = paramHttpServletRequest.getRequestDispatcher(paramString);
			localRequestDispatcher.forward(paramHttpServletRequest, paramHttpServletResponse);
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
		}
	}

	public static String formatTime(long paramLong)
	{
		long l1 = paramLong / 3600L;
		paramLong %= 3600L;
		long l2 = paramLong / 60L;
		paramLong %= 60L;
		return _$2041(l1) + ":" + _$2041(l2) + ":" + _$2041(paramLong);
	}

	private static String _$2041(long paramLong)
	{
		return "0" + paramLong;
	}

	public static void delete(File paramFile)
	{
		String str = ServerConfig.getValue("DeleteTempFile");
		if ((str != null) && (str.toUpperCase().equals("FALSE")))
			return;
		if (paramFile == null)
			return;
		if (paramFile.isFile())
		{
			if (!paramFile.delete())
				ServerConfig.debug("Can not delete file:" + paramFile.getAbsolutePath());
			return;
		}
		if (paramFile.isDirectory())
		{
			File[] arrayOfFile = paramFile.listFiles();
			for (int i = 0; i < arrayOfFile.length; i++)
				delete(arrayOfFile[i]);
			if (!paramFile.delete())
				ServerConfig.debug("Can not delete file:" + paramFile.getAbsolutePath());
		} else
		{
			return;
		}
	}

	public static void copy(String paramString1, String paramString2) throws IOException
	{
		int i = 100000;
		byte[] arrayOfByte = new byte[i];
		FileInputStream localFileInputStream = null;
		FileOutputStream localFileOutputStream = null;
		try
		{
			localFileInputStream = new FileInputStream(paramString1);
			localFileOutputStream = new FileOutputStream(paramString2);
			while (true)
				synchronized (arrayOfByte)
				{
					int j = localFileInputStream.read(arrayOfByte);
					if (j == -1)
						break;
					localFileOutputStream.write(arrayOfByte, 0, j);
				}
		} catch (Exception localException)
		{
			//localException.printStackTrace(ServerConfig.err);
		} finally
		{
			if (localFileInputStream != null)
				localFileInputStream.close();
			if (localFileOutputStream != null)
				localFileOutputStream.close();
		}
	}

	public static String htmlEncode(String paramString)
	{
		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		int i = paramString.length();
		for (int j = 0; j < i; j++)
		{
			char c = paramString.charAt(j);
			int k = 32;
			if (j < i - 1)
				k = paramString.charAt(j + 1);
			if (c == '<')
				localStringBuffer.append("&lt;");
			else if (c == '>')
				localStringBuffer.append("&gt;");
			else if ((c == '&') && (((k >= 97) && (k <= 122)) || ((k >= 65) && (k <= 90))))
				localStringBuffer.append("&#38;");
			else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
		// return "<pre>" + localStringBuffer.toString() + "</pre>";
	}

	public static String titleEncode(String paramString)
	{
		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); i++)
		{
			char c = paramString.charAt(i);
			if (c == '<')
				localStringBuffer.append("&lt;");
			else if (c == '>')
				localStringBuffer.append("&gt;");
			else if (c == '&')
				localStringBuffer.append("&#38;");
			else if (c == '"')
				localStringBuffer.append("&#34;");
			else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
	}

	public static String titleEncode(Connection connection, String user_id, String paramString)
	{
		PreparedStatement preparedstatement;
		try
		{
			preparedstatement = connection.prepareStatement("select rightstr from privilege where user_id=?" + " and upper(defunct)='N' and (rightstr='title' OR rightstr='Administrator' OR rightstr='member')");
			preparedstatement.setString(1, user_id);
			ResultSet resultset = preparedstatement.executeQuery();
			if (resultset.next())
				return paramString;
		} catch (SQLException e)
		{
			e.printStackTrace();
		}

		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); i++)
		{
			char c = paramString.charAt(i);
			if (c == '<')
				localStringBuffer.append("&lt;");
			else if (c == '>')
				localStringBuffer.append("&gt;");
			else if (c == '&')
				localStringBuffer.append("&#38;");
			else if (c == '"')
				localStringBuffer.append("&#34;");
			else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
	}

	public static String urlEncode(String paramString)
	{
		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); i++)
		{
			char c = paramString.charAt(i);
			if (c == '<')
				localStringBuffer.append("&lt;");
			else if (c == '>')
				localStringBuffer.append("&gt;");
			else if (c == '&')
				localStringBuffer.append("&#38;");
			else if (c == '"')
				localStringBuffer.append("&#34;");
			else if (c == ' ')
				localStringBuffer.append("+");
			else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
	}

	public static String urlEncode2(String paramString)
	{
		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); i++)
		{
			char c = paramString.charAt(i);
			if (c == '+')
				localStringBuffer.append("%2B");
			else if (c == '/')
				localStringBuffer.append("%2F");
			else if (c == '%')
				localStringBuffer.append("%25");
			else if (c == '=')
				localStringBuffer.append("%3D");
			else if (c == '?')
				localStringBuffer.append("%3F");
			else if (c == '&')
				localStringBuffer.append("%26");
			else if (c == '#')
				localStringBuffer.append("%23");
			else if (c == ' ')
				localStringBuffer.append("+");
			else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
	}

	public static String dealCompileInfo(String paramString)
	{
		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		paramString = paramString.replaceAll("[c-gC-G]:\\\\[tT][eE][mM][pP]\\\\", "");
		paramString = paramString.replaceAll("[c-gC-G]:[\\\\/]JudgeOnline[\\\\/]", "");
		paramString = paramString.replaceAll("[c-gC-G]:[\\\\/]Python\\d+[\\\\/]", "");// for
		// Python
		for (int i = 0; i < paramString.length(); i++)
		{
			char c = paramString.charAt(i);
			if (c == '\n')
				localStringBuffer.append("<br>");
			else if (c == '<')
				localStringBuffer.append("&lt;");
			else if (c == '>')
				localStringBuffer.append("&gt;");
			else if (c == '&')
				localStringBuffer.append("&amp;");
			else if (c == ' ')
				localStringBuffer.append("&nbsp;");
			else if (c == '\t')
				localStringBuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
	}

	public static String getReplyString(String paramString)
	{
		return "";
		// return "<blockquote>\n"+paramString+"\n</blockquote>\n";
		/*
		 * if ((paramString == null) || (paramString.length() == 0)) return "";
		 * BufferedReader localBufferedReader = new BufferedReader(new
		 * StringReader(paramString)); String str1 = ""; try { while (true) {
		 * String str2 = localBufferedReader.readLine(); if (str2 == null)
		 * break; if ((str2.length() >= 2) && (str2.substring(0,
		 * 2).equals("> "))) continue; str1 = str1 + "> " + str2 + "\n"; } }
		 * catch (IOException localIOException) { return str1; } return str1;
		 */
	}

	public static String gethtmlFormattedString(String paramString)
	{
		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); i++)
		{
			char c = paramString.charAt(i);
			String s = "";
			if (i + 6 < paramString.length())
			{
				s = paramString.substring(i, i + 6);
			}
			if (s.equals("<br />"))
			{
				i += 6;
				continue;
			}
			//if (c == '\n')
			//	localStringBuffer.append("<br>");
			//else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
	}

	public static String getproblemFormattedString(String paramString)
	{
		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); i++)
		{
			char c = paramString.charAt(i);
			if (c == '\n')
				localStringBuffer.append("<br>");
			else if (c == '<')
				localStringBuffer.append("&lt;");
			else if (c == '>')
				localStringBuffer.append("&gt;");
			else if (c == '&')
				localStringBuffer.append("&amp;");
			else if (c == ' ')
				localStringBuffer.append("&nbsp;");
			else if (c == '\t')
				localStringBuffer.append("&nbsp;&nbsp;&nbsp;&nbsp;");
			else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
	}

	public static String formatInputOutput(String paramString)
	{
		if (paramString == null)
			return "&nbsp;";
		StringBuffer localStringBuffer = new StringBuffer();
		for (int i = 0; i < paramString.length(); i++)
		{
			char c = paramString.charAt(i);
			if (c == '\n')
				localStringBuffer.append("<br>");
			if (c == ' ')
				localStringBuffer.append("");
			else
				localStringBuffer.append(c);
		}
		return localStringBuffer.toString();
	}

	public static String encodeUrl(String paramString)
	{
		if (paramString == null)
			return null;
		return paramString.replaceAll("&", "%26");
	}

	public static String fixPath(String paramString)
	{
		return paramString + File.separator;
	}

	public static boolean permission(Connection connection, HttpServletRequest request, long contest_id)
	{
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			preparedstatement = connection.prepareStatement("select start_time,end_time,private from contest where contest_id=?");
			preparedstatement.setLong(1, contest_id);
			resultset = preparedstatement.executeQuery();
			if (resultset.next())
			{
				int i = resultset.getInt("private");
				if (i == 1)
				{
					long l1 = System.currentTimeMillis();
					// boolean flag1 =
					// resultset.getTimestamp("start_time").getTime() >= l1;
					// boolean flag2 =
					// resultset.getTimestamp("start_time").getTime() <= l1;
					boolean flag3 = resultset.getTimestamp("end_time").getTime() <= l1;
					// if (!(flag2 && !flag3))
					if (flag3)
					{
						preparedstatement.close();
						preparedstatement = null;
						return true;
					}
				}
			}
			boolean flaga = UserModel.isAdminLoginned(request);
			if (flaga)
				return true;
			boolean flagb = UserModel.isLoginned(request);
			if (!flagb)
				return false;
			preparedstatement = connection.prepareStatement("select * from private where user_id=? and contest_id=?");
			preparedstatement.setString(1, UserModel.getCurrentUser(request).getUser_id());
			preparedstatement.setLong(2, contest_id);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				preparedstatement.close();
				preparedstatement = null;
				// connection.close();
				// connection = null;
				return false;
			}
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
		}
		return true;
	}

	public static boolean isAdmin(Connection connection, String user_id)
	{
		PreparedStatement preparedstatement;
		ResultSet resultset;
		try
		{
			preparedstatement = connection.prepareStatement("select user_id from privilege where user_id=? and (UPPER(rightstr)='ADMINISTRATOR' or UPPER(rightstr)='ROOT') and UPPER(defunct) = 'N'");
			preparedstatement.setString(1, user_id);
			resultset = preparedstatement.executeQuery();
			if (resultset.next())
			{
				return true;
			}
			return false;
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
		}
		return false;
	}

	public static long getNextProblemID(Connection connection, HttpServletRequest request, long pid) throws SQLException
	{
		PreparedStatement preparedstatement;
		ResultSet resultset;
		boolean flag = UserModel.isAdminLoginned(request);
		String sql = "select problem_id from problem where problem_id>?";
		if (!flag)
			sql += " AND UPPER(defunct) = 'N'";
		sql += " limit 1";
		preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setLong(1, pid);
		resultset = preparedstatement.executeQuery();

		if (!resultset.next())
		{
			preparedstatement.close();
			preparedstatement = null;
			return pid;
		}
		pid = resultset.getLong("problem_id");
		return pid;
	}

	public static long getPreviousProblemID(Connection connection, HttpServletRequest request, long pid) throws SQLException
	{
		PreparedStatement preparedstatement;
		ResultSet resultset;
		boolean flag = UserModel.isAdminLoginned(request);
		String sql = "select problem_id from problem where problem_id<?";
		if (!flag)
			sql += " AND UPPER(defunct) = 'N'";
		sql += " order by problem_id desc limit 1";
		preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setLong(1, pid);
		resultset = preparedstatement.executeQuery();

		if (!resultset.next())
		{
			preparedstatement.close();
			preparedstatement = null;
			return pid;
		}
		pid = resultset.getLong("problem_id");
		return pid;
	}

	public static boolean isContestRunning(Connection connection, long cid) throws SQLException
	{
		PreparedStatement preparedstatement;
		ResultSet resultset;
		String sql = "select contest_id from contest where contest_id=? and start_time<=? and end_time>?";
		preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setLong(1, cid);
		preparedstatement.setTimestamp(2, ServerConfig.getSystemTime());
		preparedstatement.setTimestamp(3, ServerConfig.getSystemTime());
		resultset = preparedstatement.executeQuery();
		if (resultset.next())
		{
			preparedstatement.close();
			preparedstatement = null;
			return true;
		}
		return false;
	}

	public static long problemInRunningContest(Connection connection, long pid) throws SQLException
	{
		PreparedStatement preparedstatement;
		ResultSet resultset;
		String sql = "select contest_id from problem where problem_id=? and contest_id is not null";
		preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setLong(1, pid);
		resultset = preparedstatement.executeQuery();
		if (resultset.next())
		{
			long cid = resultset.getLong("contest_id");
			if (isContestRunning(connection, cid))
				return cid;
		}
		return 0;
	}

	public static long getContestPid(Connection connection, long cid, long pid) throws SQLException
	{
		PreparedStatement preparedstatement;
		ResultSet resultset;
		String sql = "select num from contest_problem where problem_id=? and contest_id=?";
		preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setLong(1, pid);
		preparedstatement.setLong(2, cid);
		resultset = preparedstatement.executeQuery();
		if (resultset.next())
		{
			pid = resultset.getLong("num");
			preparedstatement.close();
			preparedstatement = null;
			return pid;
		}
		return -1;
	}
	
	public static void sendMail(Connection connection, String from_user, String to_user, String title, String content, long reply) throws SQLException
	{
		PreparedStatement preparedstatement = null;
		String user[] = to_user.split(";");
		Set<String> to_users = new HashSet<String>();
		String failed_users = "";
		for (int i = 0; i < user.length; i++)
		{
			to_users.add(user[i]);
		}
		for( Iterator<String> it = to_users.iterator(); it.hasNext(); )
		{
			String user_id = it.next().toString();
			preparedstatement = connection.prepareStatement("select user_id from users where user_id=?");
			preparedstatement.setString(1, user_id);
			ResultSet resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				failed_users += user_id + "; ";
				continue;
			}
			
			preparedstatement = connection.prepareStatement("INSERT INTO mail (from_user,to_user,title,content,reply,in_date) VALUES (?,?,?,?,?,NOW())");
			preparedstatement.setString(1, from_user);
			preparedstatement.setString(2, user_id);
			preparedstatement.setString(3, title);
			preparedstatement.setString(4, content);
			preparedstatement.setLong(5, reply);
			preparedstatement.executeUpdate();
		}
		if(!"".equals(failed_users))
		{
			preparedstatement = connection.prepareStatement("INSERT INTO mail (from_user,to_user,title,content,in_date) VALUES (?,?,?,?,NOW())");
			preparedstatement.setString(1, "System");
			preparedstatement.setString(2, from_user);
			preparedstatement.setString(3, "Failed to send mail to all users.");
			preparedstatement.setString(4, "Cannot send mail to these users: " + failed_users);
			preparedstatement.executeUpdate();
		}
		preparedstatement.close();
	}
	
	/**
	 * 设置cookie
	 * @param response
	 * @param name  cookie名字
	 * @param value cookie值
	 * @param maxAge cookie生命周期  以秒为单位
	 */
	public static void addCookie(HttpServletResponse response, String name,String value, int maxAge){
		Cookie cookie = new Cookie(name, value);
		//cookie.setPath("/");
		if(maxAge > 0)
			cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}
	
	/**
	 * 根据名字获取cookie
	 * @param request
	 * @param name cookie名字
	 * @return
	 */
	public static Cookie getCookieByName(HttpServletRequest request, String name){
		Map<String,Cookie> cookieMap = ReadCookieMap(request);
		if(cookieMap.containsKey(name)){
			Cookie cookie = (Cookie)cookieMap.get(name);
			return cookie;
		}
		return null;
	}


	/**
	 * 将cookie封装到Map里面
	 * @param request
	 * @return
	 */
	private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();
		if(null != cookies){
			for(Cookie cookie : cookies){
				cookieMap.put(cookie.getName(), cookie);
			}
		}
		return cookieMap;
	}
	
	public static boolean freeze_board(Connection connection, long cid) throws SQLException
	{
		PreparedStatement preparedstatement;
		ResultSet resultset;
		String sql = "SELECT 1 FROM contest WHERE contest_id=? AND freeze=1";
		preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setLong(1, cid);
		resultset = preparedstatement.executeQuery();
		if(!resultset.next())
			return false;
		sql = "INSERT INTO freeze_board SELECT * FROM attend WHERE contest_id=?";
		preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setLong(1, cid);
		preparedstatement.executeUpdate();
		
		sql = "UPDATE contest SET freeze=2 WHERE contest_id=?";
		preparedstatement = connection.prepareStatement(sql);
		preparedstatement.setLong(1, cid);
		preparedstatement.executeUpdate();
		return true;
	}
}
