package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;

public class Task extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public Task()
	{
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy()
	{
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		PrintWriter out = response.getWriter();
		FormattedOut.printHead(out, "Task List");
		/*
		 * out.println("<script LANGUAGE = \"JavaScript\" >");
		 * out.println(" function  checkvalue(name, pid)"); out.println("   {");
		 * out.println("   document.form1.name.value=name;");
		 * out.println("   document.form1.pid.value=pid;");
		 * out.println("   document.form1.submit();");
		 * out.println("   alert('已提交');"); out.println("   return true;");
		 * out.println(""); out.println(" }"); out.println("</script>");
		 */
		String cmd = "tasklist /V";
		String str = request.getParameter("op");
		if (!"all".equals(str))
		{
			cmd = "tasklist /V /FI \"IMAGENAME eq Main.exe\"";
		}
		String op = "";
		Process pro = null;
		try
		{
			pro = Runtime.getRuntime().exec(cmd);
		} catch (IOException e)
		{
			ErrorProcess.ExceptionHandle(e, out);
			return;
		}
		BufferedReader bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		long pid = 0;
		out.println("<div style=\"margin:20px\" class=\"task-list\" width=%80>");
		out.println("<p>本页面显示正在运行的评测程序进程信息<br></p>");
		out.println("<table>");
		while ((str = bfr.readLine()) != null)
		{
			op = "<tr><td><pre>";
			if (str.startsWith("Main.exe"))
			{
				pid = Long.parseLong(getNumbers(str));
				// System.out.println(pid);
				op += "<a href=\"admin.taskkill?pid=" + pid + "\">Kill</a>\t";
			}
			// out.println("<input type=\"hidden\" name=\"name\" value=\"\">");
			// out.println("<input type=\"hidden\" name=\"pid\" value=\"\">");
			out.println(op + "</pre></td><td><pre>" + str + "</pre></td></tr>");
			// System.out.println(bfr.readLine());
		}
		out.println("</table></div>");
		try
		{
			int k = pro.waitFor();
			out.println(k);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cmd = "VBoxManage list runningvms";
		//cmd = "D:\\Program Files\\Oracle\\VirtualBox\\VBoxManage.exe list vms";
		//cmd = "C:\\MinGWStudio\\Samples\\MessageBox\\VirtualBoxTest\\Release\\VirtualBoxTest.exe";
		System.out.println(cmd);
		out.println(cmd);
		pro = Runtime.getRuntime().exec(cmd);
		bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		BufferedReader bfre = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
		out.println("<div style=\"margin:20px\" class=\"vms-list\" width=%80>");
		out.println("<table>");
		while ((str = bfr.readLine()) != null)
		{
			out.println("<tr><td><pre>" + str + "</pre></td></tr>\n");
		}
		while ((str = bfre.readLine()) != null)
		{
			out.println("<tr><td><pre>" + str + "</pre></td></tr>\n");
		}
		out.println("</table>\n</div>\n");
		try
		{
			int k = pro.waitFor();
			out.println(k);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
		 * ); out.println("<HTML>");
		 * out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		 * out.println("  <BODY>"); out.print("    This is ");
		 * out.print(this.getClass()); out.println(", using the GET method");
		 * out.println("  </BODY>"); out.println("</HTML>");
		 */
		FormattedOut.printBottom(out);
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException
	{
		// Put your code here
	}

	// 截取数字
	public String getNumbers(String content)
	{
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find())
		{
			return matcher.group(0);
		}
		return "";
	}
}
