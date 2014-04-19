package com.pku.judgeonline.user;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class UserStatus extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserStatus()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out;
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		String s;
		Connection connection;
		s = request.getParameter("user_id");
		connection = DBConfig.getConn();
		try
		{
			if (s == null || s.trim().equals(""))
			{
				FormattedOut.printHead(out, request, connection, "Error -- no user found");
				connection.close();
				connection = null;
				FormattedOut.printBottom(request, out);
				return;
			}

			PreparedStatement preparedstatement;
			ResultSet resultset;
			preparedstatement = connection.prepareStatement("select * from users where user_id=?");
			preparedstatement.setString(1, s);
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error((new StringBuilder()).append("Sorry,").append(s).append(" doesn't exist").toString(), out);
				resultset.close();
				preparedstatement.close();
				connection.close();
				return;
			}
			int i;
			int j;
			String s1;
			String s2;
			String s3;
			String s4;
			String s5;
			s1 = resultset.getString("email");
			s2 = resultset.getString("nick");
			s3 = resultset.getString("school");
			Timestamp timestamp = resultset.getTimestamp("accesstime");
			s4 = "";
			if (timestamp != null)
				s4 = timestamp.toString();
			s5 = resultset.getString("ip");
			i = resultset.getInt("solved");
			j = resultset.getInt("submit");
			resultset.close();
			preparedstatement.close();
			preparedstatement = connection.prepareStatement("select user_id,solved,submit,url,qq  from users WHERE UPPER(defunct) = 'N' ORDER BY solved DESC,submit ASC");
			resultset = preparedstatement.executeQuery();
			if (!resultset.next())
			{
				ErrorProcess.Error("Sorry,no user exists", out);
				resultset.close();
				preparedstatement.close();
				connection.close();
				return;
			}

			try
			{
				int k = 1;
				int l = 1;
				int qq = 0;
				do
				{
					if (resultset.getString("user_id").toUpperCase().equals(s.toUpperCase()))
					{
						qq = resultset.getInt("qq");
						break;
					}
					k++;
				} while (resultset.next());
				l = k - 3;
				if (l < 1)
					l = 1;
				FormattedOut.printHead(out, request, connection, (new StringBuilder()).append("User -- ").append(s).toString());
				out.println("<center>");
				PreparedStatement preparedstatement2;
				ResultSet resultset2;
				PreparedStatement preparedstatement3;
				ResultSet resultset3;
				preparedstatement2 = connection.prepareStatement("select rightstr from privilege where user_id=? and upper(defunct)='N' and rightstr='Administrator'");
				preparedstatement2.setString(1, s);
				resultset2 = preparedstatement2.executeQuery();
				boolean flag4 = false;
				if (resultset2.next())
				{
					// String s9 = resultset2.getString("rightstr");
					// if (s9.equalsIgnoreCase("administrator"))
					flag4 = true;
					// System.out.println(flag4+" "+s9+" "+s);
				}
				preparedstatement3 = connection.prepareStatement("select * from privilege where user_id=? and (UPPER(rightstr)='ADMINISTRATOR' or rightstr='MEMBER')");
				preparedstatement3.setString(1, s);
				resultset3 = preparedstatement3.executeQuery();
				if (resultset3.next())
				{
				}
				out.println((new StringBuilder()).append((flag4 ? "<img src=images/icon_profile.png></img>" : "") + "<font size=5 color=blue><a href=sendpage?to=").append(s).append(">").append(s).append("--").append(Tool.titleEncode(connection, s, s2)).append("</a></font><br>").toString());
				out.println((new StringBuilder()).append("Last Loginned Time:").append(s4).append("<br>").toString());
				if (UserModel.isAdminLoginned(request))
					out.println((new StringBuilder()).append("Last Loginned IP:").append(s5).append("<br>").toString());
				out.println("<TABLE cellSpacing=0 cellPadding=0 width=70% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
				File file = null;
				boolean flagFile = false;
				String File = "";
				String img = "";
				String ss[] =
				{ ".gif", ".jpg", ".png", ".bmp" };
				String indexDir = this.getServletContext().getRealPath("/images/user");
				int ii = 0;
				
				for (ii = 0; ii < 4; ii++)
				{
					File = indexDir + "/" + s + ss[ii];
					file = new File(File);
					flagFile = file.exists();
					if (flagFile)
						break;
				}
				if (flagFile)
				{
					img = "<img src=images/user/" + s + ss[ii] + " border=0 onload='javascript:DrawImage(this);'/>";
					/*
					 * if(url!=null) {img=
					 * "<td align=\"center\" valign=\"middle\" colspan=2><a href="
					 * +url+"><img src=/oj/images/user/"+s+ss[ii]+
					 * " border=0 onload='javascript:DrawImage(this);' alt=\"Contact me!\" title=\"Contact me!\"/></a></td>"
					 * ; }
					 */
				}
				out.print("<tr><td align=\"center\" valign=\"middle\" colspan=2>" + img + "</td><td align=\"center\" valign=\"middle\" colspan=2><br>");
				out.print("<form action=usercmp method=get>");
				out.print((new StringBuilder()).append("Compare <input type=text size=10 name=uid1 value=").append(s).append(">").toString());
				String s6 = "";
				UserModel usermodel = UserModel.getCurrentUser(request);
				if (usermodel != null)
					s6 = usermodel.getUser_id();
				out.print((new StringBuilder()).append("and <input type=text size=10 name=uid2 value=").append(s6).append(">").toString());
				out.print("<input type=submit value=GO></form>");
				out.print("</td></tr>");
				out.println("<tr><td width=15% align=left>Rank:</td>");
				out.println((new StringBuilder()).append("<td align=center width=25%><font color=red>").append(k).append("</font></td>").toString());
				out.println("<td width=60% align=center>Solved Problems List</td> </tr>");
				out.println("<tr><td width=15% align=left>Solved:</td>");
				out.println((new StringBuilder()).append("<td align=center width=25%><a href=\"status?result=0&user_id=").append(s).append("\">").append(i).append("</a></td>").toString());
				out.println("<td width=60% align=center rowspan="+(qq!=0?5:4)+">");
				PreparedStatement preparedstatement1 = connection.prepareStatement("select problem_id from solution where user_id=? and result=0 group by problem_id");
				preparedstatement1.setString(1, s);
				ResultSet resultset1;
				int i1;
				for (resultset1 = preparedstatement1.executeQuery(); resultset1.next(); out.println((new StringBuilder()).append("<a href=\"status?problem_id=").append(i1).append((new StringBuilder()).append("&user_id=").append(s).append("&result=0&language=\">").toString()).append(i1).append(" </a>").toString()))
					i1 = resultset1.getInt("problem_id");

				resultset1.close();
				preparedstatement1.close();
				out.println("</td></tr>");
				out.println("<tr><td width=15% align=left>Submissions:</td>");
				out.println((new StringBuilder()).append("<td align=center width=25%><a href=\"status?user_id=").append(s).append("\">").append(j).append("</a></td></tr>").toString());
				out.println("<tr><td width=15% align=left>School:</td>");
				out.println((new StringBuilder()).append((new StringBuilder()).append("<td align=center width=25%><a href=\"searchuser?user_id=").append(Tool.urlEncode(s3)).append("&manner=4\">").toString()).append(Tool.titleEncode(connection, s, s3)).append("</a></td></tr>").toString());
				out.println("<tr><td width=15% align=left>Email:</td>");
				out.println((new StringBuilder()).append("<td align=center width=25%>").append(Tool.titleEncode(s1)).append("</td></tr>").toString());

				if (qq != 0)
				{
					out.println("<tr><td width=15% align=left>QQ:</td><td align=center width=25%>");
					out.println("<a target=\"_blank\" href=\"http://wpa.qq.com/msgrd?v=3&uin=" + qq + "&site=qq&menu=yes\"><img border=\"0\" src=\"http://wpa.qq.com/pa?p=2:" + qq + ":41 &r=0.5409521391365766\" alt=\"Contact me!\" title=\"Contact me!\"></a>");
					out.println("</td></tr>");
				}
				out.println("</table><font size=5>Neighbours:</font><br>");
				out.println("<TABLE width=70% border=1 bordercolorlight=#FFFFFF bordercolordark=#FFFFFF style=\"border-collapse: collapse\">");
				out.println("<tr BGCOLOR=#6589D1><td width=10%>Rank</td><td width=40%>User</td><td width=25%>Solved</td>");
				out.println("<td width=25%>Submissions</td></tr>");
				resultset.absolute(l);
				int j1 = l;
				int k1 = 1;
				do
				{
					String s7;
					if (j1 == k)
						s7 = "#ffffff";
					else if (k1 % 2 != 0)
						s7 = "#C0C0C0";
					else
						s7 = "#78C8FF";
					out.println((new StringBuilder()).append("<tr bgcolor=").append(s7).append("><td width=10%>").append(j1).append("</td>").toString());
					out.println((new StringBuilder()).append("<td width=40%><a href=\"userstatus?user_id=").append(resultset.getString("user_id")).append("\">").append(resultset.getString("user_id")).append("</a></td>").toString());
					out.println((new StringBuilder()).append("<td width=25%>").append(resultset.getInt("solved")).append("</td>").toString());
					out.println((new StringBuilder()).append("<td width=25%>").append(resultset.getInt("submit")).append("</td></tr>").toString());
					j1++;
					k1++;
				} while (resultset.next() && j1 < k + 4);
				resultset.close();
				preparedstatement.close();
				connection.close();
				out.println("</table>");
				out.println("</center>");
				FormattedOut.printBottom(request, out);
			} catch (SQLException sqlexception1)
			{
				sqlexception1.printStackTrace(System.out);
			}
		} catch (SQLException sqlexception)
		{
			sqlexception.printStackTrace(System.out);
		}
		return;
	}

	public void destroy()
	{
	}
}
