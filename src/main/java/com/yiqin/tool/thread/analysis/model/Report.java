package com.yiqin.tool.thread.analysis.model;

import java.util.Map;

public class Report {
	private Long dumpId;
	private String generateTime;
	private String jdkInfo;
	private int threadCount;
	
	private Map<String, String> threadElapsedMap;
	private Map<String, Integer> daemonCountMap;
	
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
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public Map<String, String> getThreadElapsedMap() {
		return threadElapsedMap;
	}
	public void setThreadElapsedMap(Map<String, String> threadElapsedMap) {
		this.threadElapsedMap = threadElapsedMap;
	}
	public Map<String, Integer> getDaemonCountMap() {
		return daemonCountMap;
	}
	public void setDaemonCountMap(Map<String, Integer> daemonCountMap) {
		this.daemonCountMap = daemonCountMap;
	}
	
	
}
