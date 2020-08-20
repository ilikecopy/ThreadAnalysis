package com.yiqin.tool.thread.analysis.model;

import java.util.Map;

import com.yiqin.tool.thread.analysis.service.impl.ReportServiceImpl.Method;

public class Report {
	private Long dumpId;
	private String generateTime;
	private String jdkInfo;
	private int threadCount;
	
	private Map<String, String> threadElapsedMap;
	private Map<String, Integer> daemonCountMap;
	private Map<String, Integer> stateCountMap;
	private Method flameData;
	
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
	public Map<String, Integer> getStateCountMap() {
		return stateCountMap;
	}
	public void setStateCountMap(Map<String, Integer> stateCountMap) {
		this.stateCountMap = stateCountMap;
	}
	public Method getFlameData() {
		return flameData;
	}
	public void setFlameData(Method flameData) {
		this.flameData = flameData;
	}
	
}
