package com.pku.judgeonline.admin.common;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.problemset.SubmitServlet;

import java.io.*;
import java.sql.*;
import java.util.StringTokenizer;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class Tools extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Tools()
	{
	}

	public void init() throws ServletException
	{
	}

	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!UserModel.isAdminLoginned(request))
			return;
		Connection connection = null;
		String s = request.getParameter("op");
		String s1 = request.getParameter("contest_only");
		if (s1 != null)
		{
			if (s1.equals("true"))
				SubmitServlet.contest_only = true;
			else
				SubmitServlet.contest_only = false;
			out.println("<p>The servlet has received a GET. This is the reply.</p>");
			out.println("<font  size=\"3\"><a href=\"javascript:history.go(-1)\">Go Back</a>");
			FormattedOut.printBottom(out);
			return;
		}
		if (s == null)
			return;
		try
		{
			s = s.trim();
			if (s.equalsIgnoreCase("test"))
			{
				connection = DBConfig.getConn();
				PreparedStatement preparedstatement = connection.prepareStatement("select problem_id,count(distinct(user_id)) as submit_user from solution group by problem_id");
				PreparedStatement preparedstatement1;
				for (ResultSet resultset = preparedstatement.executeQuery(); resultset.next(); preparedstatement1.close())
				{
					preparedstatement1 = connection.prepareStatement("update problem set submit_user=? where problem_id=?");
					preparedstatement1.setLong(1, resultset.getLong("submit_user"));
					preparedstatement1.setLong(2, resultset.getLong("problem_id"));
					preparedstatement1.executeUpdate();
				}

				preparedstatement.close();
				connection.close();
			}
			if (s.equals("addusers"))
			{
				String s2 = request.getParameter("contest_id");
				BufferedReader bufferedreader = new BufferedReader(new FileReader("c:\\a.txt"));
				connection = DBConfig.getConn();
				String s4;
				while ((s4 = bufferedreader.readLine()) != null)
				{
					StringTokenizer stringtokenizer = new StringTokenizer(s4, "|");
					String s5 = stringtokenizer.nextToken().trim();
					String s6 = stringtokenizer.nextToken().trim();
					String s7 = stringtokenizer.nextToken();
					s7 = stringtokenizer.nextToken().trim();
					PreparedStatement preparedstatement2 = connection.prepareStatement("SELECT * FROM attend WHERE UPPER(user_id) = UPPER(?) and contest_id=?");
					preparedstatement2.setString(1, s5);
					preparedstatement2.setString(2, s2);
					ResultSet resultset1 = preparedstatement2.executeQuery();
					if (resultset1.first())
					{
						preparedstatement2.close();
					} else
					{
						preparedstatement2.close();
						preparedstatement2 = connection.prepareStatement("insert into users (user_id,nick,password) values(?,?,encode(?,?))");
						preparedstatement2.setString(1, s5);
						preparedstatement2.setString(2, s6);
						preparedstatement2.setString(3, s7);
						preparedstatement2.setString(4, ServerConfig.ENCODE_STRING);
						preparedstatement2.executeUpdate();
						preparedstatement2.close();
						preparedstatement2 = connection.prepareStatement("insert into attend (user_id,nick,contest_id) values(?,?,?)");
						preparedstatement2.setString(1, s5);
						preparedstatement2.setString(2, s6);
						preparedstatement2.setString(3, s2);
						preparedstatement2.executeUpdate();
						preparedstatement2.close();
					}
				}
				bufferedreader.close();
				connection.close();
				connection = null;
			}
			if (s.equals("setsysteminfo"))
			{
				String s3 = request.getParameter("sinfo");
				if (s3 == null || s3.equals(""))
					ServerConfig.SYSTEM_INFO = null;
				else
					ServerConfig.SYSTEM_INFO = s3;
			}
			if (s.equals("resetsysteminfo"))
			{

				ServerConfig.SYSTEM_INFO = null;
			}

			if (s.equals("addsysteminfo"))
			{
				String s3 = request.getParameter("addsinfo");
				String s2 = ServerConfig.SYSTEM_INFO;
				if (s3 != null && s3.equals("") == false)
				{
					if (s2 != null && s2.equals("") == false)
						ServerConfig.SYSTEM_INFO += "<br>";
					else
						ServerConfig.SYSTEM_INFO = "";
					ServerConfig.SYSTEM_INFO += s3;
					String user = UserModel.getCurrentUser(request).getUser_id();
					ServerConfig.SYSTEM_INFO += "&nbsp;&nbsp;&nbsp;&nbsp;<font color=black>----</font><a href=sendpage?to=" + user + "><font color=black>";
					ServerConfig.SYSTEM_INFO += user;
					ServerConfig.SYSTEM_INFO += "</font></a>";
				}

			}
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
			try
			{
				if (connection != null)
					connection.close();
			} catch (Exception exception1)
			{
				exception1.printStackTrace(System.err);
			}
		}
		out.println("<html>");
		out.println("<head><title>Tools</title></head>");
		out.println("<body bgcolor=\"#ffffff\">");
		out.println("<p>The servlet has received a GET. This is the reply.</p>");
		out.println((new StringBuilder()).append("time:").append(new Timestamp(System.currentTimeMillis())).toString());
		out.println("<font  size=\"3\"><a href=\"javascript:history.go(-1)\">Go Back</a>");
		FormattedOut.printBottom(out);
		out.println("</body></html>");
	}

	public void destroy()
	{
	}
}
