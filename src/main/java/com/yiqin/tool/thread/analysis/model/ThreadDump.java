package com.yiqin.tool.thread.analysis.model;

import java.util.HashMap;
import java.util.Map;

public class ThreadDump {
	private Long dumpId;
	private String generateTime;
	private String jdkInfo;
	
	private Map<String, ThreadInfo> threadIdMap = new HashMap<String, ThreadInfo>();

	public Long getDumpId() {
		return dumpId;
	}

	public void setDumpId(Long dumpId) {
		this.dumpId = dumpId;
	}

	public String getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(String generateTime) {
		this.generateTime = generateTime;
	}

	public String getJdkInfo() {
		return jdkInfo;
	}

	public void setJdkInfo(String jdkInfo) {
		this.jdkInfo = jdkInfo;
	}

	public Map<String, ThreadInfo> getThreadIdMap() {
		return threadIdMap;
	}

	public void setThreadIdMap(Map<String, ThreadInfo> threadIdMap) {
		this.threadIdMap = threadIdMap;
	}
	
	
}
