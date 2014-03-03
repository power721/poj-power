package com.pku.judgeonline.admin.servlet;

import java.util.Iterator;
import java.util.Vector;

class Problem
{

	private static int counter = 0;
	public int num;
	Vector<Image> imageList;
	String title;
	String time;
	String memory;
	String description;
	String input;
	String output;
	String sample_input;
	String sample_output;
	String hint;
	String source;
	String spj;
	String solution;
	String type;
	Vector<String> data_in;
	Vector<String> data_out;

	Problem()
	{
		num = counter++;
		imageList = new Vector<Image>();
		data_in = new Vector<String>();
		data_out = new Vector<String>();
		title = "";
		time = "";
		memory = "";
		description = "";
		input = "";
		output = "";
		sample_input = "";
		sample_output = "";
		hint = "";
		source = "";
		spj = "";
		solution = "";
		type = "";
	}

	public String setImages(String html)
	{
		for (Iterator<Image> i = imageList.iterator(); i.hasNext();)
		{
			Image img = (Image) i.next();
			html = html.replaceAll(img.oldURL, img.URL);
		}

		return html;
	}

}
