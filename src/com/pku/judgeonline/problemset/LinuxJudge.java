package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.LanguageType;
import com.pku.judgeonline.common.ResultType;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.servlet.Rejudge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class LinuxJudge extends Thread
{
	public static final int MAX_THREAD = 1;
	public static int MAX_CONTEST_PROBLEM_NUM = 15;
	public static int threads = 0;
	public static Object mute = new Object();
	public static ArrayList<RunRecord> list = new ArrayList<RunRecord>();
	private Connection _$1537 = null;
	public static final String DATA_EXT_IN = ".in";
	public static final String DATA_EXT_OUT = ".out";

	public LinuxJudge()
	{
		start();
	}

	public static boolean havePass(Connection connection, RunRecord runRecord) throws Exception
	{
		PreparedStatement localPreparedStatement = connection.prepareStatement("select solution_id from solution where user_id=? and problem_id=? and result=0 and solution_id<? limit 1");
		localPreparedStatement.setString(1, runRecord.user_id);
		localPreparedStatement.setLong(2, runRecord.problem_id);
		localPreparedStatement.setLong(3, runRecord.solution_id);
		ResultSet localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
		{
			localPreparedStatement.close();
			return false;
		}
		localPreparedStatement.close();
		return true;
	}

	public static boolean contest_havePass(Connection connection, RunRecord runRecord) throws Exception
	{
		PreparedStatement localPreparedStatement = connection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
		localPreparedStatement.setString(1, runRecord.contest_id);
		localPreparedStatement.setLong(2, runRecord.problem_id);
		ResultSet localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
			return true;
		int i = localResultSet.getInt("num");
		localPreparedStatement.close();
		char c = (char) (65 + i);
		String str = "" + c + "_time";
		localPreparedStatement = connection.prepareStatement("select " + c + "_time from attend where contest_id=? and user_id=?");
		localPreparedStatement.setString(1, runRecord.contest_id);
		localPreparedStatement.setString(2, runRecord.user_id);
		localResultSet = localPreparedStatement.executeQuery();
		if ((localResultSet.next()) && (localResultSet.getLong(str) > 0L))
		{
			localPreparedStatement.close();
			return true;
		}
		localPreparedStatement.close();
		return false;
	}

	public static void submit(Connection connection, RunRecord runRecord) throws Exception
	{
		PreparedStatement localPreparedStatement = connection.prepareStatement("UPDATE users SET submit = submit+1,language=? where user_id =?");
		localPreparedStatement.setInt(1, runRecord.language);
		localPreparedStatement.setString(2, runRecord.user_id);
		localPreparedStatement.executeUpdate();
		localPreparedStatement.close();
		localPreparedStatement = connection.prepareStatement("UPDATE problem SET submit = submit+1,ratio=100*accepted/submit,in_date=? where problem_id=?");
		localPreparedStatement.setTimestamp(1, runRecord.submit_time);
		localPreparedStatement.setLong(2, runRecord.problem_id);
		localPreparedStatement.executeUpdate();
		localPreparedStatement.close();
		boolean bool = false;
		localPreparedStatement = connection.prepareStatement("select solution_id from solution where user_id=? and problem_id=? and solution_id<? limit 1");
		localPreparedStatement.setString(1, runRecord.user_id);
		localPreparedStatement.setLong(2, runRecord.problem_id);
		localPreparedStatement.setLong(3, runRecord.solution_id);
		ResultSet localResultSet = localPreparedStatement.executeQuery();
		bool = localResultSet.next();
		localPreparedStatement.close();
		if (!bool)
		{
			localPreparedStatement = connection.prepareStatement("UPDATE problem SET submit_user = submit_user+1 where problem_id=?");
			localPreparedStatement.setLong(1, runRecord.problem_id);
			localPreparedStatement.executeUpdate();
			localPreparedStatement.close();
		}
	}

	public static void contest_Submit(Connection connection, RunRecord runRecord, boolean paramBoolean) throws Exception
	{
		PreparedStatement localPreparedStatement = connection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
		localPreparedStatement.setString(1, runRecord.contest_id);
		localPreparedStatement.setLong(2, runRecord.problem_id);
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
			localPreparedStatement = connection.prepareStatement("update attend set " + c + "_time=?,accepts=accepts+1,penalty=penalty+?+" + str + "*1200 where contest_id=? and user_id=?");
			localPreparedStatement.setLong(1, (runRecord.submit_time.getTime() - runRecord.start_time.getTime()) / 1000L);
			localPreparedStatement.setLong(2, (runRecord.submit_time.getTime() - runRecord.start_time.getTime()) / 1000L);
			localPreparedStatement.setString(3, runRecord.contest_id);
			localPreparedStatement.setString(4, runRecord.user_id);
			localPreparedStatement.executeUpdate();
			localPreparedStatement.close();
		} else
		{
			localPreparedStatement = connection.prepareStatement("update attend set " + c + "_WrongSubmits=" + c + "_WrongSubmits+1 where contest_id=? and user_id=?");
			localPreparedStatement.setString(1, runRecord.contest_id);
			localPreparedStatement.setString(2, runRecord.user_id);
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

	public static void ReJudge(long paramLong1, long paramLong2, Connection connection) throws Exception
	{
		RunRecord localRunRecord = new RunRecord();
		localRunRecord.solution_id = paramLong1;
		File localFile = null;
		if (paramLong2 == 0L)
		{
			localFile = new File(Tool.fixPath(ServerConfig.getValue("WorkingPath")) + paramLong1);
		}
		else
		{
			localFile = new File(Tool.fixPath(ServerConfig.getValue("WorkingPath")) 
					+ "c" + paramLong2 + File.separator + paramLong1);
		}
		Tool.delete(localFile);
		localRunRecord.isRejudge = true;
		PreparedStatement localPreparedStatement = connection.prepareStatement("select * from solution where solution_id=?");
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
		localPreparedStatement = connection.prepareStatement("select uncompress(source) as source from source_code where solution_id=?");
		localPreparedStatement.setLong(1, paramLong1);
		localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
			return;
		localRunRecord.source = localResultSet.getString("source");
		localPreparedStatement.close();
		if (localRunRecord.contest_id != null)
		{
			localPreparedStatement = connection.prepareStatement("select * from contest where contest_id=?");
			localPreparedStatement.setString(1, localRunRecord.contest_id);
			localResultSet = localPreparedStatement.executeQuery();
			if (localResultSet.next())
				localRunRecord.start_time = localResultSet.getTimestamp("start_time");
			localPreparedStatement.close();
		}
		localPreparedStatement = connection.prepareStatement("select input_path,output_path,memory_limit,time_limit,case_time_limit from problem where problem_id=?");
		localPreparedStatement.setLong(1, localRunRecord.problem_id);
		localResultSet = localPreparedStatement.executeQuery();
		if (!localResultSet.next())
			return;
		
		localRunRecord.memory_limit = localResultSet.getLong("memory_limit");
		localRunRecord.time_limit = localResultSet.getLong("time_limit");
		localRunRecord.case_time_limit = localResultSet.getLong("case_time_limit");
		synchronized (Rejudge.rejudge_mute)
		{
			if (Compile(localRunRecord, connection))
				RunProcess(localRunRecord, connection);
		}
		localPreparedStatement.close();
		/*if (paramLong2 == 0L)
		{
			Tool.delete(localFile);
		}*/
	}

	public static boolean RunProcess(RunRecord runRecord, Connection connection) throws Exception
	{
		File dataDir = new File(runRecord.input_path);
		if (!dataDir.isDirectory())
		{
			throw new Exception("Data directory does not exist!");
		}
		
		ArrayList<String> inFileName = new ArrayList<String>();
		ArrayList<String> outFileName = new ArrayList<String>();
		File[] arrayOfFile = dataDir.listFiles();
		boolean isAccepted = true;
		boolean isSPJ = false;
		for (int i = 0; i < arrayOfFile.length; i++)
		{
			File inFile = arrayOfFile[i];
			if (inFile.getName().equalsIgnoreCase("spj"))
			{
				isSPJ = true;
				continue;
			}
			
			if (!inFile.getName().toLowerCase().endsWith(DATA_EXT_IN))
				continue;
			
			File outFile = new File(Tool.fixPath(dataDir.getAbsolutePath()) + inFile.getName().substring(0, inFile.getName().length() - DATA_EXT_IN.length()) + DATA_EXT_OUT);
			if (! outFile.isFile())
				continue;
			inFileName.add(inFile.getName());
			outFileName.add(outFile.getName());
		}
		
		for (int i = 0; i < inFileName.size() && isAccepted; i++)
		{
			String cmd = buildCommand(runRecord, inFileName.get(i), outFileName.get(i), 8*1024*1024, isSPJ, i==0);
			Process process = Runtime.getRuntime().exec(cmd);
		      InputStream inputStream = process.getInputStream();
		      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		      String line;
		      StringBuilder stringBuilder = new StringBuilder();
		      while ((line = bufferedReader.readLine()) != null)
		      {
		        stringBuilder.append(line);
		      }
		      bufferedReader.close();

		      StringBuilder sb = new StringBuilder();
		      InputStream errorStream = process.getErrorStream();
		      while (errorStream.available() > 0)
		      {
		        Character c = new Character((char) errorStream.read());
		        sb.append(c);
		      }

		      String[] resultStr = stringBuilder.toString().split(" ");
		      int exitValue = process.waitFor();
		      if (exitValue > 0)
		      {
		        //updateSystemError(sb.toString());
		    	  runRecord.result = ResultType.SystemError;
		    	  isAccepted = false;
		        break;
		      }
		      isAccepted = checkResult(runRecord, resultStr, sb.toString());
		      if (runRecord.result == ResultType.CompileError)
		      {
		    	  updateCompileError(runRecord, connection, readError(runRecord, "stderr_compiler.txt"));
		      }
		}
		
		try
		{
			synchronized (mute)
			{
				if (isAccepted)
				{
					runRecord.result = ResultType.Accepted;
				}
				int m = 1;
				submit(connection, runRecord);
				boolean bool1 = havePass(connection, runRecord);
				boolean bool2 = contest_havePass(connection, runRecord);
				if (bool1)
					m = 0;
				PreparedStatement localPreparedStatement = connection.prepareStatement("update solution set result=?,time=?,memory=?,valid=? where solution_id=?");
				int n = 1;
				localPreparedStatement.setInt(n++, runRecord.result);
				localPreparedStatement.setLong(n++, runRecord.time);
				localPreparedStatement.setLong(n++, runRecord.memory);
				localPreparedStatement.setInt(n++, m);
				localPreparedStatement.setLong(n++, runRecord.solution_id);
				localPreparedStatement.executeUpdate();
				localPreparedStatement.close();
				if (isAccepted)
				{
					localPreparedStatement = connection.prepareStatement("UPDATE problem SET accepted = accepted+1,ratio=100*accepted/submit WHERE problem_id=?");
					localPreparedStatement.setLong(1, runRecord.problem_id);
					localPreparedStatement.executeUpdate();
					localPreparedStatement.close();
					if ((runRecord.contest_id != null) && (!bool2))
						contest_Submit(connection, runRecord, true);
					if (!bool1)
					{
						localPreparedStatement = connection.prepareStatement("UPDATE users SET solved = solved+1 WHERE user_id=?");
						localPreparedStatement.setString(1, runRecord.user_id);
						localPreparedStatement.executeUpdate();
						localPreparedStatement.close();
						localPreparedStatement = connection.prepareStatement("update problem set solved=solved+1,difficulty=100*error/(error+solved) where problem_id=?");
						localPreparedStatement.setLong(1, runRecord.problem_id);
						localPreparedStatement.executeUpdate();
						localPreparedStatement.close();
					}
					return true;
				}
				if ((runRecord.contest_id != null) && (!bool2))
					contest_Submit(connection, runRecord, false);
				if (!bool1)
				{
					localPreparedStatement = connection.prepareStatement("update problem set error=error+1,difficulty=100*error/(error+solved) where problem_id=?");
					localPreparedStatement.setLong(1, runRecord.problem_id);
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

	public static boolean Compile(RunRecord runRecord, Connection connection) throws Exception
	{
		runRecord.input_path = ServerConfig.dataPath + File.separator + runRecord.problem_id;
		runRecord.output_path = runRecord.input_path;
		
		File workDir = null;
		if (runRecord.contest_id == null)
		{
			File localFile1 = new File(Tool.fixPath(ServerConfig.workPath) + (runRecord.solution_id - 5L));
			Tool.delete(localFile1);
			workDir = new File(Tool.fixPath(ServerConfig.workPath) + runRecord.solution_id);
		}
		else
		{
			runRecord.workPath += "c" + runRecord.contest_id;
			workDir = new File(Tool.fixPath(ServerConfig.workPath) + "c" + runRecord.contest_id + File.separator + runRecord.solution_id);
		}
		if (!workDir.mkdirs())
		{
			ServerConfig.debug("can't make directory:"+workDir.getAbsolutePath());
			return false;
		}
		
		runRecord.workPath = Tool.fixPath(workDir.getAbsolutePath());
		System.out.println(workDir.getAbsolutePath());
		try
		{
			File localFile3 = new File(workDir.getAbsolutePath() + File.separator  + "Main." + LanguageType.getExt(runRecord.language));
			localFile3.createNewFile();
			FileOutputStream localFileOutputStream = new FileOutputStream(localFile3);
			localFileOutputStream.write(runRecord.source.getBytes());
			localFileOutputStream.flush();
			localFileOutputStream.close();
			runRecord.sourceFileName = localFile3.getName();
			System.out.println(localFile3.getAbsoluteFile());
		} catch (Exception localException)
		{
			localException.printStackTrace(System.err);
		}
		return true;
	}
	
	private static String buildCommand(RunRecord runRecord, String inputFile, String outputFile, long outputLimit, boolean isSpj, boolean firstCase)
	  {
	    StringBuilder stringBuilder = new StringBuilder();
	    stringBuilder.append(ServerConfig.getRunShell());
	    stringBuilder.append(" -u ");
	    stringBuilder.append(runRecord.solution_id);
	    stringBuilder.append(" -s ");
	    stringBuilder.append(runRecord.sourceFileName);
	    stringBuilder.append(" -n ");
	    stringBuilder.append(runRecord.problem_id);
	    stringBuilder.append(" -D ");
	    stringBuilder.append(ServerConfig.dataPath);
	    stringBuilder.append(" -d ");
	    stringBuilder.append(ServerConfig.workPath);
	    stringBuilder.append(" -t ");
	    stringBuilder.append(runRecord.time_limit);
	    stringBuilder.append(" -m ");
	    stringBuilder.append(runRecord.memory_limit);
	    stringBuilder.append(" -o ");
	    stringBuilder.append(outputLimit);
	    if (isSpj)
	      stringBuilder.append(" -S");
	    stringBuilder.append(" -l ");
	    stringBuilder.append(convertLang(runRecord.language));
	    stringBuilder.append(" -I ");
	    stringBuilder.append(inputFile);
	    stringBuilder.append(" -O ");
	    stringBuilder.append(outputFile);
	    if (firstCase)
	      stringBuilder.append(" -C");
	    System.out.println(stringBuilder.toString());
	    return stringBuilder.toString();
	  }

	  private static boolean checkResult(RunRecord runRecord, String[] resultStr, String errorOut)
	  {
	    boolean isAccepted = true;
	    if (resultStr != null && resultStr.length >= 3)
	    {
	      try
	      {
	        int result = convertResult(Integer.parseInt(resultStr[0]));
	        if (result == ResultType.Accepted)
	        {
	          result = ResultType.Run;
	        } else
	        {
	          isAccepted = false;
	        }
	        int time = Integer.parseInt(resultStr[1]);
	        int memory = Integer.parseInt(resultStr[2]);
	        runRecord.result = result;
	        runRecord.time += time;
	        runRecord.memory = Math.max(runRecord.memory, memory);
	      } catch (NumberFormatException e)
	      {
	    	  //updateSystemError(e.getLocalizedMessage());
	    	  runRecord.result = ResultType.SystemError;
	    	  isAccepted = false;
	    	  return false;
	      }
	    } else
	    {
	      //updateSystemError(errorOut);
	      runRecord.result = ResultType.SystemError;
	      isAccepted = false;
	    }
	    return isAccepted;
	  }

	  private static String readError(RunRecord runRecord, String fileName)
	  {
	    String workPath = runRecord.workPath;
	    StringBuilder sb = new StringBuilder();
	    BufferedReader br = null;
	    try
	    {
	      br = new BufferedReader(new FileReader(workPath + fileName));
	      String line;
	      while ((line = br.readLine()) != null)
	      {
	        if (line.trim().startsWith(workPath))
	        {
	          line = line.substring(workPath.length());
	        }
	        sb.append(line).append('\n');
	      }
	    } catch (Exception e)
	    {
	      e.printStackTrace();
	    } finally
	    {
	      if (br != null)
	      {
	        try
	        {
	          br.close();
	        } catch (IOException ignored)
	        {
	        }
	      }
	    }

	    return sb.length() > 0 ? sb.toString() : null;
	  }

	  protected static boolean updateCompileError(RunRecord runRecord, Connection connection, String error) throws SQLException
	  {
		  PreparedStatement preparedStatement;
			preparedStatement = connection.prepareStatement("delete from compileinfo where solution_id=?");
			preparedStatement.setLong(1, runRecord.solution_id);
			preparedStatement.executeUpdate();
			preparedStatement.close();
			preparedStatement = connection.prepareStatement("insert into compileinfo (solution_id,error) values(?,?)");
			preparedStatement.setLong(1, runRecord.solution_id);
			preparedStatement.setString(2, error);
			preparedStatement.executeUpdate();
			preparedStatement.close();
	    return true;
	  }

	  private static int convertResult(int result)
	  {
	    switch (result)
	    {
	    case 0:
	      return ResultType.Run;
	    case 1:
	      return ResultType.Accepted;
	    case 2:
	      return ResultType.PresentationError;
	    case 3:
	      return ResultType.TimeLimitOut;
	    case 4:
	      return ResultType.MemoryLimitOut;
	    case 5:
	      return ResultType.WrongAnswer;
	    case 6:
	      return ResultType.OutputLimitOut;
	    case 7:
	      return ResultType.CompileError;
	    case 8:
	    case 9:
	    case 10:
	    case 11:
	    case 12:
	      return ResultType.RunTimeError;
	    case 13:
	      return ResultType.RestrictedFunction;
	    case 14:
	      return ResultType.SystemError;
	    case 15:
	      return ResultType.RunTimeError;
	    default:
	      return ResultType.SystemError;
	    }
	  }

	  private static int convertLang(int lang)
	  {
		  switch (lang)
		  {
		  case 0:return 2;
		  case 1:return 1;
		  case 2:return 3;
		  case 3:return 4;
		  case 5:return 5;
		  default:return 0;
		  }
	  }
}
