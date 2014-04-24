package com.pku.judgeonline.problemset;

import java.sql.Timestamp;

public class RunRecord
{

	public long solution_id;
	public long problem_id;
	public int language;
	public long time_limit;
	public long memory_limit;
	public long case_time_limit;
	boolean isRejudge;
	int result;
	int time;
	int memory;
	String source;
	String sourceFileName;
	String workPath;
	String input_path;
	String output_path;
	String user_id;
	String contest_id;
	String ip;
	Timestamp submit_time;
	Timestamp start_time;

	public RunRecord()
	{
	}
}
