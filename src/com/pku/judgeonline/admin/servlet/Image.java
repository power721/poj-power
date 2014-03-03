package com.pku.judgeonline.admin.servlet;

import java.io.FileOutputStream;
import java.io.IOException;
import org.w3c.dom.*;
import sun.misc.BASE64Decoder;

class Image
{

	// private static int counter = 0;
	// int num = 0;
	Problem p;
	String oldURL;
	String URL;

	public Image(Node e, Problem p, int id, int num)
	{
		// num = counter++;
		oldURL = "";
		URL = "";
		this.p = p;
		NodeList cn = e.getChildNodes();
		oldURL = cn.item(0).getTextContent();
		URL = (new StringBuilder("images/problem/pic")).append(id).append("_").append(num).toString();
		// System.out.println("oldURL:"+oldURL+"  URL:"+URL+"\n\n");
		// File file = new File(URL);
		// System.out.println(file.getAbsolutePath());
		try
		{
			byte decodeBuffer[] = (new BASE64Decoder()).decodeBuffer(cn.item(1).getTextContent());
			FileOutputStream fo = new FileOutputStream("../webapps/oj/" + URL);
			fo.write(decodeBuffer);
			fo.close();
		} catch (DOMException e1)
		{
			e1.printStackTrace();
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

}
