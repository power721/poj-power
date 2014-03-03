package com.pku.judgeonline.admin.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;
import com.pku.judgeonline.error.ErrorProcess;

public class Taskkill extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public Taskkill()
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
		response.setContentType("text/html; charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		PrintWriter out = response.getWriter();
		FormattedOut.printHead(out, "Task List");
		long pid = Long.parseLong(request.getParameter("pid"));
		boolean flag = false;
		String str = "";
		Process pro = Runtime.getRuntime().exec("tasklist /FI \"PID eq " + pid + "\"");
		BufferedReader bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		while ((str = bfr.readLine()) != null)
		{
			// System.out.println(str);
			if (str.startsWith("Main.exe"))
			{
				flag = true;
				break;
			}
		}
		if (flag == false)
		{
			ErrorProcess.Error("You can not kill this process!", out);
			return;
		}
		// out.println("Name: " + request.getParameter("name") + "<br>\n");
		// out.println("PID: " + request.getParameter("pid") + "<br>\n");
		Runtime.getRuntime().exec("taskkill /PID " + pid + " /T /F");
		// out.flush();
		// out.close();
		try
		{
			Thread.sleep(100);
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		Tool.GoToURL("admin.task", response);
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

		response.setContentType("text/html; charset=utf-8");
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		PrintWriter out = response.getWriter();
		FormattedOut.printHead(out, "Task List");
		long pid = Long.parseLong(request.getParameter("pid"));
		boolean flag = false;
		String str = "";
		Process pro = Runtime.getRuntime().exec("tasklist /FI \"PID eq " + pid + "\"");
		BufferedReader bfr = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		while ((str = bfr.readLine()) != null)
		{
			System.out.println(str);
			if (str.startsWith("Main.exe"))
			{
				flag = true;
				// break;
			}
		}
		if (flag == false)
		{
			out.flush();
			out.close();
			return;
		}
		out.println("Name: " + request.getParameter("name") + "<br>\n");
		out.println("PID: " + request.getParameter("pid") + "<br>\n");
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

}
