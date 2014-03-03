package com.pku.judgeonline.common;

public class LanguageType
{

	private static boolean _$1502 = false;
	private static int _$3700 = 0;
	private static int _$3828[] = null;
	private static String _$3702[] = _$3992();
	private static String _$3993[] = null;
	private static String _$3994[] = null;
	private static String _$3995[] = null;
	private static String _$3996[] = null;
	private static int _$3997[] = null;
	private static int _$3998[] = null;
	private static int _$3999[] = null;

	public LanguageType()
	{
	}

	public static int getOrder(int i)
	{
		_$895();
		if (i < _$3700)
			return _$3997[i];
		else
			return -1;
	}

	public static int getTimeFactor(int i)
	{
		_$895();
		return _$3998[i];
	}

	public static int getExtMemory(int i)
	{
		_$895();
		if (i < _$3700)
			return _$3828[i];
		else
			return -1;
	}

	public static int getExtTime(int i)
	{
		return _$3999[i];
	}

	public static String getExe(int i)
	{
		return _$3996[i];
	}

	public static String[] GetDescriptions()
	{
		_$895();
		return _$3702;
	}

	public static int getLangs()
	{
		_$895();
		return _$3700;
	}

	private static void _$895()
	{
		if (!_$1502)
		{
			_$3700 = _$4001();
			_$3993 = _$4002();
			_$3828 = _$4003();
			_$3997 = _$4004();
			_$3994 = _$4005();
			_$3995 = _$4006();
			_$3996 = _$4007();
			_$3998 = _$4008();
			_$3999 = _$4009();
			_$1502 = true;
		}
	}

	private static int _$4001()
	{
		return Integer.parseInt(ServerConfig.getValue("LangCount"));
	}

	private static String[] _$4005()
	{
		String as[] = new String[_$3700];
		for (int i = 0; i < _$3700; i++)
			as[i] = ServerConfig.getValue((new StringBuilder()).append(_$3702[i]).append("CompileCmd").toString());

		return as;
	}

	private static String[] _$4006()
	{
		String as[] = new String[_$3700];
		for (int i = 0; i < _$3700; i++)
			as[i] = ServerConfig.getValue((new StringBuilder()).append(_$3702[i]).append("RunCmd").toString());

		return as;
	}

	private static int[] _$4008()
	{
		String s = ServerConfig.getValue("LanguageTimeFactor");
		String as[] = s.split(",");
		int ai[] = new int[as.length];
		for (int i = 0; i < as.length; i++)
			ai[i] = Integer.parseInt(as[i]);

		return ai;
	}

	private static int[] _$4009()
	{
		String s = ServerConfig.getValue("LanguageExtTime");
		String as[] = s.split(",");
		int ai[] = new int[as.length];
		for (int i = 0; i < as.length; i++)
			ai[i] = Integer.parseInt(as[i]);

		return ai;
	}

	private static int[] _$4004()
	{
		String s = ServerConfig.getValue("CompileStreamOrder");
		String as[] = s.split(",");
		int ai[] = new int[as.length];
		for (int i = 0; i < as.length; i++)
			ai[i] = Integer.parseInt(as[i]);

		return ai;
	}

	private static String[] _$4007()
	{
		String s = ServerConfig.getValue("LanguageExes");
		String as[] = s.split(",");
		return as;
	}

	private static int[] _$4003()
	{
		String s = ServerConfig.getValue("LanguageExtMemory");
		String as[] = s.split(",");
		int ai[] = new int[as.length];
		for (int i = 0; i < as.length; i++)
			ai[i] = Integer.parseInt(as[i]);

		return ai;
	}

	private static String[] _$3992()
	{
		String s = ServerConfig.getValue("LanguageDescs");
		String as[] = s.split(",");
		return as;
	}

	private static String[] _$4002()
	{
		String s = ServerConfig.getValue("LanguageExts");
		String as[] = s.split(",");
		return as;
	}

	public static boolean isLanguage(int i)
	{
		_$895();
		return i >= 0 && i < _$3700;
	}

	public static String getDesc(int i)
	{
		return _$3702[i];
	}

	public static String getExt(int i)
	{
		_$895();
		return _$3993[i];
	}

	public static String getRunCmd(String s, String s1, int i)
	{
		_$895();
		String s2 = _$3995[i];
		s = _$2069(s);
		if (s2 == null)
		{
			s2 = (new StringBuilder()).append(s).append(s1).append(".exe").toString();
		} else
		{
			s2 = _$4011(s2, "%PATH%", s);
			s2 = _$4011(s2, "%NAME%", s1);
			s2 = _$4011(s2, "%EXT%", _$3993[i]);
		}
		return s2;
	}

	public static String getCompileCmd(String s, String s1, int i)
	{
		_$895();
		s = _$2069(s);
		String s2 = _$3994[i];
		s2 = _$4011(s2, "%PATH%", s);
		s2 = _$4011(s2, "%NAME%", s1);
		s2 = _$4011(s2, "%EXT%", _$3993[i]);
		return s2;
	}

	private static String _$4011(String s, String s1, String s2)
	{
		String s3 = "";
		int i = 0;
		int j = s.indexOf(s1);
		if (j == -1)
			return s;
		for (; j != -1; j = s.indexOf(s1, i))
		{
			s3 = (new StringBuilder()).append(s3).append(s.substring(i, j)).append(s2).toString();
			i = j + s1.length();
		}

		return (new StringBuilder()).append(s3).append(s.substring(i)).toString();
	}

	private static String _$2069(String s)
	{
		return s.endsWith("\\") ? s : (new StringBuilder()).append(s).append("\\").toString();
	}

}
