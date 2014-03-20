package com.pku.judgeonline.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Properties;

public class ServerConfig
{
	public static long startTimestamp;
	private static Properties _$1612;
	@SuppressWarnings("unused") private static Properties _$1613;
	private static String _$1614 = "C:\\judgeonline\\server.config";
	private static String _$1615 = "/etc/OJserverconfig.property";
	public static String ENCODE_STRING = "PWDforJO2005";
	public static String VALIDATE_FILE_NAME = "validate.exe";
	public static int MAX_PROBLEM_ID = 10000;
	public static String DesignerName = "Power721";
	public static String DesignerEmail = "power721@163.com";
	public static String DeveloperName = "Power721";
	public static String SYSTEM_INFO = null;
	public static String DEFAULT_TITLE = null;
	@SuppressWarnings("unused") private static boolean _$1502 = false;
	private static boolean _$998 = false;
	private static long _$1621 = 0L;
	private static long _$1622 = 0L;
	private static long _$1623 = 0L;
	private static long _$1624 = 0L;
	private static long _$1625 = 0L;
	private static long _$1626 = 0L;
	private static long _$1627 = 0L;
	private static long _$1699 = 0L;
	public static PrintStream out = null;
	public static PrintStream err = null;
	private static String _$1637 = null;
	private static String _$1638 = null;

	public static void init()
	{
		_$1628();
		_$1629();
	}

	private static synchronized void _$1629()
	{
		Connection localConnection = DBConfig.getConn();
		try
		{
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(solution_id) as maxp from solution");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1623 = localResultSet.getInt("maxp") + 1;
				if (_$1623 < 1000L)
					_$1623 = 1000L;
			} else
			{
				_$1623 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();
		} catch (Exception localException1)
		{
			try
			{
				localConnection.close();
			} catch (Exception localException2)
			{
			}
			localException1.printStackTrace(System.err);
		}
	}

	private static synchronized void _$1628()
	{
		try
		{
			String os = System.getProperty("os.name").toLowerCase();
			//System.out.println(os);
			_$1612 = new Properties();
			if (os.startsWith("windows"))
				_$1612.load(new FileInputStream(_$1614));
			else if (os.startsWith("linux"))
				_$1612.load(new FileInputStream(_$1615));
			_$1613 = new Properties();
			String str1 = _$1612.getProperty("Debug");
			_$998 = (str1 != null) && (str1.toUpperCase().equals("TRUE"));
			if (_$998)
			{
				str1 = _$1612.getProperty("DebugFile");
				if (str1 != null)
					out = new PrintStream(new FileOutputStream(str1), true);
			}
			String str2 = _$1612.getProperty("ErrFile");
			if (str2 != null)
				err = new PrintStream(new FileOutputStream(str2), true);
			_$1637 = _$1612.getProperty("RunShell");
			_$1638 = _$1612.getProperty("ComShell");
			DEFAULT_TITLE = _$1612.getProperty("DefaultTitle");
		} catch (FileNotFoundException localFileNotFoundException)
		{
			localFileNotFoundException.printStackTrace();
		} catch (IOException localIOException)
		{
			localIOException.printStackTrace(System.err);
		} finally
		{
			_$1502 = true;
		}
	}

	public static String getValue(String paramString)
	{
		String str = _$1612.getProperty(paramString);
		return str == null ? null : str;
	}

	public static void debug(Object paramObject)
	{
		if ((_$998) && (out != null))
			out.println("[Debug: " + new Date() + "]  " + paramObject);
	}

	public static void err(Object paramObject)
	{
		if (err != null)
			err.println(paramObject);
	}

	public static synchronized long getNextProblemId()
	{
		try
		{
			Connection localConnection = DBConfig.getConn();
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(problem_id) as maxp from problem");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1622 = localResultSet.getInt("maxp") + 1;
				if (_$1622 < 1000L)
					_$1622 = 1000L;
			} else
			{
				_$1622 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();

			return _$1622++;
		} catch (Exception localException)
		{
		}
		return -1L;
	}

	public static synchronized long getNextDiscussId()
	{
		try
		{
			Connection localConnection = DBConfig.getConn();
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(discuss_id) as maxp from discuss");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1624 = localResultSet.getInt("maxp") + 1;
				if (_$1624 < 1000L)
					_$1624 = 1000L;
			} else
			{
				_$1624 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();

			return _$1624++;
		} catch (Exception localException)
		{
		}
		return -1L;
	}

	public static synchronized long getNextSolutionId()
	{
		try
		{
			Connection localConnection = DBConfig.getConn();
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(solution_id) as maxp from solution");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1623 = localResultSet.getInt("maxp") + 1;
				if (_$1623 < 1000L)
					_$1623 = 1000L;
			} else
			{
				_$1623 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();

			return _$1623++;
		} catch (Exception localException)
		{
		}
		return -1L;
	}

	public static long getCurrSolutionId()
	{
		return _$1623;
	}

	public static synchronized long getNextContestId()
	{
		try
		{
			Connection localConnection = DBConfig.getConn();
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(contest_id) as maxp from contest");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1621 = localResultSet.getInt("maxp") + 1;
				if (_$1621 < 1000L)
					_$1621 = 1000L;
			} else
			{
				return ServerConfig._$1621 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();

			return _$1621++;
		} catch (Exception localException)
		{
		}
		return -1L;
	}

	public static synchronized long getNextThreadId()
	{
		try
		{
			Connection localConnection = DBConfig.getConn();
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(thread_id) as maxp from message");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1625 = localResultSet.getInt("maxp") + 1;
				if (_$1625 < 1000L)
					_$1625 = 1000L;
			} else
			{
				return ServerConfig._$1625 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();

			return _$1625++;
		} catch (Exception localException)
		{
		}
		return -1L;
	}

	public static synchronized long getNextMessagetId()
	{
		try
		{
			Connection localConnection = DBConfig.getConn();
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(message_id) as maxp from message");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1626 = localResultSet.getInt("maxp") + 1;
				if (_$1626 < 1000L)
					_$1626 = 1000L;
			} else
			{
				_$1626 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();

			return _$1626++;
		} catch (Exception localException)
		{
		}
		return -1L;
	}

	public static synchronized long getNextMailId()
	{
		try
		{
			Connection localConnection = DBConfig.getConn();
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(mail_id) as maxp from mail");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1627 = localResultSet.getInt("maxp") + 1;
				if (_$1627 < 1000L)
					_$1627 = 1000L;
			} else
			{
				_$1627 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();

			return _$1627++;
		} catch (Exception localException)
		{
		}
		return -1L;
	}

	public static synchronized long getNextAnnounceId()
	{
		try
		{
			Connection localConnection = DBConfig.getConn();
			PreparedStatement localPreparedStatement = localConnection.prepareStatement("select max(id) as maxp from announce");
			ResultSet localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
			{
				_$1699 = localResultSet.getInt("maxp") + 1;
				if (_$1699 < 1000L)
					_$1699 = 1000L;
			} else
			{
				_$1699 = 1000L;
			}
			localPreparedStatement.close();
			localConnection.close();
			return _$1699++;
		} catch (Exception localException)
		{
		}
		return -1L;
	}

	public static String getRunShell()
	{
		return _$1637;
	}

	public static String getComShell()
	{
		return _$1638;
	}

	public static Timestamp getSystemTime()
	{
		return new Timestamp(System.currentTimeMillis());
	}
}
