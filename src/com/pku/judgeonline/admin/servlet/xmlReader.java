package com.pku.judgeonline.admin.servlet;

import com.pku.judgeonline.common.*;
import com.pku.judgeonline.admin.common.FormattedOut;
import com.pku.judgeonline.security.Guard;
import java.io.IOException;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class xmlReader extends HttpServlet
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static NodeList itemList;
	String xmlPath = "";
	boolean flag = false;

	public xmlReader()
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
		}
		if (!UserModel.isAdminLoginned(request))
		{
			Tool.GoToURL(LoginServlet.Admin_Login, response);
			return;
		}
		xmlPath = ServerConfig.getValue("xmlPath");
		String str = request.getParameter("str");

		File xmlFile;
		int id = 0;
		try
		{

			FormattedOut.printHead(out, "xmlReader");
			//PrintWriter out = response.getWriter();

			if (str == null || str == "")
			{
				File xml = new File(xmlPath);
				String[] files = xml.list();
				int m = 0;
				int n = 0;
				for (m = 0; m < files.length; m++)
					if (files[m].endsWith(".xml"))
					{
						str = xmlPath + "\\" + files[m];
						xmlFile = new File(str);
						if (!xmlFile.exists())
							return;
						out.println("<h3>" + xmlFile.getAbsolutePath() + "</h3><br>");
						Document doc = parseXML(str);
						itemList = doc.getElementsByTagName("item");
						for (int i = 0; i < itemList.getLength(); i++)
						{
							Problem p = itemToProblem(itemList.item(i), id++);

							out.println(++n + " <b>" + p.title + "</b>  &nbsp&nbsp&nbsp&nbsp\tdata files:" + p.data_in.size());
							if (p.spj != "")
								out.println("<font color=red> SPJ</font>");
							out.println("<br>");
						}

						out.println("<hr>");
					}
			} else
			{
				str = xmlPath + str;
				xmlFile = new File(str);

				out.println(xmlFile.getAbsolutePath());
				Document doc = parseXML(str);
				itemList = doc.getElementsByTagName("item");
				for (int i = 0; i < itemList.getLength(); i++)
				{
					Problem p = itemToProblem(itemList.item(i), id++);
					out.println("<center><h2>Problem " + (char) (i + 'A') + ":" + p.title + "</h2></center>");
					out.println("<center>Time: " + Integer.parseInt(p.time) * 1000 + "MS  " + "Memory: " + Integer.parseInt(p.memory) * 1024 + "K" + "</center>");
					out.println("Description:<br>" + p.description);
					out.println("Input:<br>" + p.input);
					out.println("Output:<br>" + p.output);
					out.println("Sample_input:<br><pre>" + p.sample_input + "</pre>");
					out.println("Sample_output:<br><pre>" + p.sample_output + "</pre>");
					out.println("Hint:<br>" + p.hint);
					out.println("Source:<br>" + p.source);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
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
			if (name.equalsIgnoreCase("img"))
				p.imageList.add(new Image(e, p, id, num++));
		}

		return p;
	}

	public void destroy()
	{
	}
}
