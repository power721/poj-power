package com.pku.judgeonline.servlet;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.problemset.Judge;

import java.sql.*;

public class Rejudge extends Thread
{

	private long _$2020;
	private long _$2018;
	public static Object rejudge_mute = new Object();

	public Rejudge()
	{
		_$2018 = 0L;
	}

	public Rejudge(long l, long l1)
	{
		_$2018 = 0L;
		_$2020 = l;
		_$2018 = l1;
		start();
	}

	public void run()
	{
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("select contest_id from problem where problem_id=?");
			preparedstatement.setLong(1, _$2020);
			ResultSet resultset = preparedstatement.executeQuery();
			String s = null;
			if (resultset.next())
				s = resultset.getString("contest_id");
			if (s != null)
				_$2018 = Integer.parseInt(s);
			preparedstatement.close();
			connection.close();
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
		}
		try
		{
			ReJudgeOne(_$2020, _$2018);
		} catch (Exception exception1)
		{
			exception1.printStackTrace(System.err);
		}
	}

	public static void FireProblemFromContest(long l, long l1, Connection connection) throws Exception
	{
		PreparedStatement preparedstatement = connection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
		preparedstatement.setLong(1, l1);
		preparedstatement.setLong(2, l);
		ResultSet resultset = preparedstatement.executeQuery();
		int i = 100;
		if (!resultset.next())
			return;
		i = resultset.getInt("num");
		preparedstatement.close();
		if (i > Judge.MAX_CONTEST_PROBLEM_NUM)
		{
			return;
		} else
		{
			char c = (char) (65 + i);
			String s = (new StringBuilder()).append(" ").append(c).append("_time ").toString();
			String s1 = (new StringBuilder()).append(" ").append(c).append("_WrongSubmits").toString();
			PreparedStatement preparedstatement1 = connection.prepareStatement((new StringBuilder()).append("update attend set accepts=accepts-(").append(s).append("!=0),penalty=penalty-").append(s).append("-(").append(s).append("!=0)*").append(s1).append("*1200 ,").append(s).append("=0,").append(s1).append("=0 where contest_id=?").toString());
			preparedstatement1.setLong(1, l1);
			preparedstatement1.executeUpdate();
			preparedstatement1.close();
			return;
		}
	}

	public static void RebuildContestRankOfProb(long l, long l1, Connection connection) throws Exception
	{
		FireProblemFromContest(l, l1, connection);
		PreparedStatement preparedstatement = connection.prepareStatement("select start_time from contest where contest_id=?");
		preparedstatement.setLong(1, l1);
		ResultSet resultset = preparedstatement.executeQuery();
		if (!resultset.next())
		{
			preparedstatement.close();
			System.err.println((new StringBuilder()).append("No contest:").append(l1).toString());
			return;
		}
		Timestamp timestamp = resultset.getTimestamp("start_time");
		preparedstatement.close();
		preparedstatement = connection.prepareStatement("select num from contest_problem where contest_id=? and problem_id=?");
		preparedstatement.setLong(1, l1);
		preparedstatement.setLong(2, l);
		resultset = preparedstatement.executeQuery();
		int i = 100;
		if (!resultset.next())
		{
			preparedstatement.close();
			System.err.println((new StringBuilder()).append("No problem:").append(l).toString());
			return;
		}
		i = resultset.getInt("num");
		preparedstatement.close();
		if (i > Judge.MAX_CONTEST_PROBLEM_NUM)
		{
			System.err.println((new StringBuilder()).append("Num too large:").append(i).toString());
			return;
		}
		char c = (char) (65 + i);
		// String s = (new
		// StringBuilder()).append(" ").append(c).append("_time ").toString();
		String s1 = (new StringBuilder()).append(" ").append(c).append("_WrongSubmits").toString();
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		preparedstatement = connection.prepareStatement("select user_id from attend where contest_id=?");
		preparedstatement.setLong(1, l1);
		resultset = preparedstatement.executeQuery();
		do
		{
			if (!resultset.next())
				break;
			boolean flag = false;
			String s2 = resultset.getString("user_id");
			PreparedStatement preparedstatement1 = connection.prepareStatement("select solution_id,in_date from solution where contest_id=? and problem_id=? and user_id=? and result=0 order by solution_id limit 1");
			preparedstatement1.setLong(1, l1);
			preparedstatement1.setLong(2, l);
			preparedstatement1.setString(3, s2);
			ResultSet resultset1 = preparedstatement1.executeQuery();
			long l4;
			if (resultset1.next())
			{
				l4 = resultset1.getLong("solution_id");
				timestamp1 = resultset1.getTimestamp("in_date");
				flag = true;
			} else
			{
				l4 = 0x3b9ac9ffL;
			}
			preparedstatement1.close();
			preparedstatement1 = connection.prepareStatement("select count(*) as ws from solution where contest_id=? and problem_id=? and user_id=? and solution_id<?");
			preparedstatement1.setLong(1, l1);
			preparedstatement1.setLong(2, l);
			preparedstatement1.setString(3, s2);
			preparedstatement1.setLong(4, l4);
			resultset1 = preparedstatement1.executeQuery();
			long l3;
			if (resultset1.next())
				l3 = resultset1.getLong("ws");
			else
				l3 = 0L;
			preparedstatement1.close();
			if (l3 != 0L)
			{
				PreparedStatement preparedstatement2 = connection.prepareStatement((new StringBuilder()).append("update attend set ").append(s1).append("=? where contest_id=? and user_id=?").toString());
				preparedstatement2.setLong(1, l3);
				preparedstatement2.setLong(2, l1);
				preparedstatement2.setString(3, s2);
				preparedstatement2.executeUpdate();
				preparedstatement2.close();
			}
			if (flag)
			{
				long l2 = (timestamp1.getTime() - timestamp.getTime()) / 1000L;
				PreparedStatement preparedstatement3 = connection.prepareStatement((new StringBuilder()).append("update attend set accepts=accepts+1,penalty=penalty+?+").append(s1).append("*1200,").append(c).append("_time=? where contest_id=? and user_id=?").toString());
				preparedstatement3.setLong(1, l2);
				preparedstatement3.setLong(2, l2);
				preparedstatement3.setLong(3, l1);
				preparedstatement3.setString(4, s2);
				preparedstatement3.executeUpdate();
				preparedstatement3.close();
			}
		} while (true);
		preparedstatement.close();
	}

	public static void ReJudgeOne(long l, long l1) throws Exception
	{
		Connection connection = DBConfig.getConn();
		PreparedStatement preparedstatement = connection.prepareStatement("update problem set accepted=0,submit=0,submit_user=0,error=0,solved=0 where problem_id=?");
		preparedstatement.setLong(1, l);
		preparedstatement.executeUpdate();
		preparedstatement.close();
		if (l1 != 0L)
			synchronized (Judge.mute)
			{
				FireProblemFromContest(l, l1, connection);
			}
		preparedstatement = connection.prepareStatement("select solution_id from solution where problem_id=? and result<>10000  order by solution_id asc");
		preparedstatement.setLong(1, l);
		for (ResultSet resultset = preparedstatement.executeQuery(); resultset.next(); Judge.ReJudge(resultset.getLong("solution_id"), l1, connection))
			;
		preparedstatement.close();
		synchronized (Judge.mute)
		{
			CheckOnePS(l, connection);
			CheckOneP(l, connection);
			if (l1 != 0L)
				RebuildContestRankOfProb(l, l1, connection);
			users();
		}
		connection.close();
	}

	public static void ReJudgeSol(long l, long l1) throws Exception
	{
		Connection connection = DBConfig.getConn();
		//Judge.ReJudge(l, l1, connection);
		long pid = 0L;
		PreparedStatement preparedstatement = connection.prepareStatement("select problem_id from solution where solution_id=?");
		preparedstatement.setLong(1, l);
		ResultSet resultset = preparedstatement.executeQuery();
		if(!resultset.next())
			return;
		pid = resultset.getLong("problem_id");
		if (l1 != 0L)
			synchronized (Judge.mute)
			{
				FireProblemFromContest(pid, l1, connection);
			}
		Judge.ReJudge(l, l1, connection);
		synchronized (Judge.mute)
		{
			CheckOnePS(pid, connection);
			CheckOneP(pid, connection);
			if (l1 != 0L)
				RebuildContestRankOfProb(pid, l1, connection);
			users();
		}
		connection.close();
	}

	public static void CheckOnePS(long l, Connection connection)
	{
		try
		{
			PreparedStatement preparedstatement = connection.prepareStatement("update solution set valid=1 where problem_id=?");
			preparedstatement.setLong(1, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select user_id,min(solution_id) as minacc from solution where result=0 and problem_id=? group by user_id");
			preparedstatement.setLong(1, l);
			String s;
			long l1;
			for (ResultSet resultset = preparedstatement.executeQuery(); resultset.next(); UpdateS(l, s, l1, connection))
			{
				s = resultset.getString("user_id");
				l1 = resultset.getLong("minacc");
			}

			preparedstatement.close();
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public static void UpdateS(long l, String s, long l1, Connection connection) throws Exception
	{
		PreparedStatement preparedstatement = connection.prepareStatement("update solution set valid=0 where problem_id=? and user_id=? and solution_id>?");
		preparedstatement.setLong(1, l);
		preparedstatement.setString(2, s);
		preparedstatement.setLong(3, l1);
		preparedstatement.executeUpdate();
		preparedstatement.close();
	}

	public static void CheckOneP(long l, Connection connection)
	{
		try
		{
			PreparedStatement preparedstatement = connection.prepareStatement("update problem set accepted=0,submit=0,submit_user=0,error=0,solved=0 where problem_id=?");
			preparedstatement.setLong(1, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select count(*) as accepted from solution where result=0 and problem_id=?");
			preparedstatement.setLong(1, l);
			ResultSet resultset = preparedstatement.executeQuery();
			resultset.next();
			long l1 = resultset.getLong("accepted");
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("UPDATE problem SET accepted=? WHERE problem_id=?");
			preparedstatement.setLong(1, l1);
			preparedstatement.setLong(2, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select count(*) as submits from solution where problem_id=? and result!=10000");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			resultset.next();
			long l2 = resultset.getLong("submits");
			preparedstatement = connection.prepareStatement("UPDATE problem SET submit=?,ratio=100*accepted/submit WHERE problem_id=?");
			preparedstatement.setLong(1, l2);
			preparedstatement.setLong(2, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select count(distinct user_id) as submit_user from solution where problem_id=? and result!=10000");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			resultset.next();
			l2 = resultset.getLong("submit_user");
			preparedstatement = connection.prepareStatement("UPDATE problem SET submit_user=?  WHERE problem_id=?");
			preparedstatement.setLong(1, l2);
			preparedstatement.setLong(2, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select count(distinct user_id) as solved from solution where problem_id=? and result=0");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			resultset.next();
			long l3 = resultset.getLong("solved");
			preparedstatement = connection.prepareStatement("UPDATE problem SET solved=? WHERE problem_id=?");
			preparedstatement.setLong(1, l3);
			preparedstatement.setLong(2, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select count(distinct user_id) as error from solution where problem_id=? and result!=0 and result!=10000 and valid=1");
			preparedstatement.setLong(1, l);
			resultset = preparedstatement.executeQuery();
			resultset.next();
			long l4 = resultset.getLong("error");
			preparedstatement = connection.prepareStatement("UPDATE problem SET error=?,difficulty=100*error/(error+solved) WHERE problem_id=?");
			preparedstatement.setLong(1, l4);
			preparedstatement.setLong(2, l);
			preparedstatement.executeUpdate();
			preparedstatement.close();
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
			return;
		}
	}

	public static void users()
	{
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("update users set solved=0,submit=0");
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("update users,(select user_id,count(*) as sub,count(if(result=0 and valid=1,1,null)) as sol from solution group by user_id) as temp set solved=sol,submit=sub where users.user_id=temp.user_id;");
			preparedstatement.executeUpdate();
			preparedstatement.close();
			connection.close();
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public static void problem()
	{
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("update problem set accepted=0,submit=0,submit_user=0,error=0,solved=0");
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select problem_id,count(*) as accepted from solution where result=0 group by problem_id");
			PreparedStatement preparedstatement1;
			ResultSet resultset;
			for (resultset = preparedstatement.executeQuery(); resultset.next(); preparedstatement1.close())
			{
				int i = resultset.getInt("problem_id");
				long l1 = resultset.getLong("accepted");
				preparedstatement1 = connection.prepareStatement("UPDATE problem SET accepted=? WHERE problem_id=?");
				preparedstatement1.setLong(1, l1);
				preparedstatement1.setInt(2, i);
				preparedstatement1.executeUpdate();
			}

			resultset.close();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select problem_id,count(*) as submit from solution group by problem_id");
			PreparedStatement preparedstatement2;
			for (resultset = preparedstatement.executeQuery(); resultset.next(); preparedstatement2.close())
			{
				int j = resultset.getInt("problem_id");
				long l2 = resultset.getLong("submit");
				preparedstatement2 = connection.prepareStatement("UPDATE problem SET submit=?,ratio=100*accepted/submit WHERE problem_id=?");
				preparedstatement2.setLong(1, l2);
				preparedstatement2.setInt(2, j);
				preparedstatement2.executeUpdate();
			}

			resultset.close();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select problem_id,count(*) as submit_user from (select problem_id,count(*) as accepted from solution  group by problem_id,user_id)as temp group by problem_id");
			PreparedStatement preparedstatement3;
			for (resultset = preparedstatement.executeQuery(); resultset.next(); preparedstatement3.close())
			{
				int k = resultset.getInt("problem_id");
				long l3 = resultset.getLong("submit_user");
				preparedstatement3 = connection.prepareStatement("UPDATE problem SET submit_user=?  WHERE problem_id=?");
				preparedstatement3.setLong(1, l3);
				preparedstatement3.setInt(2, k);
				preparedstatement3.executeUpdate();
			}

			resultset.close();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select problem_id,count(*) as solved from (select problem_id,count(*) as accepted from solution where result=0 group by problem_id,user_id)as temp group by problem_id");
			PreparedStatement preparedstatement4;
			for (resultset = preparedstatement.executeQuery(); resultset.next(); preparedstatement4.close())
			{
				int l = resultset.getInt("problem_id");
				long l4 = resultset.getLong("solved");
				preparedstatement4 = connection.prepareStatement("UPDATE problem SET solved=? WHERE problem_id=?");
				preparedstatement4.setLong(1, l4);
				preparedstatement4.setInt(2, l);
				preparedstatement4.executeUpdate();
			}

			resultset.close();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select problem_id,count(*) as error from solution where result<>0 and valid=1 group by problem_id");
			PreparedStatement preparedstatement5;
			for (resultset = preparedstatement.executeQuery(); resultset.next(); preparedstatement5.close())
			{
				int i1 = resultset.getInt("problem_id");
				long l5 = resultset.getLong("error");
				preparedstatement5 = connection.prepareStatement("UPDATE problem SET error=?,difficulty=100*error/(error+solved) WHERE problem_id=?");
				preparedstatement5.setLong(1, l5);
				preparedstatement5.setInt(2, i1);
				preparedstatement5.executeUpdate();
			}

			resultset.close();
			preparedstatement.close();
			connection.close();
		} catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

}
