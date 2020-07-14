package com.yiqin.tool.thread.analysis.service;

import com.yiqin.tool.thread.analysis.model.Report;
import com.yiqin.tool.thread.analysis.model.ThreadDump;

public interface ReportService {

	public Report analysis(ThreadDump dump);
	
	public String analysisAndSaveToCache(ThreadDump dump);
	
	public Report getReportById(String uuid);
}
