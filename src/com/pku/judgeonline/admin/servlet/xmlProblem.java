package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.error.ErrorProcess;
import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class xmlProblem extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static Object prob_mute = new Object();
	private static NodeList itemList;
	String xmlPath = "";

	// boolean flag = false;
	public xmlProblem()
	{
	}

	public void init() throws ServletException
	{
		ServerConfig.init();
		// flag = false;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		if (!Guard.Guarder(request, response, out))
		{
			return;
		}
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		/*
		 * if(flag) { ErrorProcess.Error("题目已经添加不要刷新!", out);
		 * 
		 * return; }
		 */
		int num = 0;
		xmlPath = ServerConfig.getValue("xmlPath");
		String str = request.getParameter("str");
		str = xmlPath + str;
		File xmlFile;
		//PrintWriter out = response.getWriter();
		try
		{
			Connection connection = DBConfig.getConn();
			FormattedOut.printHead(out, "xmlProblem");

			File xml = new File(xmlPath);
			if (!xml.exists())
			{
				ErrorProcess.Error(xmlPath + " dictrory not exists.", out);
				connection.close();
				return;
			}
			boolean flag = false;
			String[] files = xml.list();
			int m = 0;
			for (m = 0; m < files.length; m++)
				if (files[m].endsWith(".xml"))
				{
					str = xmlPath + "\\" + files[m];
					xmlFile = new File(str);

					Document doc = parseXML(str);
					itemList = doc.getElementsByTagName("item");
					for (int ii = 0; ii < itemList.getLength(); ii++)
					{
						int id = 1000;
						PreparedStatement localPreparedStatement9 = connection.prepareStatement("select max(problem_id) as maxp from problem");
						ResultSet localResultSet9 = localPreparedStatement9.executeQuery();
						if (localResultSet9.next())
						{
							id = localResultSet9.getInt("maxp") + 1;
							if (id < 1000)
								id = 1000;
						}
						Problem p = itemToProblem(itemList.item(ii), id);
						if (p.data_in.isEmpty())
							continue;

						String s = new String(p.title.getBytes("gb2312"), "UTF-8");
						
						int i = 0;
						try
						{
							i = Integer.parseInt(p.time) * 1000;
							if (i >= 1000000)
								i /= 1000;
						} catch (NumberFormatException numberformatexception)
						{
							i = 0;
						}
						int j = 0;
						try
						{
							j = Integer.parseInt(request.getParameter("case_time_limit"));
						} catch (NumberFormatException numberformatexception1)
						{
							j = 0;
						}
						if (j == 0)
							j = i;
						int k = 0;
						try
						{
							k = Integer.parseInt(p.memory) * 1024;
						} catch (NumberFormatException numberformatexception2)
						{
							k = 0;
						}
						String s1 = new String(p.description.getBytes("gb2312"), "UTF-8");
						
						String s2 = new String(p.input.getBytes("gb2312"), "UTF-8");
						
						String s3 = new String(p.output.getBytes("gb2312"), "UTF-8");
						
						String s4 = request.getParameter("contest_id");
						if (s4 != null && s4.trim().equals(""))
							s4 = null;
						String s5 = new String(p.sample_input.getBytes("gb2312"), "UTF-8");
						
						String s6 = new String(p.sample_output.getBytes("gb2312"), "UTF-8");
						
						String s7 = new String(p.source.getBytes("gb2312"), "UTF-8");
						
						String s8 = new String(p.hint.getBytes("gb2312"), "UTF-8");
						
						synchronized (prob_mute)
						{
							long l = ServerConfig.getNextProblemId();
							String s9 = Tool.fixPath(ServerConfig.getValue("DataFilesPath"));
							String s10 = (new StringBuilder()).append(s9).append(l).toString();
							String s11 = (new StringBuilder()).append(s9).append(l).toString();
							File file = new File(s10);
							if (!file.isDirectory())
								file.mkdirs();

							if (p.spj != null &&  !"".equals(p.spj))
							{
								String msg = "SPJ代码存放在" + file.getAbsolutePath() + "\\" + "Validate.exe用记事本打开，自己修改";
								out.println("<font color=red><b>spj " + l + ":</font>" + msg + "</b><br>");
								File Validate = new File(file.getAbsolutePath() + "\\" + "Validate.exe");
								FileOutputStream fout = new FileOutputStream(Validate);
								fout.write(p.spj.getBytes());
								msg = "/*注意本OJ的SPJ程序返回0表示AC，返回2表示WA，3个参数是标准输入、用户输出、标准输出.*/";
								fout.write(msg.getBytes());
								fout.close();
								s += "<font color=black>(need SPJ)</font>";
								p.title = s;
								s8 += new String("\n暂时没有SPJ.".getBytes("gb2312"), "UTF-8");
								num++;
							}
							flag = true;
							PreparedStatement preparedstatement = connection.prepareStatement("INSERT INTO problem (problem_id,title,description,input,output,input_path,output_path,sample_input,sample_output,hint,source,in_date,time_limit,memory_limit,case_time_limit,contest_id)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							int i1 = 1;
							preparedstatement.setLong(i1++, l);
							preparedstatement.setString(i1++, s);
							preparedstatement.setString(i1++, s1);
							preparedstatement.setString(i1++, s2);
							preparedstatement.setString(i1++, s3);
							preparedstatement.setString(i1++, s10);
							preparedstatement.setString(i1++, s11);
							preparedstatement.setString(i1++, s5);
							preparedstatement.setString(i1++, s6);
							preparedstatement.setString(i1++, s8);
							preparedstatement.setString(i1++, s7);
							preparedstatement.setTimestamp(i1++, ServerConfig.getSystemTime());
							preparedstatement.setInt(i1++, i);
							preparedstatement.setInt(i1++, k);
							preparedstatement.setInt(i1++, j);
							preparedstatement.setString(i1++, s4);
							preparedstatement.executeUpdate();
							preparedstatement.close();

							if (s4 != null)
							{
								PreparedStatement preparedstatement1 = connection.prepareStatement("insert into contest_problem (contest_id,problem_id,title,num) values(?,?,?,?)");
								int j1 = 1;
								preparedstatement1.setString(j1++, s4);
								preparedstatement1.setLong(j1++, l);
								preparedstatement1.setString(j1++, s);
								preparedstatement1.setInt(j1++, 999);
								preparedstatement1.executeUpdate();
								preparedstatement1.close();
								preparedstatement1 = connection.prepareStatement("select problem_id from contest_problem where contest_id=? order by num");
								preparedstatement1.setString(1, s4);
								ResultSet resultset = preparedstatement1.executeQuery();
								j1 = 0;
								PreparedStatement preparedstatement2;
								for (; resultset.next(); preparedstatement2.close())
								{
									preparedstatement2 = connection.prepareStatement("update contest_problem set num=? where contest_id=? and problem_id=?");
									preparedstatement2.setInt(1, j1++);
									preparedstatement2.setString(2, s4);
									preparedstatement2.setLong(3, resultset.getLong("problem_id"));
									preparedstatement2.executeUpdate();
								}

								preparedstatement1.close();
							}

							out.println(l + " <b>" + p.title + "</b>  &nbsp&nbsp&nbsp&nbsp\tdata files:" + p.data_in.size());
							if (!"".equals(p.solution))
							{
								String type = ".c";
								if (p.type.equalsIgnoreCase("cpp") || p.type.equalsIgnoreCase("c++"))
									type = ".cpp";
								if (p.type.equalsIgnoreCase("java"))
									type = ".java";
								File solution = new File(file.getAbsolutePath() + "\\" + l + type);
								solution.createNewFile();
								FileOutputStream fout = new FileOutputStream(solution);
								fout.write(p.solution.getBytes());
								fout.close();
								out.println("&nbsp&nbsp&nbsp&nbsp    solution " + solution + " ");
							}
							out.println("<br>");
							int n = 0;
							Iterator in = p.data_in.iterator();
							Iterator it = p.data_out.iterator();
							for (; in.hasNext(); n++)
							{
								File datain = new File(file.getAbsolutePath() + "\\" + l + "_" + n + ".in");
								datain.createNewFile();
								byte data[] = ((String) in.next()).getBytes();
								FileOutputStream fo = new FileOutputStream(datain);
								fo.write(data);
								fo.close();
								File dataout = new File(file.getAbsolutePath() + "\\" + l + "_" + n + ".out");
								dataout.createNewFile();
								if (it.hasNext())
								{
									data = ((String) it.next()).getBytes();
									fo = new FileOutputStream(dataout);
									fo.write(data);
									fo.close();
								}
							}
						}

					}
					out.println("<h3>" + xmlFile.getAbsolutePath() + " add succeed.</h3><hr>");
					String ss = xmlFile.getName();
					int len = ss.length();
					xmlFile.renameTo(new File(xmlPath + "\\" + ss.substring(0, len - 4) + ".xml.bak"));
				}
			if (num > 0)
			{
				String msg = "<b>总共有" + num + "个题目需要添加SPJ程序。</b><br>";
				out.println(msg);
			}
			String msg = "<font color=red><b>在" + xmlPath + "目录没有找到任何xml文件。</b></font>";
			if (flag == true)
				msg = "<font color=red><b>题目添加成功。建议重启一下Tomcat服务器。</b></font>";
			// flag = true;
			out.println(msg);
			connection.close();
		} catch (Exception exception)
		{
			ErrorProcess.ExceptionHandle(exception, out);
		}
		FormattedOut.printBottom(out);
		return;
	}

	private static void mkdirs()
	{
		File dir = new File("images/problem");
		if (!dir.exists())
			dir.mkdirs();
	}

	private static Document parseXML(String filepath)
	{
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(filepath);
			doc.normalize();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		} catch (SAXException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return doc;
	}

	private static Problem itemToProblem(Node item, int id)
	{
		Problem p = new Problem();
		NodeList ch = item.getChildNodes();
		int num = 0;
		for (int i = 0; i < ch.getLength(); i++)
		{
			Node e = ch.item(i);
			String name = e.getNodeName();
			String value = e.getTextContent();
			if (name.equalsIgnoreCase("title"))
				p.title = value;
			if (name.equalsIgnoreCase("time_limit"))
				p.time = value;
			if (name.equalsIgnoreCase("memory_limit"))
				p.memory = value;
			if (name.equalsIgnoreCase("description"))
				p.description = p.setImages(value);
			if (name.equalsIgnoreCase("input"))
				p.input = p.setImages(value);
			if (name.equalsIgnoreCase("output"))
				p.output = p.setImages(value);
			if (name.equalsIgnoreCase("sample_input"))
				p.sample_input = value;
			if (name.equalsIgnoreCase("sample_output"))
				p.sample_output = value;
			if (name.equalsIgnoreCase("test_input"))
				p.data_in.add(value);
			if (name.equalsIgnoreCase("test_output"))
				p.data_out.add(value);
			if (name.equalsIgnoreCase("hint"))
				p.hint = p.setImages(value);
			if (name.equalsIgnoreCase("source"))
				p.source = p.setImages(value);
			if (name.equalsIgnoreCase("spj"))
				p.spj = value;
			if (name.equalsIgnoreCase("solution"))
			{
				p.solution = value;
				p.type = ((Element) e).getAttribute("language");
			}
			if (name.equalsIgnoreCase("img"))
				p.imageList.add(new Image(e, p, id, num++));
		}

		return p;
	}

	public void destroy()
	{
		// flag = false;
	}
}
