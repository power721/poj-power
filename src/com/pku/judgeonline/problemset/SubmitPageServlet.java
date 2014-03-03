package com.pku.judgeonline.problemset;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class SubmitPageServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SubmitPageServlet()
	{
	}

	public void init() throws ServletException
	{
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		int i, pid = 0;
		long l;
		String submit = request.getParameter("oj");
		PrintWriter out;
		Connection connection;
		try
		{
			pid = i = Integer.parseInt(request.getParameter("problem_id"));
		} catch (NumberFormatException numberformatexception)
		{
			i = 0;
		}
		l = 0L;
		try
		{
			l = Integer.parseInt(request.getParameter("contest_id"));
		} catch (NumberFormatException numberformatexception1)
		{
			l = 0L;
		}
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		// response.setCharacterEncoding("UTF-8");
		out = response.getWriter();
		if (!UserModel.isLoginned(request))
		{
			Tool.forwardToUrl(request, response, (new StringBuilder()).append("loginpage?url=").append(Tool.encodeUrl((new StringBuilder()).append("submitpage?problem_id=").append(i).append(l == 0L ? "" : (new StringBuilder()).append("&contest_id=").append(l).toString()).toString())).toString());
			return;
		}
		connection = DBConfig.getConn();
		PreparedStatement preparedstatement;
		String s1;
		if (l == 0L)
			FormattedOut.printHead(out, request, connection, "Submit Your Solution Via Web");
		else
			FormattedOut.printContestHead(out, l, "Submit Your Solution Via Web", request);
		if (submit == "" || submit == null)
			submit = "./submit";
		out.println("<script language=\"javascript\" type=\"text/javascript\" src=\"js/editarea/edit_area/edit_area_full.js\"></script>");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("editAreaLoader.init({");
		out.println("  id : \"source\"   // textarea id");
		out.println("  ,allow_resize: \"both\"");
		out.println("  ,display: \"later\"");
		out.println("  ,font_size: \"12\"");
		out.println("  ,replace_tab_by_spaces: \"4\"");
		out.println("  ,syntax: \"cpp\"      // syntax to be uses for highgliting");
		out.println("  ,syntax_selection_allow: \"cpp,c,pas,java,python\"");
		out.println("  ,toolbar: \"search, go_to_line, fullscreen, |, undo, redo, |, select_font,syntax_selection,|, change_smooth_selection, highlight, reset_highlight, word_wrap, |, help\"");
		out.println("  ,start_highlight: true    // to display with highlight mode on start-up");
		out.println("});");
		out.println("</script>");
		out.println("<table border=0 width=99% background=images/table_back.jpg><tr><td>");
		out.println("<form method=POST action=" + submit + ">");
		out.println("<p align=\"center\"><font size=\"4\" color=\"#333399\">Submit Your Solution Via Web</font></p>");
		out.println("<p align=\"center\">");
		try
		{
			ResultSet resultset;
			if (l != 0L && i <= 26)
			{
				preparedstatement = connection.prepareStatement("select problem_id from contest_problem where contest_id=? and num=?");
				preparedstatement.setLong(1, l);
				preparedstatement.setLong(2, i);
				resultset = preparedstatement.executeQuery();
				if (!resultset.next())
				{
					ErrorProcess.Error((new StringBuilder()).append("Can not find contest problem (ID:").append(pid).append(")<br><br>").toString(), out);
					preparedstatement.close();
					connection.close();
					return;
				}
				i = resultset.getInt("problem_id");
			}
			out.println((new StringBuilder()).append("Problem ID:<input type=\"text\" name=\"problem_id\" value=\"").append(i != pid ? "" + (char) (pid + 65) : i).append("\" size=\"20\" accesskey=p><br>").toString());
			preparedstatement = connection.prepareStatement("select title,contest_id from problem where problem_id=?");
			preparedstatement.setInt(1, i);
			resultset = preparedstatement.executeQuery();
			s1 = "<font color=red>No such problem.</font>";
			long l1 = 0L;
			if (resultset.next())
			{
				s1 = resultset.getString("title");
				l1 = resultset.getLong("contest_id");
			}
			if (l1 != 0L)
			{
				PreparedStatement preparedstatement2 = connection.prepareStatement("select * from contest where contest_id=? and UPPER(defunct)='N'");
				preparedstatement2.setLong(1, l1);
				ResultSet resultset2 = preparedstatement2.executeQuery();
				if (resultset2.next())
				{
					int j1 = resultset2.getInt("private");
					boolean flag = j1 > 0;
					if (flag && !Tool.permission(connection, request, l1))
					{
						ErrorProcess.Error("It's a private contest.You have no permission.", out);
						return;
					}
				}
				out.println("<input type=hidden name=contest_id value=" + l1 + ">");
			}

			// out.println((new
			// StringBuilder()).append("Problem Title: <b><font color=blue><a href=showproblem?problem_id=").append(pid).append("&contest_id="+l+">").append(s1).append("</a></font></b><br>").toString());
			out.println(new StringBuilder().append("Problem Title:<span id=ptitle>").append("<a href=showproblem?problem_id=").append(pid).append("&contest_id=" + l + ">").append(s1).append("</a>").append("</span><br>").toString());
			preparedstatement.close();
		} catch (SQLException sqlexception)
		{
			sqlexception.printStackTrace(System.out);
		}
		out.println("Language:<select size=\"1\" name=\"language\" style=\"width:100;\" accesskey=l>");
		int j = 1;
		try
		{
			if (UserModel.isLoginned(request))
			{
				String s = UserModel.getCurrentUser(request).getUser_id();
				PreparedStatement preparedstatement1 = connection.prepareStatement("select language from users where user_id=?");
				preparedstatement1.setString(1, s);
				ResultSet resultset1 = preparedstatement1.executeQuery();
				if (resultset1.next())
					j = resultset1.getInt("language");
				preparedstatement1.close();
			}
			connection.close();
		} catch (Exception exception)
		{
			exception.printStackTrace(System.err);
			try
			{
				connection.close();
			} catch (Exception exception1)
			{
				exception1.printStackTrace(System.err);
			}
		}
		int k = LanguageType.getLangs();
		String as[] = LanguageType.GetDescriptions();
		for (int i1 = 0; i1 < k; i1++)
			out.println((new StringBuilder()).append("<option value=").append(i1).append(i1 != j ? ">" : " selected>").append(Tool.titleEncode(as[i1])).append("</option>").toString());

		out.println("</select><br>");
		out.println("<p align=\"center\">");
		out.println("Source: <br>");
		out.println("<textarea id=\"source\" rows=\"30\" name=\"source\" cols=\"100\" accesskey=c></textarea></p>");
		out.println("<div align=\"center\">");
		out.println("<pre><input type=\"submit\" value=\"Submit\" name=\"submit\" accesskey=s><input type=\"reset\" value=\"Reset\" name=\"reset\" ></pre>");
		out.println("</div>");
		out.println("</form>");
		out.println("</td></tr></table>");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("$(\"input[name='problem_id']\").keyup(function(){");
		out.println("	  $(\"#ptitle\").load(\"ajax?name=pid&val=\"+this.value"+(l!=0l?("+\"&cid="+l+"\""):"")+");");
		// out.println("	  $(\"#ptitle\").load(\"data.txt\");");
		out.println("	});");
		out.println("</script>");
		FormattedOut.printBottom(request, out);
		return;
	}

	public void destroy()
	{
	}
}
