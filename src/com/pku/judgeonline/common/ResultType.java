package com.pku.judgeonline.common;

public class ResultType
{

	public static final int Accepted = 0;
	public static final int CompileError = 7;
	public static final int TimeLimitOut = 2;
	public static final int MemoryLimitOut = 3;
	public static final int WrongAnswer = 4;
	public static final int RunTimeError = 5;
	public static final int OutputLimitOut = 6;
	public static final int PresentationError = 1;
	public static final int RestrictedFunction = 8;
	public static final int SystemError = 98;
	public static final int Other = 99;
	public static final int Run = 100;
	public static final int Wait = 10000;

	public ResultType()
	{
	}

	public static String getResult(int i)
	{
		if (i == 0)
			return "AC";
		if (i == 7)
			return "CE";
		if (i == 2)
			return "TLE";
		if (i == 3)
			return "MLE";
		if (i == 4)
			return "WA";
		if (i == 5)
			return "RE";
		if (i == 6)
			return "OLE";
		if (i == 8)
			return "RF";
		if (i == 1)
			return "PE";
		if (i == 10000)
			return "Waiting";
		if (i == 98)
			return "SE";
		if (i == 99)
			return "VE";
		if (i == 100)
			return "Run";
		else
			return "Other";
	}

	public static String getResultDescript(int i)
	{
		if (i == 0)
			return "<font color=red><b>Accepted</b></font>";
		if (i == 7)
			return "<font color=#1e9e00>Compile Error</font>";
		if (i == 2)
			return "<font color=#ff9900>Time Limit Exceed</font>";
		if (i == 3)
			return "<font color=#0692ff>Memory Limit Exceed</font>";
		if (i == 4)
			return "<font color=#000000>Wrong Answer</font>";
		if (i == 5)
			return "<font color=#bb338f>Runtime Error</font>";
		if (i == 6)
			return "<font color=#999999>Output Limit Exceed</font>";
		if (i == 8)
			return "Restricted Function";
		if (i == 1)
			return "<font color=#ff03fa>Presentation Error</font>";
		if (i == 10000)
			return "<font color=green>Waiting</font>";
		if (i == 98)
			return "<font color=black>System Error</font>";
		if (i == 99)
			return "Validate Error";
		else if (i == 100)
			return "Running";
		else
			return "Other";
	}
}
