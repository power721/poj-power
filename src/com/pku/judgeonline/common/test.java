package com.pku.judgeonline.common;

import com.pku.judgeonline.problemset.Judge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

public class test
{
	public static void main(String[] paramArrayOfString)
	{
		try
		{
			BufferedReader BufferedReader = new BufferedReader(new FileReader("c:\\a.txt"));
			Connection Connection = DBConfig.getConn();
			String str3;
			while ((str3 = BufferedReader.readLine()) != null)
			{
				StringTokenizer StringTokenizer = new StringTokenizer(str3, "\t");
				String str1 = StringTokenizer.nextToken().trim();
				String str2 = StringTokenizer.nextToken().trim();
				String str4 = StringTokenizer.nextToken().trim();
				PreparedStatement PreparedStatement = Connection.prepareStatement("SELECT * FROM users WHERE UPPER(user_id) = UPPER(?)");
				PreparedStatement.setString(1, str1);
				ResultSet ResultSet = PreparedStatement.executeQuery();
				if (ResultSet.first())
				{
					PreparedStatement.close();
					Connection.close();
					return;
				}
				PreparedStatement.close();
				PreparedStatement = Connection.prepareStatement("INSERT INTO users (user_id,password,nick,email) values (?,encode(?,'13671107113'),?,' ')");
				PreparedStatement.setString(1, str1);
				PreparedStatement.setString(2, str2);
				PreparedStatement.setString(3, str4);
				PreparedStatement.executeUpdate();
				PreparedStatement.close();
			}
			BufferedReader.close();
			Connection.close();
		} catch (Exception Exception)
		{
			Exception.printStackTrace(System.err);
		}
	}

	public static void setInOut()
	{
		try
		{
			Connection Connection = DBConfig.getConn();
			for (int i = 1000; i < 1609; i++)
			{
				PreparedStatement PreparedStatement = Connection.prepareStatement("update problem set input_path=?,output_path=? where problem_id=?");
				PreparedStatement.setString(1, "D:\\data\\" + i);
				PreparedStatement.setString(2, "D:\\data\\" + i);
				PreparedStatement.setLong(3, i);
				PreparedStatement.executeUpdate();
				PreparedStatement.close();
			}
		} catch (Exception Exception)
		{
		}
	}

	public static void ReJudgeOne(long paramLong1, long paramLong2) throws Exception
	{
		Connection Connection = DBConfig.getConn();
		PreparedStatement PreparedStatement = Connection.prepareStatement("select solution_id from solution where problem_id=? and result<>10000 and result<>1 order by solution_id asc");
		PreparedStatement.setLong(1, paramLong1);
		ResultSet ResultSet = PreparedStatement.executeQuery();
		while (ResultSet.next())
			Judge.ReJudge(ResultSet.getLong("solution_id"), paramLong2, Connection);
		PreparedStatement.close();
		CheckOnePS(paramLong1, Connection);
		Connection.close();
	}

	public static void CheckOnePS(long paramLong, Connection paramConnection)
	{
		try
		{
			PreparedStatement PreparedStatement = paramConnection.prepareStatement("select user_id,min(solution_id) as minacc from solution where result=0 and problem_id=? group by user_id");
			PreparedStatement.setLong(1, paramLong);
			ResultSet ResultSet = PreparedStatement.executeQuery();
			while (ResultSet.next())
			{
				String str = ResultSet.getString("user_id");
				long l = ResultSet.getLong("minacc");
				UpdateS(paramLong, str, l, paramConnection);
			}
		} catch (Exception Exception)
		{
			Exception.printStackTrace();
		}
	}

	public static void UpdateS(long paramLong1, String paramString, long paramLong2, Connection paramConnection) throws Exception
	{
		PreparedStatement PreparedStatement = paramConnection.prepareStatement("update solution set valid=0 where problem_id=? and user_id=? and solution_id>?");
		PreparedStatement.setLong(1, paramLong1);
		PreparedStatement.setString(2, paramString);
		PreparedStatement.setLong(3, paramLong2);
		PreparedStatement.executeUpdate();
		PreparedStatement.close();
		PreparedStatement = paramConnection.prepareStatement("update solution set valid=1 where problem_id=? and user_id=? and solution_id<=?");
		PreparedStatement.setLong(1, paramLong1);
		PreparedStatement.setString(2, paramString);
		PreparedStatement.setLong(3, paramLong2);
		PreparedStatement.executeUpdate();
		PreparedStatement.close();
	}

	public static void solution()
	{
		try
		{
			Connection Connection = DBConfig.getConn();
			PreparedStatement PreparedStatement = Connection.prepareStatement("select problem_id,user_id,min(solution_id) as minacc from solution where result=0 group by problem_id,user_id");
			ResultSet ResultSet = PreparedStatement.executeQuery();
			while (ResultSet.next())
			{
				long l1 = ResultSet.getLong("problem_id");
				String str = ResultSet.getString("user_id");
				long l2 = ResultSet.getLong("minacc");
				UpdateS(l1, str, l2, Connection);
			}
			Connection.close();
		} catch (Exception Exception)
		{
			return;
		}
	}

	public static void problem()
	{
		try
		{
			Connection Connection = DBConfig.getConn();
			PreparedStatement PreparedStatement1 = Connection.prepareStatement("update problem set accepted=0,submit=0,submit_user=0,error=0,solved=0");
			PreparedStatement1.executeUpdate();
			PreparedStatement1.close();
			PreparedStatement1 = Connection.prepareStatement("select problem_id,count(*) as accepted from solution where result=0 group by problem_id");
			ResultSet ResultSet = PreparedStatement1.executeQuery();
			int i;
			long l;
			PreparedStatement PreparedStatement2;
			while (ResultSet.next())
			{
				i = ResultSet.getInt("problem_id");
				l = ResultSet.getLong("accepted");
				PreparedStatement2 = Connection.prepareStatement("UPDATE problem SET accepted=? WHERE problem_id=?");
				PreparedStatement2.setLong(1, l);
				PreparedStatement2.setInt(2, i);
				PreparedStatement2.executeUpdate();
				PreparedStatement2.close();
			}
			ResultSet.close();
			PreparedStatement1.close();
			PreparedStatement1 = Connection.prepareStatement("select problem_id,count(*) as submit from solution group by problem_id");
			ResultSet = PreparedStatement1.executeQuery();
			while (ResultSet.next())
			{
				i = ResultSet.getInt("problem_id");
				l = ResultSet.getLong("submit");
				PreparedStatement2 = Connection.prepareStatement("UPDATE problem SET submit=?,ratio=100*accepted/submit WHERE problem_id=?");
				PreparedStatement2.setLong(1, l);
				PreparedStatement2.setInt(2, i);
				PreparedStatement2.executeUpdate();
				PreparedStatement2.close();
			}
			ResultSet.close();
			PreparedStatement1.close();
			PreparedStatement1 = Connection.prepareStatement("select problem_id,count(*) as submit_user from (select problem_id,count(*) as accepted from solution  group by problem_id,user_id)as temp group by problem_id");
			ResultSet = PreparedStatement1.executeQuery();
			while (ResultSet.next())
			{
				i = ResultSet.getInt("problem_id");
				l = ResultSet.getLong("submit_user");
				PreparedStatement2 = Connection.prepareStatement("UPDATE problem SET submit_user=?  WHERE problem_id=?");
				PreparedStatement2.setLong(1, l);
				PreparedStatement2.setInt(2, i);
				PreparedStatement2.executeUpdate();
				PreparedStatement2.close();
			}
			ResultSet.close();
			PreparedStatement1.close();
			PreparedStatement1 = Connection.prepareStatement("select problem_id,count(*) as solved from (select problem_id,count(*) as accepted from solution where result=0 group by problem_id,user_id)as temp group by problem_id");
			ResultSet = PreparedStatement1.executeQuery();
			while (ResultSet.next())
			{
				i = ResultSet.getInt("problem_id");
				l = ResultSet.getLong("solved");
				PreparedStatement2 = Connection.prepareStatement("UPDATE problem SET solved=? WHERE problem_id=?");
				PreparedStatement2.setLong(1, l);
				PreparedStatement2.setInt(2, i);
				PreparedStatement2.executeUpdate();
				PreparedStatement2.close();
			}
			ResultSet.close();
			PreparedStatement1.close();
			PreparedStatement1 = Connection.prepareStatement("select problem_id,count(*) as error from solution where result<>0 and valid=1 group by problem_id");
			ResultSet = PreparedStatement1.executeQuery();
			while (ResultSet.next())
			{
				i = ResultSet.getInt("problem_id");
				l = ResultSet.getLong("error");
				PreparedStatement2 = Connection.prepareStatement("UPDATE problem SET error=?,difficulty=100*error/(error+solved) WHERE problem_id=?");
				PreparedStatement2.setLong(1, l);
				PreparedStatement2.setInt(2, i);
				PreparedStatement2.executeUpdate();
				PreparedStatement2.close();
			}
			ResultSet.close();
			PreparedStatement1.close();
			Connection.close();
		} catch (Exception Exception)
		{
			Exception.printStackTrace();
		}
	}

	public static void users()
	{
		try
		{
			Connection Connection = DBConfig.getConn();
			PreparedStatement PreparedStatement1 = Connection.prepareStatement("select user_id ,count(*) as solved from (select user_id from solution where result=0 group by problem_id,user_id) as temp group by user_id");
			ResultSet ResultSet = PreparedStatement1.executeQuery();
			String str;
			int i;
			PreparedStatement PreparedStatement2;
			while (ResultSet.next())
			{
				str = ResultSet.getString("user_id");
				i = ResultSet.getInt("solved");
				PreparedStatement2 = Connection.prepareStatement("update users set solved=? where user_id=?");
				PreparedStatement2.setInt(1, i);
				PreparedStatement2.setString(2, str);
				PreparedStatement2.executeUpdate();
			}
			ResultSet.close();
			PreparedStatement1.close();
			PreparedStatement1 = Connection.prepareStatement("select user_id,count(*) as submit from solution group by user_id");
			ResultSet = PreparedStatement1.executeQuery();
			while (ResultSet.next())
			{
				str = ResultSet.getString("user_id");
				i = ResultSet.getInt("submit");
				PreparedStatement2 = Connection.prepareStatement("update users set submit=? where user_id=?");
				PreparedStatement2.setInt(1, i);
				PreparedStatement2.setString(2, str);
				PreparedStatement2.executeUpdate();
			}
			ResultSet.close();
			PreparedStatement1.close();
			Connection.close();
		} catch (Exception Exception)
		{
			Exception.printStackTrace();
		}
	}
}
