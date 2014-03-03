package com.pku.judgeonline.common;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class DBConfig
{
	public static boolean inited = false;
	public static DataSource ds = null;

	public static void Init()
	{
		try
		{
			InitialContext localInitialContext = new InitialContext();
			ds = (DataSource) localInitialContext.lookup("java:/comp/env/jdbc/PojDB");
			inited = true;
		} catch (Exception localException)
		{
			System.err.println("DBConfig (): " + localException.getMessage());
			localException.printStackTrace();
		}
	}

	public static Connection getConn()
	{
		if (!inited)
			Init();
		try
		{
			return ds.getConnection();
		} catch (SQLException localSQLException)
		{
			System.err.println("getConn ():" + localSQLException.getMessage());
			localSQLException.printStackTrace(System.err);
			Init();
		}
		return null;
	}
}
