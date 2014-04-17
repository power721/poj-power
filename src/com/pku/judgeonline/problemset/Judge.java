package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.LanguageType;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.servlet.Rejudge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Judge extends Thread
{
	public static final int MAX_THREAD = 1;
	public static int MAX_CONTEST_PROBLEM_NUM = 15;
	public static int threads = 0;
	public static Object mute = new Object();
	public static ArrayList<RunRecord> list = new ArrayList<RunRecord>();
	private Connection _$1537 = null;
	public static final String DATA_EXT_IN = ".in";
	public static final String DATA_EXT_OUT = ".out";

	public Judge()
	{
		start();
	}

	public static boolean havePass(Connection paramConnection, RunRecord paramRunRecord) throws Exception
	{
		PreparedStatement localPreparedStatement = paramConnection.prepareStatement("select solution_id from solution where user_id=? and problem_id=? and result=0 and solution_id<? limit 1");
		localPreparedStatement.setString(1, paramRunRecord.user_id);
		localPreparedStatement.setLong(2, paramRunRecord.problem_id);
		localPreparedStatement.setLong(3, paramRunRecord.solution_id);
		ResultSet localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
		{
			localPreparedStatement.close();
			return false;
		}
		localPreparedStatement.close();
		return true;
	}

	public static boolean contest_havePass(Connection paramConnection, RunRecord paramRunRecord) throws Exception
	{
		PreparedStatement localPreparedStatement = paramConnection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
		localPreparedStatement.setString(1, paramRunRecord.contest_id);
		localPreparedStatement.setLong(2, paramRunRecord.problem_id);
		ResultSet localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
			return true;
		int i = localResultSet.getInt("num");
		localPreparedStatement.close();
		char c = (char) (65 + i);
		String str = "" + c + "_time";
		localPreparedStatement = paramConnection.prepareStatement("select " + c + "_time from attend where contest_id=? and user_id=?");
		localPreparedStatement.setString(1, paramRunRecord.contest_id);
		localPreparedStatement.setString(2, paramRunRecord.user_id);
		localResultSet = localPreparedStatement.executeQuery();
		if ((localResultSet.next()) && (localResultSet.getLong(str) > 0L))
		{
			localPreparedStatement.close();
			return true;
		}
		localPreparedStatement.close();
		return false;
	}

	public static void submit(Connection paramConnection, RunRecord paramRunRecord) throws Exception
	{
		PreparedStatement localPreparedStatement = paramConnection.prepareStatement("UPDATE users SET submit = submit+1,language=? where user_id =?");
		localPreparedStatement.setInt(1, paramRunRecord.language);
		localPreparedStatement.setString(2, paramRunRecord.user_id);
		localPreparedStatement.executeUpdate();
		localPreparedStatement.close();
		localPreparedStatement = paramConnection.prepareStatement("UPDATE problem SET submit = submit+1,ratio=100*accepted/submit,in_date=? where problem_id=?");
		localPreparedStatement.setTimestamp(1, paramRunRecord.submit_time);
		localPreparedStatement.setLong(2, paramRunRecord.problem_id);
		localPreparedStatement.executeUpdate();
		localPreparedStatement.close();
		boolean bool = false;
		localPreparedStatement = paramConnection.prepareStatement("select solution_id from solution where user_id=? and problem_id=? and solution_id<? limit 1");
		localPreparedStatement.setString(1, paramRunRecord.user_id);
		localPreparedStatement.setLong(2, paramRunRecord.problem_id);
		localPreparedStatement.setLong(3, paramRunRecord.solution_id);
		ResultSet localResultSet = localPreparedStatement.executeQuery();
		bool = localResultSet.next();
		localPreparedStatement.close();
		if (!bool)
		{
			localPreparedStatement = paramConnection.prepareStatement("UPDATE problem SET submit_user = submit_user+1 where problem_id=?");
			localPreparedStatement.setLong(1, paramRunRecord.problem_id);
			localPreparedStatement.executeUpdate();
			localPreparedStatement.close();
		}
	}

	public static void contest_Submit(Connection paramConnection, RunRecord paramRunRecord, boolean paramBoolean) throws Exception
	{
		PreparedStatement localPreparedStatement = paramConnection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
		localPreparedStatement.setString(1, paramRunRecord.contest_id);
		localPreparedStatement.setLong(2, paramRunRecord.problem_id);
		ResultSet localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
			return;
		int i = localResultSet.getInt("num");
		localPreparedStatement.close();
		if (i > MAX_CONTEST_PROBLEM_NUM)
			return;
		char c = (char) (65 + i);
		if (paramBoolean)
		{
			String str = "" + c + "_WrongSubmits";
			localPreparedStatement = paramConnection.prepareStatement("update attend set " + c + "_time=?,accepts=accepts+1,penalty=penalty+?+" + str + "*1200 where contest_id=? and user_id=?");
			localPreparedStatement.setLong(1, (paramRunRecord.submit_time.getTime() - paramRunRecord.start_time.getTime()) / 1000L);
			localPreparedStatement.setLong(2, (paramRunRecord.submit_time.getTime() - paramRunRecord.start_time.getTime()) / 1000L);
			localPreparedStatement.setString(3, paramRunRecord.contest_id);
			localPreparedStatement.setString(4, paramRunRecord.user_id);
			localPreparedStatement.executeUpdate();
			localPreparedStatement.close();
		} else
		{
			localPreparedStatement = paramConnection.prepareStatement("update attend set " + c + "_WrongSubmits=" + c + "_WrongSubmits+1 where contest_id=? and user_id=?");
			localPreparedStatement.setString(1, paramRunRecord.contest_id);
			localPreparedStatement.setString(2, paramRunRecord.user_id);
			localPreparedStatement.executeUpdate();
			localPreparedStatement.close();
		}
	}

	public void run()
	{
		this._$1537 = DBConfig.getConn();
		RunRecord localRunRecord;
		try
		{
			while (true)
			{
				synchronized (list)
				{
					if (list.isEmpty())
					// continue;
					// else
					{
						this._$1537.close();
						threads = threads > 0 ? threads - 1 : 0;
						return;
					}
					localRunRecord = (RunRecord) list.get(0);
					list.remove(0);
				}
				synchronized (Rejudge.rejudge_mute)
				{
					if (Compile(localRunRecord, this._$1537))
						RunProcess(localRunRecord, this._$1537);
				}
			}
		} catch (Exception localException)
		{
			threads = threads > 0 ? threads - 1 : 0;
			localException.printStackTrace(System.err);
		}
	}

	public static void ReJudge(long paramLong1, long paramLong2, Connection paramConnection) throws Exception
	{
		RunRecord localRunRecord = new RunRecord();
		localRunRecord.solution_id = paramLong1;
		File localFile = new File(Tool.fixPath(ServerConfig.getValue("WorkingPath")) + localRunRecord.solution_id);
		Tool.delete(localFile);
		localRunRecord.isRejudge = true;
		PreparedStatement localPreparedStatement = paramConnection.prepareStatement("select * from solution where solution_id=?");
		localPreparedStatement.setLong(1, paramLong1);
		ResultSet localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
			return;
		if (paramLong2 != 0L)
			localRunRecord.contest_id = localResultSet.getString("contest_id");
		else
			localRunRecord.contest_id = null;
		localRunRecord.language = localResultSet.getInt("language");
		localRunRecord.problem_id = localResultSet.getLong("problem_id");
		localRunRecord.user_id = localResultSet.getString("user_id");
		localRunRecord.submit_time = localResultSet.getTimestamp("in_date");
		localPreparedStatement.close();
		localPreparedStatement = paramConnection.prepareStatement("select uncompress(source) as source from source_code where solution_id=?");
		localPreparedStatement.setLong(1, paramLong1);
		localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
			return;
		localRunRecord.source = localResultSet.getString("source");
		localPreparedStatement.close();
		if (localRunRecord.contest_id != null)
		{
			localPreparedStatement = paramConnection.prepareStatement("select * from contest where contest_id=?");
			localPreparedStatement.setString(1, localRunRecord.contest_id);
			localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
				localRunRecord.start_time = localResultSet.getTimestamp("start_time");
			localPreparedStatement.close();
		}
		localPreparedStatement = paramConnection.prepareStatement("select input_path,output_path,memory_limit,time_limit,case_time_limit from problem where problem_id=?");
		localPreparedStatement.setLong(1, localRunRecord.problem_id);
		localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
			return;
		localRunRecord.input_path = localResultSet.getString("input_path");
		localRunRecord.output_path = localResultSet.getString("output_path");
		localRunRecord.memory_limit = localResultSet.getLong("memory_limit");
		localRunRecord.time_limit = localResultSet.getLong("time_limit");
		localRunRecord.case_time_limit = localResultSet.getLong("case_time_limit");
		synchronized (Rejudge.rejudge_mute)
		{
			if (Compile(localRunRecord, paramConnection))
				RunProcess(localRunRecord, paramConnection);
		}
		localPreparedStatement.close();
		Tool.delete(localFile);
	}

	public static boolean RunProcess(RunRecord paramRunRecord, Connection paramConnection) throws Exception
	{
		Process localProcess = Runtime.getRuntime().exec(ServerConfig.getRunShell());
		OutputStream localOutputStream = localProcess.getOutputStream();
		File localFile1 = new File(paramRunRecord.input_path);
		if (!localFile1.isDirectory())
		{
			System.err.println(paramRunRecord.input_path + "not exists");
			return false;
		}
		File localFile2 = new File(paramRunRecord.output_path);
		if (!localFile2.isDirectory())
		{
			System.err.println(paramRunRecord.output_path + "not exists");
			return false;
		}
		ArrayList<String> localArrayList1 = new ArrayList<String>();
		ArrayList<String> localArrayList2 = new ArrayList<String>();
		File[] arrayOfFile = localFile1.listFiles();
		Object localObject1;
		for (int i = 0; i < arrayOfFile.length; i++)
		{
			File localFile3 = arrayOfFile[i];
			if (!localFile3.getName().toLowerCase().endsWith(DATA_EXT_IN))
				continue;
			localObject1 = new File(Tool.fixPath(localFile2.getAbsolutePath()) + localFile3.getName().substring(0, localFile3.getName().length() - DATA_EXT_IN.length()) + DATA_EXT_OUT);
			if (!((File) localObject1).isFile())
				continue;
			localArrayList1.add(localFile3.getAbsolutePath());
			localArrayList2.add(((File) localObject1).getAbsolutePath());
		}
		int i = localArrayList1.size();
		localOutputStream.write((paramRunRecord.time_limit * LanguageType.getTimeFactor(paramRunRecord.language) + i * LanguageType.getExtTime(paramRunRecord.language) + "\n").getBytes());
		localOutputStream.write((paramRunRecord.case_time_limit * LanguageType.getTimeFactor(paramRunRecord.language) + LanguageType.getExtTime(paramRunRecord.language) + "\n").getBytes());
		ServerConfig.debug(paramRunRecord.time_limit + "");
		File localFile3 = new File(Tool.fixPath(ServerConfig.getValue("WorkingPath")) + paramRunRecord.solution_id);
		try
		{
			localObject1 = "Main";
			long l1 = LanguageType.getExtMemory(paramRunRecord.language);
			localOutputStream.write(((paramRunRecord.memory_limit + l1) * 1024L + "\n").getBytes());
			ServerConfig.debug("Memory Limit: " + (paramRunRecord.memory_limit + l1) * 1024L + "");
			String str1 = LanguageType.getRunCmd(localFile3.getAbsolutePath(), (String) localObject1, paramRunRecord.language);
			localOutputStream.write((str1 + "\n").getBytes());
			ServerConfig.debug("Run cmd: " + str1 + "");
			localOutputStream.write((localFile3.getAbsolutePath() + "\n").getBytes());
			ServerConfig.debug("Work Path: " + localFile3.getAbsolutePath());
			localOutputStream.write((localArrayList1.size() + "\n").getBytes());
			ServerConfig.debug("Datd Files: " + localArrayList1.size() + "");
			long l2 = 0L;
			long l3 = 0L;
			try
			{
				for (int j = 0; j < localArrayList1.size(); j++)
				{
					localOutputStream.write(((String) localArrayList1.get(j) + "\n").getBytes());
					ServerConfig.debug((String) localArrayList1.get(j));
					File localObject2 = new File((String) localArrayList2.get(j));
					localOutputStream.write((Tool.fixPath(localFile3.getAbsolutePath()) + ((File) localObject2).getName() + "\n").getBytes());
					ServerConfig.debug(Tool.fixPath(localFile3.getAbsolutePath()) + ((File) localObject2).getName());
					localOutputStream.write((((File) localObject2).getAbsolutePath() + "\n").getBytes());
					ServerConfig.debug(((File) localObject2).getAbsolutePath());
				}
				localOutputStream.flush();
			} catch (IOException localIOException1)
			{
			}
			String str2 = null;
			Object localObject2 = new BufferedReader(new InputStreamReader(localProcess.getInputStream()));
			BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getErrorStream()));
			String str3 = "";
			try
			{
				str2 = ((BufferedReader) localObject2).readLine();
				l2 = Long.parseLong(str2);
				str2 = ((BufferedReader) localObject2).readLine();
				l3 = Long.parseLong(str2);
				while ((str2 = localBufferedReader.readLine()) != null)
					str3 = str3 + str2;
			} catch (Exception localException2)
			{
			}
			ServerConfig.debug("Time=" + l2);
			ServerConfig.debug("Memory=" + l3);
			l3 -= l1;
			localProcess.waitFor();
			try
			{
				localBufferedReader.close();
				((BufferedReader) localObject2).close();
			} catch (IOException localIOException2)
			{
			}
			int k = localProcess.exitValue();
			// System.out.println(k+" "+str3);
			if ((k != 0) && (str3.indexOf("Exception") != -1))
				k = 5;
			if ((k != 0) && (str3.indexOf("Traceback") != -1))
				k = 5;
			if (k < 0)
				k = 98;
			boolean bool1 = false;
			boolean bool2 = false;
			int m = 1;
			synchronized (mute)
			{
				submit(paramConnection, paramRunRecord);
				bool1 = havePass(paramConnection, paramRunRecord);
				bool2 = contest_havePass(paramConnection, paramRunRecord);
				if (bool1)
					m = 0;
				PreparedStatement localPreparedStatement = paramConnection.prepareStatement("update solution set result=?,time=?,memory=?,valid=? where solution_id=?");
				int n = 1;
				localPreparedStatement.setInt(n++, k);
				localPreparedStatement.setLong(n++, l2);
				localPreparedStatement.setLong(n++, l3);
				localPreparedStatement.setInt(n++, m);
				localPreparedStatement.setLong(n++, paramRunRecord.solution_id);
				localPreparedStatement.executeUpdate();
				localPreparedStatement.close();
				if (k == 0)
				{
					localPreparedStatement = paramConnection.prepareStatement("UPDATE problem SET accepted = accepted+1,ratio=100*accepted/submit WHERE problem_id=?");
					localPreparedStatement.setLong(1, paramRunRecord.problem_id);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					if ((paramRunRecord.contest_id != null) && (!bool2))
						contest_Submit(paramConnection, paramRunRecord, true);
					if (!bool1)
					{
						localPreparedStatement = paramConnection.prepareStatement("UPDATE users SET solved = solved+1 WHERE user_id=?");
						localPreparedStatement.setString(1, paramRunRecord.user_id);
						localPreparedStatement.executeUpdate();
						localPreparedStatement.close();
						localPreparedStatement = paramConnection.prepareStatement("update problem set solved=solved+1,difficulty=100*error/(error+solved) where problem_id=?");
						localPreparedStatement.setLong(1, paramRunRecord.problem_id);
						localPreparedStatement.executeUpdate();
						localPreparedStatement.close();
					}
					return true;
				}
				if ((paramRunRecord.contest_id != null) && (!bool2))
					contest_Submit(paramConnection, paramRunRecord, false);
				if (!bool1)
				{
					localPreparedStatement = paramConnection.prepareStatement("update problem set error=error+1,difficulty=100*error/(error+solved) where problem_id=?");
					localPreparedStatement.setLong(1, paramRunRecord.problem_id);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
				}
				return false;
			}
		} catch (Exception localException1)
		{
			localException1.printStackTrace(System.err);
		}
		return false;
	}

	public static boolean Compile(RunRecord paramRunRecord, Connection paramConnection) throws Exception
	{
		File localFile2 = null;
		if (paramRunRecord.contest_id == null)
		{
			File localFile1 = new File(Tool.fixPath(ServerConfig.getValue("WorkingPath")) + (paramRunRecord.solution_id - 1L));
			Tool.delete(localFile1);
			localFile2 = new File(Tool.fixPath(ServerConfig.getValue("WorkingPath")) + paramRunRecord.solution_id);
		}
		else
		{
			localFile2 = new File(Tool.fixPath(ServerConfig.getValue("WorkingPath")) 
					+ "c" + paramRunRecord.contest_id + "//" + paramRunRecord.solution_id);
		}
		String str1 = "Main";
		if (!localFile2.mkdirs())
		{
			ServerConfig.debug("can't make directory:"+localFile2.getAbsolutePath());
			return false;
		}
		try
		{
			File localFile3 = null;
			localFile3 = new File(localFile2.getAbsolutePath() + "//" + str1 + "." + LanguageType.getExt(paramRunRecord.language));
			localFile3.createNewFile();
			FileOutputStream localFileOutputStream = new FileOutputStream(localFile3);
			localFileOutputStream.write(paramRunRecord.source.getBytes());
			localFileOutputStream.flush();
			localFileOutputStream.close();
			String str2 = null;
			String str3 = ServerConfig.getComShell();
			str2 = LanguageType.getCompileCmd(localFile2.getAbsolutePath(), str1, paramRunRecord.language);
			// System.out.println(str2);

			ServerConfig.debug("Compile Command: " + str2);
			Runtime localRuntime = Runtime.getRuntime();
			Process localProcess = localRuntime.exec(str3);
			OutputStream localOutputStream = localProcess.getOutputStream();
			localOutputStream.write((str2 + "\n").getBytes());
			localOutputStream.flush();
			BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localProcess.getErrorStream()));
			String str4 = "";
			String str5 = "";
			long l = System.currentTimeMillis();
			String str6;
			while ((l + 10000L > System.currentTimeMillis()) && ((str6 = localBufferedReader.readLine()) != null))
				str4 = str4 + str6 + "\n";
			localBufferedReader.close();
			str5 = str5 + str4;
			try
			{
				localProcess.waitFor();
			} catch (InterruptedException localInterruptedException)
			{
			}
			File localFile4 = new File(Tool.fixPath(localFile2.getAbsolutePath()) + str1 + "." + LanguageType.getExe(paramRunRecord.language));
			ServerConfig.debug(Tool.fixPath(localFile2.getAbsolutePath()) + str1 + ".exe");
			boolean bool1 = localFile4.isFile();
			boolean bool2 = false;
			boolean bool3 = false;
			int i = 1;
			if (!bool1)
				synchronized (mute)
				{
					submit(paramConnection, paramRunRecord);
					bool2 = havePass(paramConnection, paramRunRecord);
					bool3 = contest_havePass(paramConnection, paramRunRecord);
					if (bool2)
						i = 0;
					PreparedStatement localPreparedStatement = paramConnection.prepareStatement("update solution set result=?,time=?,memory=?,valid=? where solution_id=?");
					int j = 1;
					localPreparedStatement.setInt(j++, 7);
					localPreparedStatement.setLong(j++, 0L);
					localPreparedStatement.setLong(j++, 0L);
					localPreparedStatement.setInt(j++, i);
					localPreparedStatement.setLong(j++, paramRunRecord.solution_id);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					localPreparedStatement = paramConnection.prepareStatement("delete from compileinfo where solution_id=?");
					localPreparedStatement.setLong(1, paramRunRecord.solution_id);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					localPreparedStatement = paramConnection.prepareStatement("insert into compileinfo (solution_id,error) values(?,?)");
					localPreparedStatement.setLong(1, paramRunRecord.solution_id);
					// System.out.println(str5);
					localPreparedStatement.setString(2, str5);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					if ((paramRunRecord.contest_id != null) && (!bool3))
						contest_Submit(paramConnection, paramRunRecord, false);
					if (!bool2)
					{
						localPreparedStatement = paramConnection.prepareStatement("update problem set error=error+1,difficulty=100*error/(error+solved) where problem_id=?");
						localPreparedStatement.setLong(1, paramRunRecord.problem_id);
						localPreparedStatement.executeUpdate();
						localPreparedStatement.close();
					}
				}
			return bool1;
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
		}
		return false;
	}
}
