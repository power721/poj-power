package com.pku.judgeonline.servlet;

import com.pku.judgeonline.common.DBConfig;
import com.pku.judgeonline.common.FormattedOut;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ACShare extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ACShare()
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
		try
		{
			Connection connection = DBConfig.getConn();
			FormattedOut.printHead(out, request, connection, "AC Share Project");
			String s2 = "<center><h1><font color=blue>AC共享计划（AC Share Project）</font></h1></center>";
			out.println(s2);
			s2 = "<TABLE cellSpacing=0 cellPadding=0 align=center width=99% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF><tr><td colspan=8>“AC共享计划”旨在促进算法交流，开阔解题思路。<br>";
			out.println(s2);
			s2 = "参与“AC共享计划”的用户在自己AC某题以后，可以看到参与“AC共享计划”的其他人这题的代码。 <br>你可以学习别人高效的算法，简洁的代码。但是不能直接提交别人的代码。<br>你可以找出代码中的错误，并报告管理员，管理员查看数据是否正确。<br><br>";
			out.println(s2);
			s2 = "但是比赛中的代码不能浏览。<br>";
			out.println(s2);
			int i = 0;
			PreparedStatement preparedstatement = connection.prepareStatement("select count(*) as num from users where share=1");
			ResultSet resultset = preparedstatement.executeQuery();
			if (resultset.next())
				i = resultset.getInt("num");
			preparedstatement = connection.prepareStatement("select user_id,solved from users where share=1 order by solved desc");
			resultset = preparedstatement.executeQuery();
			s2 = (new StringBuilder()).append("下面是加入AC共享计划的成员名单(").append(i).append("):<br><br></td></tr>").toString();
			out.println("<br>");
			out.println(s2);
			int j = 1;
			out.println("<tr>");
			while (resultset.next())
			{
				out.println((new StringBuilder()).append("<td><a href=userstatus?user_id=").append(resultset.getString("user_id")).append(">").append(resultset.getString("user_id")).append("</a>(<a href=status?result=0&user_id=").append(resultset.getString("user_id")).append(">").append(resultset.getInt("solved")).append("</a>)</td>").toString());
				if (j == 7)
				{
					out.println("</tr><tr>");
					j = 0;
				}
				j++;
			}
			for(;j<8;++j)
				out.println((new StringBuilder()).append("<td></td>").toString());
			out.println("</tr>");
			out.println("<tr><td colspan=8><br></td></tr>");
			out.println("<tr><td colspan=8><br><br><br><br><center>");
			if (UserModel.isLoginned(request))
			{
				String s1 = request.getParameter("user");
				String s = UserModel.getCurrentUser(request).getUser_id();
				if (s1 != null && !"".equals(s1))
				{
					if (UserModel.isUser(request, s1))
					{
						PreparedStatement preparedstatement1 = connection.prepareStatement("update users set share=1 where user_id=?");
						preparedstatement1.setString(1, s1);
						preparedstatement1.executeUpdate();
					}
					Tool.GoToURL("ACShare", response);
				}
				PreparedStatement preparedstatement2 = connection.prepareStatement("select share from users where user_id=?");
				preparedstatement2.setString(1, s);
				ResultSet resultset1 = preparedstatement2.executeQuery();
				if (resultset1.next())
					if (resultset1.getInt("share") == 0)
					{
						s2 = (new StringBuilder()).append("<form method='get' action='./ACShare'><input type=hidden name=user value=").append(s).append("><input type='submit' value='我也要加入'/>").toString();
						out.println(s2);
					} else
					{
						s2 = "你已经加入.";
						out.println(s2);
					}
				preparedstatement2.close();
			} else
			{
				s2 = "<a href=loginpage>登录加入</a>";
				out.println(s2);
			}
			s2 = "</center><br>友情提示：为保证此功能不被滥用，不设置退出功能。若加入后希望退出，请联系管理员。</td></tr></table>";
			out.println(s2);
			FormattedOut.printBottom(request, out);
			out.close();
			preparedstatement.close();
			connection.close();
			return;
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
		}
	}

	public void destroy()
	{
	}
}
