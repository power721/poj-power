package com.pku.judgeonline.admin.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pku.judgeonline.common.ServerConfig;
import com.pku.judgeonline.common.Tool;
import com.pku.judgeonline.common.UserModel;

public class FileDownload extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
	{
		if (!UserModel.isAdminLoginned(req))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, res);
			return;
		}
		//String path = req.getParameter("path");
		String fileName = req.getParameter("name");
		String pid = req.getParameter("pid");
		String path = new StringBuilder().append(Tool.fixPath(ServerConfig.getValue("DataFilesPath"))).append(pid).append("\\").append(fileName).toString();
		//System.out.println(path);
		File f = new File(path);
		if (!f.exists())
		{
			res.sendError(404, "File not found!");
			return;
		}
		BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
		byte[] buf = new byte[1024];
		int len = 0;
		res.reset(); // 非常重要
		res.setContentType("application/x-msdownload");
		res.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
		OutputStream out = res.getOutputStream();
		while ((len = br.read(buf)) > 0)
			out.write(buf, 0, len);
		br.close();
		out.close();
	}

}
