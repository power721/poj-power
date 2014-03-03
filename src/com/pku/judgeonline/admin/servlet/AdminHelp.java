package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AdminHelp extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AdminHelp()
	{
	}

	public void init() throws ServletException
	{
		ServerConfig.init();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
		{
			return;
		} else
		{
			FormattedOut.printHead(out, "Admin Help");
			out.println("<center><textarea class=\"help\">");
			out.println("tomcat");
			out.println("加文件代码");
			out.println("");
			out.println("<a href=\"../gongju/111.txt\">aaa</a>");
			out.println("文件放的地址和图片一样");
			out.println("链接:");
			out.println("<a href=\"bbs\"  target=\"_blank\">BBS</a>");
			out.println("");
			out.println("放图片代码");
			out.println("<img src=\"images/1356_1.jpg\" />");
			out.println("<img src=\"images/1356_1.jpg\" alt=\"picture\" title=\"picture\" />");
			out.println("");
			out.println("");
			out.println("html:");
			out.println("<br>\t\t(换行)");
			out.println("&amp;amp;           & ");
			out.println("&amp;lt;            < ");
			out.println("&amp;gt;            > ");
			out.println("&amp;quot;          \\ ");
			out.println("&amp;nbsp;\t\t(空格)");
			out.println("");
			out.println("加粗: <b>加粗文字</b>");
			out.println("下划线:<u>下划线文字</u>");
			out.println("删除线:<del>删除线文字</del>");
			out.println("字体:<font color=red size=6>文字</font>");
			out.println("");
			out.println("<i> 显示斜体文本效果。 ");
			out.println("<em> 把文本定义为强调的内容。 ");
			out.println("<strong> 把文本定义为语气更强的强调的内容。 ");
			out.println("<blockquote> 标签定义块引用。");
			out.println("<center> 标签对其所包括的文本进行水平居中。");
			out.println("<hr> 标签在 HTML 页面中创建一条水平线。");
			out.println("<sub> 标签可定义下标文本。");
			out.println("<sup> 标签可定义上标文本。");
			out.println("");
			out.println("");
			out.println("");
			out.println("HTML 4.01 参考手册");
			out.println("http://www.w3school.com.cn/tags/index.asp");
			out.println("</textarea>");
			out.println("");
			out.println("");
			out.println("</center>");
			FormattedOut.printBottom(out);
			return;
		}
	}

	public void destroy()
	{
	}
}
