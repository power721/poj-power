package com.pku.judgeonline.servlet;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Archive extends HttpServlet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	class ZipMultiDirectoryCompress
	{

		public ZipMultiDirectoryCompress()
		{

		}

		public void startCompress(ZipOutputStream zos, String oppositePath, String directory)
		{
			File file = new File(directory);
			if (file.isDirectory())
			{
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					File aFile = files[i];
					if (aFile.isDirectory())
					{
						String newOppositePath = oppositePath + aFile.getName() + "/";
						compressDirectory(zos, oppositePath, aFile);
						startCompress(zos, newOppositePath, aFile.getPath());
					} else
					{
						compressFile(zos, oppositePath, aFile);
					}
				}
			} else
			{
				compressFile(zos, oppositePath, file);
			}
		}

		public void compressFile(ZipOutputStream zos, String oppositePath, File file)
		{
			// 创建一个Zip条目，每个Zip条目都是必须相对于根路径
			ZipEntry entry = new ZipEntry(oppositePath + file.getName());
			InputStream is = null;
			try
			{
				// 将条目保存到Zip压缩文件当中
				zos.putNextEntry(entry);
				// 从文件输入流当中读取数据，并将数据写到输出流当中.
				is = new FileInputStream(file);
				int length = 0;
				int bufferSize = 1024;
				byte[] buffer = new byte[bufferSize];
				while ((length = is.read(buffer, 0, bufferSize)) >= 0)
				{
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
			} catch (IOException ex)
			{
				ex.printStackTrace();
			} finally
			{
				try
				{
					if (is != null)
						is.close();
				} catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		public void compressDirectory(ZipOutputStream zos, String oppositePath, File file)
		{
			// 压缩目录，这是关键，创建一个目录的条目时，需要在目录名后面加多一个"/"
			ZipEntry entry = new ZipEntry(oppositePath + file.getName() + "/");
			try
			{
				zos.putNextEntry(entry);
				zos.closeEntry();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public Archive()
	{
	}

	public void init() throws ServletException
	{
	}

	public static void deleteDir(File dir)
	{
		if (dir == null || !dir.exists() || !dir.isDirectory())
			return; // 检查参数
		for (File file : dir.listFiles())
		{
			if (file.isFile())
				file.delete(); // 删除所有文件
			else if (file.isDirectory())
				deleteDir(file); // 递规的方式删除文件夹
		}
		dir.delete();// 删除目录本身
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String s3 = ServerConfig.getValue("Archive");
		if (s3 != null && s3.equals("FALSE"))
		{
			ErrorProcess.Error("It's not available now.", out);
			return;
		}
		if (!Guard.Guarder(request, response, out))
			return;
		if (!UserModel.isLoginned(request))
		{
			Tool.GoToURL(".", response);
			return;
		}
		try
		{
			Connection connection = DBConfig.getConn();
			UserModel usermodel = UserModel.getCurrentUser(request);
			String s = usermodel.getUser_id();
			String problem = null;
			String solution = null;
			String code = null;
			String dir = "..\\webapps\\download\\";
			String fileType = null;
			PreparedStatement preparedstatement = connection.prepareStatement("select problem_id from solution where user_id=? group by problem_id");
			preparedstatement.setString(1, s);
			ResultSet resultset = preparedstatement.executeQuery();
			File dic = new File(dir + s + "\\");
			File problemFile = null;
			File file = null;
			dic.mkdirs();
			String destFile = dir + s + ".zip";
			file = new File(destFile);
			file.delete();
			// System.out.println(s+" "+dic.getAbsolutePath());
			for (; resultset.next();)
			{
				problem = resultset.getString("problem_id");
				PreparedStatement preparedstatement1 = connection.prepareStatement("select * from solution where user_id=? and problem_id=? and result<10");
				preparedstatement1.setString(1, s);
				preparedstatement1.setString(2, problem);
				ResultSet resultset1 = preparedstatement1.executeQuery();
				problemFile = new File(dir + s + "\\" + problem);
				// System.out.println(problem+" "+problemFile.getAbsolutePath());
				problemFile.mkdirs();
				for (; resultset1.next();)
				{
					solution = resultset1.getString("solution_id");
					int type = resultset1.getInt("language");
					int k2 = resultset1.getInt("result");
					String ext = "";
					fileType = ".cpp";
					if (type == 1)
						fileType = ".c";
					else if (type == 2)
						fileType = ".pas";
					else if (type == 3)
						fileType = ".java";
					else if (type == 4)
						fileType = ".asm";

					if (k2 == 0)
					{
						ext = "_" + resultset1.getString("time") + "MS_" + resultset1.getString("memory") + "K";
					}

					file = new File(dir + s + "\\" + problem + "\\" + solution + "_" + ResultType.getResult(k2) + ext + fileType);
					// System.out.println(k2+" "+ext+" "+file.getName());
					FileOutputStream fos = new FileOutputStream(file);
					PreparedStatement preparedstatement2 = connection.prepareStatement("select uncompress(source) as source from source_code where solution_id=?");
					preparedstatement2.setString(1, solution);
					ResultSet resultset2 = preparedstatement2.executeQuery();
					if (resultset2.next())
					{
						code = resultset2.getString("source");
						// System.out.println(code);
						fos.write(code.getBytes());
					}
					fos.close();
				}
			}

			ZipMultiDirectoryCompress zipCompress = new ZipMultiDirectoryCompress();
			String directory = dir + s;
			String defaultParentPath = "";
			ZipOutputStream zos = null;
			try
			{
				// 创建一个Zip输出流
				zos = new ZipOutputStream(new FileOutputStream(destFile));
				// 启动压缩进程
				zipCompress.startCompress(zos, defaultParentPath, directory);
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			} finally
			{
				try
				{
					if (zos != null)
						zos.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			// Archive
			deleteDir(dic);
			preparedstatement.close();
			connection.close();
			Tool.GoToURL("../download/" + s + ".zip", response);
			// Tool.forwardToUrl(request,response,"../download/"+s+".zip");
			// file = new File(destFile);
			// file.deleteOnExit();
			// Tool.GoToURL("../", response);

		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
			return;
		}
		out.close();
	}

	public void destroy()
	{
	}
}
