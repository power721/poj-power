package com.pku.judgeonline.common;

import com.pku.judgeonline.error.ErrorProcess;
import java.io.PrintWriter;

public class ValueCheck
{

	public ValueCheck()
	{
	}

	public static boolean checkId(String s, PrintWriter out)
	{
		if (s == null || s.equals(""))
		{
			ErrorProcess.Error("User ID can not be NULL", out);
			return false;
		}
		if (s.getBytes().length < 5)
		{
			ErrorProcess.Error("User ID is too short", out);
			return false;
		}
		if (s.getBytes().length > 20)
		{
			ErrorProcess.Error("User ID is too long", out);
			return false;
		}
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.toLowerCase().charAt(i);
			if (c == '>')
			{
				ErrorProcess.Error("User ID can not contain \">\"", out);
				return false;
			}
			if (c == '<')
			{
				ErrorProcess.Error("User ID can not contain \"<\"", out);
				return false;
			}
			if (c == '#')
			{
				ErrorProcess.Error("User ID can not contain \">#\"", out);
				return false;
			}
			if (c == '&')
			{
				ErrorProcess.Error("User ID can not contain \"&\"", out);
				return false;
			}
			if ((c < 'a' || c > 'z') && (c > '9' || c < '0') && c != '_' && c != ' ')
			{
				ErrorProcess.Error("User ID can only contain number,letter and '_'", out);
				return false;
			}
		}

		return true;
	}

	public static boolean checkId(String s)
	{
		if (s == null || s.equals(""))
		{
			return false;
		}
		if (s.getBytes().length > 20)
		{
			return false;
		}
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.toLowerCase().charAt(i);
			if (c == '>')
			{
				return false;
			}
			if (c == '<')
			{
				return false;
			}
			if (c == '#')
			{
				return false;
			}
			if (c == '&')
			{
				return false;
			}
			if ((c < 'a' || c > 'z') && (c > '9' || c < '0') && c != '_' && c != ' ')
			{
				return false;
			}
		}

		return true;
	}

	public static boolean checkPassword(String s, PrintWriter out)
	{
		if (s == null || s.equals(""))
		{
			ErrorProcess.Error("Password can not be NULL", out);
			return false;
		}
		if (s.getBytes().length > 20)
		{
			ErrorProcess.Error("Password is too long", out);
			return false;
		}
		if (s.getBytes().length < 6)
		{
			ErrorProcess.Error("Password is too short", out);
			return false;
		}
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c == ' ')
			{
				ErrorProcess.Error("Password can not contain blank", out);
				return false;
			}
		}

		return true;
	}

	public static boolean checkNick(String s, PrintWriter out)
	{
		if (s == null || s.equals(""))
		{
			ErrorProcess.Error("nick can not be NULL", out);
			return false;
		}
		if (s.getBytes().length > 100)
		{
			ErrorProcess.Error("nick is too long", out);
			return false;
		}
		for (int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if (c == '"')
			{
				ErrorProcess.Error("nick can not contain \"", out);
				return false;
			}
		}

		return true;
	}
}
