package com.pku.judgeonline.common;

import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.user.Login;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.*;

import javax.servlet.http.HttpServletRequest;

public class FormattedOut
{
	public FormattedOut()
	{
	}

	public static void printSimpleHead(PrintWriter out, String s)
	{
		ServerConfig.startTimestamp = System.currentTimeMillis();
		if (s == null)
		{
			try
			{
				s = new String(ServerConfig.DEFAULT_TITLE.getBytes("ISO8859_1"), "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
			//out.println("<!DOCTYPE HTML>");
			out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
			out.println(" <head>");
			out.println("   <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
			out.println("   <meta http-equiv=\"Pragma\" content=\"no-cache\">");
			if (s.equals("Problem Status List"))
				out.println("<META HTTP-EQUIV=\"Refresh\" content=\"60\">");
			else if (s.equals("Contest Standing"))
				out.println("<META HTTP-EQUIV=\"Refresh\" content=\"200\">");
			out.println(new StringBuilder().append("   <title>").append(s).append("</title>").toString());
			out.println("   <link rel=\"Shortcut Icon\" href=\"images/favicon.ico\" />");
			out.println("   <link href=\"css/oj.css\" type=\"text/css\" rel=\"Stylesheet\" rev=\"Stylesheet\" media=\"all\"/>");
			out.println("   <link href=\"css/MyStyle.css\" type=\"text/css\" rel=\"Stylesheet\" rev=\"Stylesheet\" media=\"all\"/>");
			out.println("   <script type=\"text/javascript\">var beginTime = new Date().getTime();</script>");
			out.println("   <script type=\"text/javascript\" src=\"js/jquery-1.10.2.min.js\"></script>");
			out.println("   <script type=\"text/javascript\" src=\"js/oj.js\"></script>");
			out.println(" </head>\n");
			out.println("<body>");
			out.println("  <a name=\"top\"></a>");
			out.println("  <div id=\"main\">");
	}

	public static void printContestHead(PrintWriter out, long l, String s, HttpServletRequest request)
	{
		printSimpleHead(out, s);
		String s2 = null;
		String url = null;
		String str = null;
		long ll = 0l;
		out.println("<table border=\"0\" width=\"99%\" background=\"images/table_back.jpg\"><tr><td class=\"Navigation\"><a href=\".\" target=\"_top\">Home Page</a></td><td class=\"Navigation\"><a href=\"bbs\" target=\"_blank\">Web Board</a></td>");

		try
		{
			Connection connection = DBConfig.getConn();
			url = request.getRequestURI();
			str = request.getQueryString();
			if (str != null)
				url = new StringBuilder(url).append("?").append(str).toString();
			if (UserModel.isLoginned(request))
			{
				UserModel usermodel = UserModel.getCurrentUser(request);
				s2 = usermodel.getUser_id();

				PreparedStatement preparedstatement1 = connection.prepareStatement("select new_mail,count(*) as mails from mail where to_user=? and UPPER(defunct)='N' group by new_mail");
				preparedstatement1.setString(1, s2);
				for (ResultSet resultset1 = preparedstatement1.executeQuery(); resultset1.next();)
				{
					int i = resultset1.getInt("new_mail");
					if (i != 0)
						ll = resultset1.getLong("mails");
				}

			}
			
			out.println((new StringBuilder()).append("<td class=\"Navigation\"><a href=\"contests\" target=\"_top\">Contest</a></td><td class=\"Navigation\"><a href=\"showcontest?contest_id=").append(l).append("\" target=\"_top\">Problems</a></td><td class=\"Navigation\"><a href=\"conteststanding?contest_id=" + l + "\">Standing</a></td><td class=\"Navigation\"><a href=\"conteststatus?contest_id=").append(l).append("\" target=\"_top\">Status</a></td><td class=\"Navigation\"><a href=\"conteststatistics?contest_id=").append(l).append(
					"\" target=\"_top\">Statistics</a></td class=\"Navigation\"><td class=\"Navigation\">" + (s2 != null ? ("<a href=\"userstatus?user_id=" + s2 + "\"><font color=\"red\">" + s2 + "</font></a>&nbsp&nbsp<a href=\"login?action=logout&url=" + Tool.urlEncode2(url) + "\">Log Out</a>") : "<a href=\"loginpage?url=" + Tool.urlEncode2(url) + "\"><font color=\"red\">Login</font></a>") + "</td></tr></table>\n").toString());
			//if (ServerConfig.SYSTEM_INFO != null)
			//	out.println((new StringBuilder()).append("<MARQUEE SCROLLAMOUNT=3 BEHAVIOR=ALTERNATE SCROLLDELAY=150><font color=red>").append(ServerConfig.SYSTEM_INFO).append("</font></MARQUEE>").toString());

			PreparedStatement preparedstatement = connection.prepareStatement("select * from announce where start_time<? and end_time>? and UPPER(defunct)='N' order by id");
			preparedstatement.setTimestamp(1, ServerConfig.getSystemTime());
			preparedstatement.setTimestamp(2, ServerConfig.getSystemTime());
			ResultSet resultset = preparedstatement.executeQuery();
			String notice = "<MARQUEE SCROLLAMOUNT=\"3\" BEHAVIOR=\"ALTERNATE\" SCROLLDELAY=\"150\" onMouseOver=\"javascript:this.stop();\" onMouseOut=\"javascript:this.start();\">\n";
			boolean flagOut = false;
			for (; resultset.next();)
			{
				String ID = resultset.getString("id");
				String title = resultset.getString("title");
				String content = resultset.getString("content");
				boolean flagc = true;
				if (content == null || content.equals("") == true)
					flagc = false;
				flagOut = true;
				notice += (flagc ? "<a href=\"showannounce?announce_id=" + ID + "\">" : "") + title + (flagc ? "</a>" : "") + "<br/>\n";
			}
			if (ll != 0l)
			{
				notice += "<img src=\"images/messages.gif\" />You have " + ll + " new <a href=\"mail\" target=\"_top\">mails</a>.\n";
			}
			notice += "</MARQUEE>\n";
			if (flagOut)
				out.println(notice);
		
			String ss = request.getRequestURI();
			String ss1 = request.getQueryString();
			if (ss1 != null)
				ss = ss + "?" + ss1;
			preparedstatement = connection.prepareStatement("update sessions set uri=?,last_activity=UNIX_TIMESTAMP() where session_id=?");
			preparedstatement.setString(1, ss);
			preparedstatement.setString(2, request.getSession().getId());
			preparedstatement.executeUpdate();
			
			connection.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
	}
/*
	public static void printContestHead(PrintWriter out, long l, String s)
	{
		printSimpleHead(out, s);
		out.println("<table border=\"0\" width=\"99%\" background=\"images/table_back.jpg\"><tr><td class=\"Navigation\"><a href=\".\">Home Page</a></td><td class=\"Navigation\"><a href=\"bbs\" target=_blank>Web Board</a></td>");
		out.println((new StringBuilder()).append("<td class=\"Navigation\"><a href=\"contests\" target=\"_top\">Contest</a></td><td class=\"Navigation\"><a href=\"showcontest?contest_id=").append(l).append("\">Problems</a></td><td class=\"Navigation\"><a href=\"conteststanding?contest_id=" + l + "\">Standing</a></td><td class=\"Navigation\"><a href=\"status?contest_id=").append(l).append("\">Status</a></td><td class=\"Navigation\"><a href=\"conteststatistics?contest_id=").append(l).append("\">Statistics</a></td class=\"Navigation\"><td class=\"Navigation\"><a href=\"awardcontest_announce.htm\" target=_blank><font color=red>Award Contest</font></a></td></tr></table>\n").toString());
		if (ServerConfig.SYSTEM_INFO != null)
			out.println((new StringBuilder()).append("<MARQUEE SCROLLAMOUNT=3 BEHAVIOR=ALTERNATE SCROLLDELAY=150><font color=red>").append(ServerConfig.SYSTEM_INFO).append("</font></MARQUEE>").toString());
		String notice = "<MARQUEE width=80% SCROLLAMOUNT=3 BEHAVIOR=ALTERNATE SCROLLDELAY=150 onMouseOver=\"javascript:this.stop();\" onMouseOut=\"javascript:this.start();\">\n";
		boolean flagOut = false;
		try
		{
			Connection connection = DBConfig.getConn();
			PreparedStatement preparedstatement = connection.prepareStatement("select * from announce where start_time<? and end_time>? and UPPER(defunct)='N' order by id");
			preparedstatement.setTimestamp(1, ServerConfig.getSystemTime());
			preparedstatement.setTimestamp(2, ServerConfig.getSystemTime());
			ResultSet resultset = preparedstatement.executeQuery();

			for (; resultset.next();)
			{
				String ID = resultset.getString("id");
				String title = resultset.getString("title");
				String content = resultset.getString("content");
				boolean flagc = true;
				if (content == null || content.equals("") == true)
					flagc = false;
				flagOut = true;
				notice += "<font color=red>" + (flagc ? "<a href=\"showannounce?announce_id=" + ID + "\">" : "") + title + (flagc ? "</a>" : "") + "</font>\n";
			}
			notice += "</MARQUEE>";
			connection.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		
		if (flagOut)
			out.println(notice);
	}
*/
	public static void printHead(PrintWriter out, HttpServletRequest request, Connection connection, String s)
	{
		printSimpleHead(out, s);
		printNavigation(out);
		out.println("       <td class=\"Navigation\">");
		boolean flag = false;
		boolean flag1 = false;
		if (connection == null)
			flag = true;
		String s1 = null;
		Timestamp timestamp = ServerConfig.getSystemTime();
		try
		{
			if (connection == null || connection.isClosed())
			{
				connection = DBConfig.getConn();
				flag = true;
			}
			PreparedStatement preparedstatement = connection.prepareStatement("select contest_id,start_time from contest where end_time>? order by start_time limit 2");
			preparedstatement.setTimestamp(1, timestamp);
			ResultSet resultset = preparedstatement.executeQuery();
			boolean flag2 = false;
			if (resultset.next())
			{
				Timestamp timestamp1 = resultset.getTimestamp("start_time");
				if (timestamp1 != null && timestamp1.before(timestamp))
				{
					s1 = resultset.getString("contest_id");
					if (resultset.next())
					{
						flag1 = true;
						timestamp1 = resultset.getTimestamp("start_time");
						if (timestamp1 != null && timestamp1.before(timestamp))
							flag2 = true;
					}
				} else
				{
					flag1 = true;
				}
			}
			
			String ss = request.getRequestURI();
			String ss1 = request.getQueryString();
			if (ss1 != null)
				ss = ss + "?" + ss1;
			preparedstatement = connection.prepareStatement("update sessions set uri=?,last_activity=UNIX_TIMESTAMP() where session_id=?");
			preparedstatement.setString(1, ss);
			preparedstatement.setString(2, request.getSession().getId());
			preparedstatement.executeUpdate();
			preparedstatement.close();
			preparedstatement = null;
			if (s1 == null)
				out.println("      <font color=\"#1a5cc8\">Current Contest</font><br>");
			else
			{
				if (flag2)
					out.println((new StringBuilder()).append("        <a href=\"contests?type=r\"><font color=\"red\">Current Contest</font></a><br>").toString());
				else
					out.println((new StringBuilder()).append("        <a href=\"showcontest?contest_id=").append(s1).append("\"><font color=\"red\">Current Contest</font></a><br>").toString());
			}
			out.println("        <a href=\"pastcontests\">Past Contests</a><br>");
			if (flag1)
				out.println("        <a href=\"contests\"><font color=\"red\">Scheduled Contests</font></a>");
			else
				out.println("        <a href=\"contests\">Scheduled Contests</a>");
			out.println("      </td>\n      <td  class=\"Navigation\">");
			if (!UserModel.isLoginned(request))
			{
				String url = request.getRequestURI();
				if (request.getQueryString() != null)
					url = new StringBuilder().append(url).append("?").append(request.getQueryString()).toString();
				out.println("        <form method=\"POST\" action=\"login?action=login\">");
				out.println("          User  &nbsp;&nbsp;ID:&nbsp;<input type=text name=user_id1 size=10><br>");
				out.println("          Password:<input type=password name=password1 size=10><br>");
				out.println("          <input type=Submit value=Login name=B1>&nbsp;&nbsp;&nbsp;&nbsp;");
				out.println("          <input type=button value=Register name=B2 onclick=\"location.href='registerpage'\" />");
				out.println((new StringBuilder()).append("          <input type=hidden name=url value=\"").append(Tool.urlEncode(url)).append("\">").toString());
				out.println("        </form>");
			} else
			{
				Login.showMail(request, out, connection, true);
			}
			out.println("      </td>\n    </tr>\n  </table>\n");
			
			if (s == null || s.equals("Announcement") == false)
			{

				preparedstatement = connection.prepareStatement("select * from announce where start_time<? and end_time>? and UPPER(defunct)='N' order by id");
				preparedstatement.setTimestamp(1, ServerConfig.getSystemTime());
				preparedstatement.setTimestamp(2, ServerConfig.getSystemTime());
				resultset = preparedstatement.executeQuery();
				String notice = "    <marquee width=80% SCROLLAMOUNT=10 SCROLLDELAY=130 onMouseOver=\"javascript:this.stop();\" onMouseOut=\"javascript:this.start();\">\n";
				boolean flagOut = false;
				for (; resultset.next();)
				{
					String ID = resultset.getString("id");
					String title = resultset.getString("title");
					String content = resultset.getString("content");
					boolean flagc = true;
					if (content == null || content.equals("") == true)
						flagc = false;
					flagOut = true;
					notice += (flagc ? "    <a href=\"showannounce?announce_id=" + ID + "\">" : "") + title + (flagc ? "</a>" : "") + "<br/>\n";
				}
				notice += "    </marquee>";
				if (flagOut)
					out.println(notice);
			}
			if (flag)
			{
				connection.close();
				connection = null;
			}
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
	}

	public static void printBottom(HttpServletRequest request, PrintWriter out)
	{
		out.println("   </div><!-- div main -->\n");
		out.println("   <div class=\"footer\">");
		out.println("     <img height=29 src=\"images/home.jpg\" width=40 border=0><a href=.>Home Page</a>&nbsp;&nbsp;");
		out.println("     <img height=29 src=\"images/goback.jpg\" width=40 border=0><a href=\"javascript:history.go(-1)\">Go Back</a>&nbsp;&nbsp;");
		out.println("     <img height=29 width=40 border=0 src=\"images/top.jpg\"><a href=\"#top\" target=\"_self\">To top</a>");
		out.println("     <br><hr>");
		out.println("     <div class=\"copyright\">");
		out.println("       <a href=\"http://git.oschina.net/power/poj-power.git\" target=\"_balnk\">Power OJ Rev.20140303</a>|");
		out.println("       <a href=\"faq.htm\">F.A.Q</a>|");
		out.println("       <span id=\"divPageLoadTime\">");
		out.println(System.currentTimeMillis() - ServerConfig.startTimestamp);
		out.println(" ms</span><br>");
		out.println("       All Copyright Reserved 2010-2012 <a href=mailto:power0721@gmail.com><b>power721</b></a><br>");
		out.println(new StringBuilder().append("       Any problem, Please <a href=mailto:").append(ServerConfig.getValue("AdminEmail")).append(">Contact Administrator</a>").toString());
		out.println("     </div>");
		out.println("   </div><!-- div footer -->\n");
		out.println(" </body>");
		out.println("</html>");
	}
	
	public static void printNavigation(PrintWriter out)
	{
		out.println("");
		out.println("   <table align=center width=99% border=1 bordercolor=#FFFFFF style=\"border-collapse: collapse\">");
		out.println("     <tr>");
		out.println("       <td align=middle colSpan=5 id=logo>");
		out.println("         <a href='./'><img border=0 src=images/logo.jpg></a>");
		out.println("       </td>");
		out.println("     </tr>");
		out.println("     <tr vAlign=top align=center bgcolor=#6589D1>");
		out.println("       <th width=20%  class=head>Home</th>");
		out.println("       <th width=20% class=head>Problems</th>");
		out.println("       <th width=20% class=head>Authors</th>");
		out.println("       <th width=20% class=head>Contests</th>");
		out.println("       <th width=20% class=head>User</th>");
		out.println("     </tr>");
		out.println("     <tr vAlign=top align=center bgcolor=#F1F1FD>");
		out.println("       <td class=Navigation>");
		out.println("         <A href=bbs>Web Board</A><br>");
		out.println("         <A href=./>Home Page</A><br>");
		out.println("         <A href=faq.htm target=_blank>F.A.Q</A><br>");
		out.println("         <A href=../judge><font color=red>Virtual Judge</font></A>");
		out.println("       </td>");
		out.println("       <td class=Navigation>");
		out.println("         <form method=get action=gotoproblem>");
		out.println("           <A href=problemlist>Problems</A><br>");
		out.println("           <A href=submitpage>Submit Problem</A><br>");
		out.println("           <A href=status>Status (Online)</A><br>");
		out.println("           <font color=blue>Prob.ID:</font>");
		out.println("           <input type=text name=pid size=6>");
		out.println("           <input type=Submit value=Go name=pb1>");
		out.println("         </form>");
		out.println("       </td>");
		out.println("       <td class=Navigation>");
		out.println("         <form method=GET action=searchuser>");
		out.println("           <a href=registerpage>Register</a><br>");
		out.println("           <a href=modifyuserpage>Update your info</a><br>");
		out.println("           <a href=userlist>User ranklist</a><br>");
		out.println("           <input type=text name=user_id size=10>");
		out.println("           <input type=hidden name=manner value=0>");
		out.println("           <input type=Submit value=Search >");
		out.println("         </form>");
		out.println("       </td>");
	}

}
