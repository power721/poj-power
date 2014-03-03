package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class UploadPage extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UploadPage()
	{
	}

	public void init() throws ServletException
	{
	}

	public static boolean deleteFile(File f)
	{
		if (f.exists())
		{
			if (f.isFile())
				return f.delete();
			else if (f.isDirectory())
			{
				File[] files = f.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					if (!deleteFile(files[i]))
						return false;
				}
				return f.delete();
			} else
				return false;
		} else
			return false;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		FormattedOut.printHead(out, "Upload Data");
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		String str1 = request.getParameter("op");
		if (str1 == null)
			str1 = "";
		String str2 = request.getParameter("idx");
		int idx = -1;
		if (str2 != null)
			idx = Integer.parseInt(str2);
		String s = request.getParameter("problem_id");
		if (s == null)
		{
			ErrorProcess.Error("", out);
			return;
		} else
		{
			int i;
			int j = 1;
			String s1 = Tool.fixPath(ServerConfig.getValue("DataFilesPath"));
			String s2 = (new StringBuilder()).append(s1).append(s).toString();
			File file = new File(s2);
			File[] data = file.listFiles();
			if ((str1.trim().equals("del")))
			{
				if (idx < 0 || idx >= data.length)
				{
					ErrorProcess.Error("No such file.", out);
					return;
				}
				if (!deleteFile(data[idx]))
				{
					ErrorProcess.Error("Delete file error.", out);
					return;
				}
				Tool.GoToURL("admin.upload?problem_id=" + s, response);
				return;
			}
			
			out.println("<form method=\"POST\" enctype=\"multipart/form-data\" action=\"admin.uploadservlet\">");
			out.println("          <table border=\"0\" cellpadding=\"10\" cellspacing=\"10\" style=\"border-collapse: collapse\" bordercolor=\"#111111\" width=\"100%\" id=\"AutoNumber3\">");
			out.println("            <tr>");
			out.println("              <td width=\"100%\" bgcolor=\"#00FFFF\">");
			out.println("              <p align=\"center\"><font face=\"华文仿宋\" color=\"#000080\"><b>--------------------------------- 上 传 数 据 ---------------------------------</b></font></td>");
			out.println("            </tr>");
			out.println("            <tr>");
			out.println("              <td width=\"100%\">");
			out.println("              <table border=\"0\" cellpadding=\"3\" cellspacing=\"3\" style=\"border-collapse: collapse\" bordercolor=\"#111111\" width=\"100%\" id=\"AutoNumber4\">        ");
			
			out.println((new StringBuilder()).append("                 <input type=\"hidden\" name=\"FilePath\" value=\"").append(s2).append("\" >").toString());
			out.println((new StringBuilder()).append("                  <input type=\"hidden\" name=\"FileID\" value=\"").append(s).append("\">").toString());
			out.println("               <tr>");
			out.println((new StringBuilder()).append("                  <td width=\"20%\">请选择").append(s).append("题的数据文件(.in,.out):</td>").toString());
			out.println("                  <td width=\"80%\">");
			out.println("                  <div class=\"uploader blue\">");
			out.println("                    <input type=\"text\" class=\"filename\" readonly=\"readonly\"/>");
			out.println("                    <input type=\"button\" name=\"file\" class=\"button\" value=\"Browse...\"/>");
			out.println("                    <input type=\"file\" name=\"FileData\" size=\"30\">");
			out.println("                </div>");
			out.println("                </td></tr>");
			out.println("              </table>");
			out.println("              </td>");
			out.println("            </tr>");
			out.println("            <tr>");
			out.println("              <td width=\"100%\">");
			out.println("              <input type=button name=\"Submit\" value=\"提交\" onclick=\"LimitAttachData(this.form, this.form.FileData.value)\">");
			out.println("              <input type=\"reset\" value=\"重置\" name=\"B2\"></td>");
			out.println("            </tr>");
			out.println("          </table>");
			out.println("        </form>");
			out.println("<script type=\"text/javascript\">");
			out.println("$(function(){");
			out.println("$(\"input[type=file]\").change(function(){$(this).parents(\".uploader\").find(\".filename\").val($(this).val());});");
			out.println("$(\"input[type=file]\").each(function(){");
			out.println("if($(this).val()==\"\"){$(this).parents(\".uploader\").find(\".filename\").val(\"No file selected...\");}");
			out.println("});");
			out.println("});");
			out.println("</script>");
			out.println("<center><font size=5 color=#333399>The Data Files of problem <a href=showproblem?problem_id=" + s + "><u>" + s + "</u></a></font><br>");
			out.println("<TABLE cellSpacing=0 cellPadding=0 width=99% border=1 style=\"border-collapse: collapse\" bordercolor=#FFFFFF>");
			out.println("<tr align=center bgcolor=#6589D1>");
			out.println("<td width=\"10%\" ><b>No.</b></td>");
			out.println("<td width=\"50%\" ><b>Name</b></td>");
			out.println("<td width=\"20%\" ><b>Size</b></td>");
			out.println("<td width=\"20%\" ><b>Operator</b></td>");
			out.println("</tr>");
			if(data != null)
			for (i = 0; i < data.length; i++)
				if (data[i].isFile())
				{
					FileInputStream fis = new FileInputStream(data[i]);
					long size = fis.available();
					fis.close();
					String str = String.format("%d", size) + " ";
					if (size >= 1048576)
						str = String.format("%.2f", size / 1048576.) + " M";
					else if (size >= 1024)
						str = String.format("%.2f", size / 1024.) + " K";
					out.println("<tr>");
					out.println("<td>" + j + "</td><td><a href=admin.download?name=" + Tool.urlEncode2(data[i].getName()) + "&pid="+s+" title=右键另存为>" + data[i].getName() + "</a></td><td>" + str + "B</td><td><a href=javascript:filedel(\"" + s + "\"," + i + ")>Del</a></td>");
					out.println("</tr>");
					j++;
				}
			out.println("</table></center>");
			out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"button\" name=\"button1\" value=\"返回\" onclick=\"javascript:history.back(-1);\">");
			FormattedOut.printBottom(out);
			return;
		}
	}

	public void destroy()
	{
	}
}
