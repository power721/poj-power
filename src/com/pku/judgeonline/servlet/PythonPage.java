package com.pku.judgeonline.servlet;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PythonPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public PythonPage()
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
		PrintWriter out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
			return;
		if (!(UserModel.isLoginned(request) && UserModel.isAdminLoginned(request)))
		{
			ErrorProcess.Error("Sorry, you have no permission.", out);
			return;
		}
		Connection connection = DBConfig.getConn();
		FormattedOut.printHead(out, request, connection, "Python Shell");
		//String user = UserModel.getCurrentUser(request).getUser_id();
		// out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		// out.println("<HTML>");
		// out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		// out.println("  <BODY>");
		out.println("<script language=\"javascript\" type=\"text/javascript\" src=\"js/editarea/edit_area/edit_area_full.js\"></script>");
		out.println("<script language=\"javascript\" type=\"text/javascript\">");
		out.println("editAreaLoader.init({");
		out.println("  id : \"source\"   // textarea id");
		out.println("  ,allow_resize: \"both\"");
		out.println("  ,font_size: \"12\"");
		out.println("  ,replace_tab_by_spaces: \"4\"");
		out.println("  ,syntax: \"python\"      // syntax to be uses for highgliting");
		// out.println("  ,syntax_selection_allow: \"basic,c,cpp,java,pas,perl,php,python,ruby\"");
		out.println("  ,toolbar: \"search, go_to_line, fullscreen, |, undo, redo, |, select_font,syntax_selection,|, change_smooth_selection, highlight, reset_highlight, word_wrap, |, help\"");
		out.println("  ,start_highlight: true    // to display with highlight mode on start-up");
		out.println("});");
		out.println("</script>");
		out.print("<form method=post action=pythonservlet>");
		out.print("<textarea id=\"source\" name=\"content\" rows=20 name=content cols=100></textarea><br>");
		out.println("<input type=submit value=Submit>");
		out.println("<input type=reset value=Reset>");
		out.println("</form>");
		// out.println("  </BODY>");
		// out.println("</HTML>");
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

}
