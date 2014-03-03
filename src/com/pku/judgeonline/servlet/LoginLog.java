package com.pku.judgeonline.servlet;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class LoginLog extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LoginLog()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		if (!UserModel.isLoginned(request))
		{
			Tool.GoToURL(".", response);
			return;
		}
		String user = request.getParameter("user");
		String log = request.getParameter("log");
		String log_id = request.getParameter("id");
		String op = request.getParameter("op");
		long id = 0L;
		try
		{
			if (log_id != null)
			{
				id = Integer.parseInt(log_id);
			}
		} catch (NumberFormatException numberformatexception)
		{
		}
		int l = 20;
		if (log != null && log != "")
			l = Integer.parseInt(log);
		if (l <= 0)
			l = 20;
		if (l > 200)
			l = 200;
		try
		{
			Connection connection = DBConfig.getConn();
			UserModel usermodel = UserModel.getCurrentUser(request);
			String s = usermodel.getUser_id();
			boolean flag = UserModel.isAdminLoginned(request);
			if (user != null && user != "" && flag)
				s = user;
			FormattedOut.printHead(out, request, connection, "Login Log");
			PreparedStatement preparedstatement;
			ResultSet resultset;
			if (id != 0L)
			{
				preparedstatement = connection.prepareStatement("SELECT * FROM loginlog l where log_id=?");
				preparedstatement.setLong(1, id);
				resultset = preparedstatement.executeQuery();
				if (!resultset.next())
				{
					ErrorProcess.Error("No such log", out);
					preparedstatement.close();
					preparedstatement = null;
					connection.close();
					connection = null;
					return;
				}
				String s1 = resultset.getString("user_id");
				if (!flag && !s1.equals(s))
				{
					ErrorProcess.Error("You have no permission.", out);
					preparedstatement.close();
					preparedstatement = null;
					connection.close();
					connection = null;
					return;
				}
				String s2 = resultset.getString("time");
				String s3 = resultset.getString("ip");
				String s4 = resultset.getString("succeed");
				String s5 = resultset.getString("info");
				String s6 = "<font color=blue>Succeeded</font>";
				if (s4.equals("N"))
					s6 = "<font color=red>Failed</font>";
				String s7 = getClientOS(s5);
				out.println("<center><TABLE cellSpacing=0 cellPadding=0 width=70% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
				out.println("<tr>");
				out.println("<td>User ID:</td><td><a href=userstatus?user_id=" + s1 + ">" + s1 + "</a></td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td>Time:</td><td>" + s2 + "</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td>IP:</td><td>" + s3 + " ");
				String IPinfo = "<script language=\"javascript\" type=\"text/javascript\">document.write(remote_ip_info['country']+remote_ip_info['province']+remote_ip_info['city']+' '+remote_ip_info['isp']+' '+remote_ip_info['type']+' '+remote_ip_info['desc']);</script>";
				out.println("<script language=\"javascript\" type=\"text/javascript\" src=\"http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=" + s3 + "\"></script>" + IPinfo + "</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td>Status:</td><td>" + s6 + "</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td>System:</td><td>" + s7 + "</td>");
				out.println("</tr>");
				out.println("<tr>");
				out.println("<td>Detail:</td><td>" + s5 + "</td>");
				out.println("</tr>");

				out.println("</table></center>");
			}
			if (op != null && op.equals("list") && flag)
			{
				preparedstatement = connection.prepareStatement("SELECT * FROM loginlog l order by time desc limit ?");
				preparedstatement.setInt(1, l);
				resultset = preparedstatement.executeQuery();
			} else
			{
				preparedstatement = connection.prepareStatement("SELECT * FROM loginlog l where user_id=? order by time desc limit ?");
				preparedstatement.setString(1, s);
				preparedstatement.setInt(2, l);
				resultset = preparedstatement.executeQuery();
			}
			out.println((new StringBuilder()).append("<p align=\"center\" style=\"font-family:Times;color:#333399;font-size:14pt\">Last " + l + " Login Attempt(s) of ").append(s).append("</p>").toString());
			if (flag)
			{
				out.println("<a href=loginlog?op=list>List All</a>");
				out.println("<form method=GET action=loginlog>User:&nbsp<input type=text name=user size=10 value=" + s + ">Log:&nbsp<input type=text name=log value=" + l + " size=5>&nbsp<input type=Submit value=Loginlog></form>");
			}
			out.println("<center><TABLE cellSpacing=0 cellPadding=0 width=70% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.println("<tr align=center bgcolor=#6589D1>");
			out.println("<td width=\"10%\" ><b><font color=white>No.</font></b></td>");
			out.println("<td width=\"10%\" ><b><font color=white>User</font></b></td>");
			out.println("<td width=\"30%\" ><b><font color=white>Time</font></b></td>");
			out.println("<td width=\"35%\" ><b><font color=white>IP</font></b></td>");
			out.println("<td width=\"15%\" ><b><font color=white>Status</font></b></td>");
			out.println("</tr>");
			int i = 1;
			for (; resultset.next(); out.println("</tr>"))
			{
				out.println("<tr align=center>");
				log_id = resultset.getString("log_id");
				String s5 = resultset.getString("user_id");
				String s1 = resultset.getString("time");
				String s2 = resultset.getString("ip");
				String s3 = resultset.getString("succeed");
				String s4 = "<font color=blue>Succeeded</font>";
				if (s3.equals("N"))
					s4 = "<font color=red>Failed</font>";
				out.println("<td width=\"10%\" align=\"center\">" + i + "</td>");
				out.println("<td><a href=userstatus?user_id=" + s5 + ">" + s5 + "</a></td>");
				out.println("<td>" + s1 + "</td>");
				out.println("<td>" + s2 + "</td>");
				out.println("<td><a href=loginlog?user=" + s + "&log=" + l + "&op=" + op + "&id=" + log_id + ">" + s4 + "</a></td>");
				i++;
			}
			out.println("</table></center>");
			FormattedOut.printBottom(request, out);
			
			resultset.close();
			preparedstatement.close();
			connection.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		out.close();
	}

	public static String getClientOS(String userAgent)
	{
		String cos = "unknow os";
		if (userAgent == null)
			return cos;
		Pattern p = Pattern.compile(".*(Windows NT 6\\.1).*");
		Matcher m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "Win 7";
			return cos;
		}
		p = Pattern.compile(".*(Windows NT 5\\.1|Windows XP).*");
		m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "WinXP";
			return cos;
		}
		p = Pattern.compile(".*(Windows NT 5\\.2).*");
		m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "Win2003";
			return cos;
		}
		p = Pattern.compile(".*(Win2000|Windows 2000|Windows NT 5\\.0).*");
		m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "Win2000";
			return cos;
		}

		p = Pattern.compile(".*(Mac|apple|MacOS8).*");
		m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "MAC";
			return cos;
		}

		p = Pattern.compile(".*(WinNT|Windows NT).*");
		m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "WinNT";
			return cos;
		}

		p = Pattern.compile(".*Linux.*");
		m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "Linux";
			return cos;
		}

		p = Pattern.compile(".*(68k|68000).*");
		m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "Mac68k";
			return cos;
		}

		p = Pattern.compile(".*(9x 4.90|Win9(5|8)|Windows 9(5|8)|95/NT|Win32|32bit).*");
		m = p.matcher(userAgent);
		if (m.find())
		{
			cos = "Win9x";
			return cos;
		}

		return cos;
	}

	public void destroy()
	{
	}
}
