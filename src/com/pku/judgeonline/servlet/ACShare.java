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
			String s2 = "<center><h1><font color=blue>AC鍏变韩璁″垝锛圓C Share Project锛�/font></h1></center>";
			out.println(s2);
			s2 = "<TABLE cellSpacing=0 cellPadding=0 align=center width=99% border=1 background=images/table_back.jpg style=\"border-collapse: collapse\" bordercolor=#FFFFFF><tr><td colspan=8>鈥淎C鍏变韩璁″垝鈥濇棬鍦ㄤ績杩涚畻娉曚氦娴侊紝寮�様瑙ｉ鎬濊矾銆�br>";
			out.println(s2);
			s2 = "鍙備笌鈥淎C鍏变韩璁″垝鈥濈殑鐢ㄦ埛鍦ㄨ嚜宸盇C鏌愰浠ュ悗锛屽彲浠ョ湅鍒板弬涓庘�AC鍏变韩璁″垝鈥濈殑鍏朵粬浜鸿繖棰樼殑浠ｇ爜銆�<br>浣犲彲浠ュ涔犲埆浜洪珮鏁堢殑绠楁硶锛岀畝娲佺殑浠ｇ爜銆備絾鏄笉鑳界洿鎺ユ彁浜ゅ埆浜虹殑浠ｇ爜銆�br>浣犲彲浠ユ壘鍑轰唬鐮佷腑鐨勯敊璇紝骞舵姤鍛婄鐞嗗憳锛岀鐞嗗憳鏌ョ湅鏁版嵁鏄惁姝ｇ‘銆�br><br>";
			out.println(s2);
			s2 = "浣嗘槸姣旇禌涓殑浠ｇ爜涓嶈兘娴忚銆�br>";
			out.println(s2);
			int i = 0;
			PreparedStatement preparedstatement = connection.prepareStatement("select count(*) as num from users where share=1");
			ResultSet resultset = preparedstatement.executeQuery();
			if (resultset.next())
				i = resultset.getInt("num");
			preparedstatement = connection.prepareStatement("select user_id,solved from users where share=1 order by solved desc");
			resultset = preparedstatement.executeQuery();
			s2 = (new StringBuilder()).append("涓嬮潰鏄姞鍏C鍏变韩璁″垝鐨勬垚鍛樺悕鍗�").append(i).append("):<br><br></td></tr>").toString();
			out.println("<br>");
			out.println(s2);
			int j = 1;
			out.println("<tr>");
			while (resultset.next())
			{
				out.println((new StringBuilder()).append("<td><a href=userstatus?user_id=").append(resultset.getString("user_id")).append(">").append(resultset.getString("user_id")).append("</a>(<a href=status?result=0&user_id=").append(resultset.getString("user_id")).append(">").append(resultset.getInt("solved")).append("</a>)</td>").toString());
				if (j == 8)
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
				if (s1 != null && s1 != "")
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
						s2 = (new StringBuilder()).append("<form method='get' action='./ACShare'><input type=hidden name=user value=").append(s).append("><input type='submit' value='鎴戜篃瑕佸姞鍏�/>").toString();
						out.println(s2);
					} else
					{
						s2 = "浣犲凡缁忓姞鍏�";
						out.println(s2);
					}
				preparedstatement2.close();
			} else
			{
				s2 = "<a href=loginpage>鐧诲綍鍔犲叆</a>";
				out.println(s2);
			}
			s2 = "</center><br>鍙嬫儏鎻愮ず锛氫负淇濊瘉姝ゅ姛鑳戒笉琚互鐢紝涓嶈缃�鍑哄姛鑳姐�鑻ュ姞鍏ュ悗甯屾湜閫�嚭锛岃鑱旂郴绠＄悊鍛樸�</td></tr></table>";
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
