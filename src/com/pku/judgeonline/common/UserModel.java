package com.pku.judgeonline.common;

import java.io.Serializable;
import java.sql.*;
import javax.servlet.http.HttpServletRequest;

public class UserModel implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _$1523;
	private String _$1472;
	private String _$1474;
	private long _$1524;
	private boolean _$478;
	private boolean _$1525;
	private boolean _$1526;
	private boolean _$1527;
	private boolean _$1528;
	public static String SESSION_USER_TAG = "SESSION_USER_TAG";

	public UserModel(String s, String s1, String s2, boolean flag, boolean flag1)
	{
		_$1523 = s;
		_$1472 = s1;
		_$478 = flag;
		_$1525 = flag1;
		_$1474 = s2;
		_$1524 = 0L;
		_$1526 = false;
	}

	public UserModel(String s, String s1, String s2, boolean flag, boolean flag1, boolean flag2)
	{
		_$1523 = s;
		_$1472 = s1;
		_$478 = flag;
		_$1525 = flag1;
		_$1527 = flag2;
		_$1474 = s2;
		_$1524 = 0L;
		_$1526 = false;
	}

	public UserModel(String s, String s1, String s2, boolean flag, boolean flag1, boolean flag2, boolean flag3)
	{
		_$1523 = s;
		_$1472 = s1;
		_$478 = flag;
		_$1525 = flag1;
		_$1527 = flag2;
		_$1528 = flag3;
		_$1474 = s2;
		_$1524 = 0L;
		_$1526 = false;
	}

	public static boolean isAdminLoginned(HttpServletRequest request)
	{
		UserModel usermodel = getCurrentUser(request);
		return usermodel != null && usermodel.isAdmin();
	}

	public static boolean isSourceBrowser(HttpServletRequest request)
	{
		UserModel usermodel = getCurrentUser(request);
		return usermodel != null && usermodel._$1525;
	}

	public static boolean isMember(HttpServletRequest request)
	{
		UserModel usermodel = getCurrentUser(request);
		return usermodel != null && usermodel._$1527;
	}

	public static boolean isRoot(HttpServletRequest request)
	{
		UserModel usermodel = getCurrentUser(request);
		return usermodel != null && usermodel._$1528;
	}

	public static boolean isNewsPublisher(HttpServletRequest request)
	{
		UserModel usermodel = getCurrentUser(request);
		return usermodel != null && usermodel._$1526;
	}

	public static boolean isLoginned(HttpServletRequest request)
	{
		if (request == null)
			return false;
		try
		{
			UserModel usermodel = getCurrentUser(request);
			return usermodel != null;
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
		}
		return false;
	}

	public static boolean login(String s, String s1, Connection connection, boolean flag, HttpServletRequest request)
	{
		logout(request);
		if (!ValueCheck.checkId(s))
			return false;
		boolean flag1;
		String s2;
		flag1 = false;
		if (connection == null)
		{
			connection = DBConfig.getConn();
			flag1 = true;
		}
		try
		{
			String info = request.getHeader("user-agent") + "\n" + request.getRemoteHost();
			PreparedStatement preparedstatement = connection.prepareStatement("insert into loginlog (user_id,password,ip,time,info) values(?,encode(?,?),?,?,?)");
			Timestamp timestamp = ServerConfig.getSystemTime();
			preparedstatement.setString(1, s);
			preparedstatement.setString(2, s1);
			preparedstatement.setString(3, ServerConfig.ENCODE_STRING);
			preparedstatement.setString(4, request.getRemoteAddr());
			preparedstatement.setTimestamp(5, timestamp);
			preparedstatement.setString(6, info);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("SELECT user_id,nick FROM users WHERE user_id=? and password=encode(?,?) AND UPPER(defunct) = 'N'");
			preparedstatement.setString(1, s);
			preparedstatement.setString(2, s1);
			preparedstatement.setString(3, ServerConfig.ENCODE_STRING);
			ResultSet resultset = preparedstatement.executeQuery();
			if (resultset.next())
			{
				s = resultset.getString("user_id");
				s2 = resultset.getString("nick");
			} else
			{
				/*
				 * PreparedStatement preparedstatement1 =
				 * connection.prepareStatement
				 * ("UPDATE loginlog set succeed='N' where user_id=? and time=?"
				 * ); preparedstatement1.setString(1, s);
				 * preparedstatement1.setTimestamp(2, timestamp);
				 * preparedstatement1.executeUpdate();
				 * preparedstatement1.close();
				 */
				if (flag1)
					connection.close();
				return false;
			}

			PreparedStatement preparedstatement2 = connection.prepareStatement("UPDATE users SET ip=? , accesstime=? WHERE user_id=?");
			preparedstatement2.setString(1, request.getRemoteAddr());
			preparedstatement2.setTimestamp(2, ServerConfig.getSystemTime());
			preparedstatement2.setString(3, s);
			preparedstatement2.executeUpdate();
			preparedstatement2.close();
			boolean flag2 = false;
			boolean flag5 = false;
			boolean flag6 = false;
			flag = false;
			preparedstatement2 = connection.prepareStatement("select rightstr from privilege where user_id=? and upper(defunct)='N'");
			preparedstatement2.setString(1, s);
			ResultSet resultset1 = preparedstatement2.executeQuery();
			do
			{
				if (!resultset1.next())
					break;
				String s3 = resultset1.getString("rightstr");
				if (s3.equalsIgnoreCase("administrator"))
					flag = true;
				else if (s3.equalsIgnoreCase("source_browser"))
					flag2 = true;
				else if (s3.equalsIgnoreCase("news_publisher"))
				{
				} else if (s3.equalsIgnoreCase("member"))
					flag5 = true;
				else if (s3.equalsIgnoreCase("root"))
					flag = flag6 = true;
			} while (true);
			preparedstatement2.close();
			if (flag1)
				connection.close();
			UserModel usermodel = new UserModel(s, s1, s2, flag, flag2, flag5, flag6);
			request.getSession().setAttribute(SESSION_USER_TAG, usermodel);
			return true;
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
		}
		return false;
	}

	public static void logout(HttpServletRequest request)
	{
		request.getSession().removeAttribute(SESSION_USER_TAG);
	}

	public static UserModel getCurrentUser(HttpServletRequest request)
	{
		return (UserModel) request.getSession().getAttribute(SESSION_USER_TAG);
	}

	public String getPassword()
	{
		return _$1472;
	}

	public String getUser_id()
	{
		return _$1523;
	}

	public String getNick()
	{
		return _$1474;
	}

	public void setNewsPublisher(boolean flag)
	{
		_$1526 = flag;
	}

	public boolean getNewsPublisher()
	{
		return _$1526;
	}

	public long getLastSubmitTime()
	{
		return _$1524;
	}

	public void setLastSubmitTime(long l)
	{
		_$1524 = l;
	}

	public void setUser_id(String s)
	{
		_$1523 = s;
	}

	public void setPassword(String s)
	{
		_$1472 = s;
	}

	public boolean isAdmin()
	{
		return _$478;
	}

	public void setAdmin(boolean flag)
	{
		_$478 = flag;
	}

	public static boolean isUser(HttpServletRequest request, String s)
	{
		UserModel usermodel = getCurrentUser(request);
		if (usermodel == null)
			return false;
		String s1 = usermodel.getUser_id();
		if (s1 == null)
			return false;
		else
			return s1.compareToIgnoreCase(s) == 0;
	}

}
