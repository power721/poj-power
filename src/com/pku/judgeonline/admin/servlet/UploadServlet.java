package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.common.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

public class UploadServlet extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Initialize global variables
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	// Process the HTTP Post request
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		final int NONE = 0;
		final int DATAHEADER = 1;
		final int FILEDATA = 2;
		final int FIELDDATA = 3;
		// 将请求消息的实体送到b变量中
		int TotalBytes = request.getContentLength();
		byte[] b = new byte[TotalBytes];
		String contentType = request.getContentType();// 请求消息类型
		String fieldname = ""; // 表单域的名称
		String fieldvalue = ""; // 表单域的值
		String filename = ""; // 文件名
		String boundary = ""; // 分界符
		String lastboundary = ""; // 结束符
		int filesize = 0; // 文件长度
		Hashtable<String, String> formfields = new Hashtable<String, String>();
		int pos = contentType.indexOf("boundary=");
		// String fileID; // 上传的文件ID
		if (pos != -1) // 取得分界符和结束符
		{
			pos += "boundary=".length();
			boundary = "--" + contentType.substring(pos);
			lastboundary = boundary + "--";
		}
		int state = NONE;
		// 得到数据输入流reqbuf
		DataInputStream in = new DataInputStream(request.getInputStream());
		in.readFully(b);
		in.close();
		String reqContent = new String(b);
		BufferedReader reqbuf = new BufferedReader(new StringReader(reqContent));
		boolean flag = true;
		int i = 0;
		while (flag == true)
		{
			String s = reqbuf.readLine();
			if (lastboundary.equals(s) || s == null)
				break;
			switch (state)
			{
			case NONE:
				if (s.startsWith(boundary))
				{
					state = DATAHEADER;
					i += 1;
				}
				break;
			case DATAHEADER:
				pos = s.indexOf("filename=");
				if (pos == -1)
				{ // 将表单域的名字解析出来
					pos = s.indexOf("name=");
					pos += "name=".length() + 1;
					s = s.substring(pos);
					int l = s.length();
					s = s.substring(0, l - 1);
					fieldname = s;
					state = FIELDDATA;
				} else
				{ // 将文件名解析出来
					String temp = s;
					pos = s.indexOf("filename=");
					pos += "filename=".length() + 1;
					s = s.substring(pos);
					int l = s.length();
					s = s.substring(0, l - 1);
					pos = s.lastIndexOf("\\");
					s = s.substring(pos + 1);
					filename = s;
					// 从字节数组中取出文件数组
					pos = byteIndexOf(b, temp, 0);
					b = subBytes(b, pos + temp.getBytes().length + 2, b.length);// 去掉前面的部分
					s = reqbuf.readLine();
					b = subBytes(b, s.getBytes().length + 4, b.length);
					pos = byteIndexOf(b, boundary, 0);
					b = subBytes(b, 0, pos - 1);
					File f = new File(formfields.get("FilePath") + "\\" + filename); // 写入文件
					DataOutputStream fileout = new DataOutputStream(new FileOutputStream(f));
					fileout.write(b, 0, b.length - 1);
					fileout.close();

					filesize = b.length - 1;
					state = FILEDATA;
				}
				break;
			case FIELDDATA:
				s = reqbuf.readLine();
				fieldvalue = s;
				formfields.put(fieldname, fieldvalue);
				state = NONE;
				break;
			case FILEDATA:
				while ((!s.startsWith(boundary)) && (!s.startsWith(lastboundary)))
				{
					s = reqbuf.readLine();
					if (s.startsWith(boundary))
					{
						state = DATAHEADER;
						break;
					}
				}
				break;
			}
		}
		reqbuf.close();
		Tool.GoToURL("admin.upload?problem_id=" + formfields.get("FileID"), response);
		// 指定输出类型
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		FormattedOut.printHead(out, "File Upload");
		out.println("<h1>文件上传结果</h1><hr>");
		out.println("文件" + filename + "上传成功！<br>目录  " + formfields.get("FilePath") + "<br>文件长度为:" + filesize + "字节<br><br>");
		out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.showproblem?problem_id=").append(formfields.get("FileID")).append("\">See This Problem</a><br>").toString());
		out.println((new StringBuilder()).append("<img border=\"0\" src=\"/images/j0293234.wmf\" width=\"40\" height=\"29\"> <a href=\"admin.upload?problem_id=").append(formfields.get("FileID")).append("\">See The DataFiles</a><br>").toString());
		out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"button\" name=\"button1\" value=\"返回\" onclick=\"javascript:history.back(-1);\">");
		FormattedOut.printBottom(out);
		out.close();
	}

	// Get Servlet information
	public String getServletInfo()
	{
		return "UploadServlet.UploadServlet Information";
	}

	// 字节数组中的INDEXOF函数，与STRING类中的INDEXOF类似
	private static int byteIndexOf(byte[] b, String s, int start)
	{
		return byteIndexOf(b, s.getBytes(), start);
	}

	// 字节数组中的INDEXOF函数，与STRING类中的INDEXOF类似
	private static int byteIndexOf(byte[] b, byte[] s, int start)
	{
		int i;
		if (s.length == 0)
		{
			return 0;
		}
		int max = b.length - s.length;
		if (max < 0)
			return -1;
		if (start > max)
			return -1;
		if (start < 0)
			start = 0;
		search: for (i = start; i <= max; i++)
		{
			if (b[i] == s[0])
			{
				int k = 1;
				while (k < s.length)
				{
					if (b[k + i] != s[k])
					{
						continue search;
					}
					k++;
				}
				return i;
			}
		}
		return -1;
	}

	// 用于从一个字节数组中提取一个字节数组
	private static byte[] subBytes(byte[] b, int from, int end)
	{
		byte[] result = new byte[end - from];
		System.arraycopy(b, from, result, 0, end - from);
		return result;
	}

	// 用于从一个字节数组中提取一个字符串
	private static String subBytesString(byte[] b, int from, int end)
	{
		return new String(subBytes(b, from, end));
	}

}
