package com.pku.judgeonline.common;

import java.util.ArrayList;

public class Page
{
	private int totalRows = 0;
	private int totalPages = 1;
	private int size = 20;
	private int currentPage = 1;
	private int firstRow = 0;
	private ArrayList<String> keys;
	private ArrayList<String> values;
	/**
	 * Constructor of the object.
	 */
	public Page(int totalRows, String queryString)
	{
		this.totalRows = totalRows;
		init(queryString);
	}
	
	public Page(int totalRows, int size, String queryString)
	{
		this.totalRows = totalRows;
		this.size = size;
		init(queryString);
	}
	
	public void init(String queryString)
	{
		keys = new ArrayList<String>();
		values = new ArrayList<String>();
		setQueryString(queryString);
		if(totalRows > 0)
			totalPages = (totalRows + size-1) / size;
		//System.out.println("totalRows: "+totalRows+" totalPages:"+totalPages);
	}
	
	public int getTotalPages()
	{
		return totalPages;
	}
	
	public int getSize()
	{
		return size;
	}
	
	public void setSize(int size)
	{
		this.size = size;
		if(totalRows > 0)
			totalPages = (totalRows + size-1) / size;
	}
	
	public int getCurrentPage()
	{
		return currentPage;
	}
	
	public void setCurrentPage(int currentPage)
	{
		if(currentPage < 1)
			currentPage = 1;
		this.currentPage = currentPage;
		firstRow = (currentPage - 1) * size;
	}

	public void setCurrentPage(String s)
	{
		try
		{
			currentPage = Integer.parseInt(s);
		} catch (NumberFormatException numberformatexception)
		{
			currentPage = 1;
		}
		if(currentPage < 1)
			currentPage = 1;
		firstRow = (currentPage - 1) * size;
	}

	public int getLimit()
	{
		if(firstRow < 0)
			firstRow = 0;
		return firstRow;
	}
	
	public String getQueryString(int currentPage)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < keys.size(); i++)
		{
			String key = (String) keys.get(i);
			String value = (String) values.get(i);
			if("p".equals(key) && currentPage > 0)
				value = String.valueOf(currentPage);
			sb.append("&");
			sb.append(key);
			sb.append("=");
			sb.append(value);
		}
		if(sb.length() < 1)
			return sb.append("p=").append(currentPage).toString();
		return sb.delete(0, 1).toString();
	}
	
	public void setQueryString(String queryString)
	{
		if (queryString != null)
		{
			String s[] = queryString.split("&");
			for (int i = 0; i < s.length; i++)
			{
				String s1[] = s[i].split("=");
				if (s1.length == 2)
				{
					keys.add(s1[0]);
					values.add(s1[1]);
				} else
				{
					keys.add(s1[0]);
					values.add("");
				}
				if("p".equals(s1[0]))
					setCurrentPage(s1[1]);
			}
		}
	}
}
