package com.yiqin.tool.thread.analysis.model;

import java.util.List;

public class ThreadInfo {
	private String name;
	private Boolean isDaemon;
	private Integer prio;
	private String osPrio;
	private String cpu;
	private String elapsed;
	private String tid;
	private String nid;
	private String other;

	
	private String state;
	
	private List<StackMethod> stack;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean isDaemon() {
		return isDaemon;
	}
	public void setDaemon(Boolean isDaemon) {
		this.isDaemon = isDaemon;
	}
	public Integer getPrio() {
		return prio;
	}
	public void setPrio(Integer prio) {
		this.prio = prio;
	}
	public String getOsPrio() {
		return osPrio;
	}
	public void setOsPrio(String osPrio) {
		this.osPrio = osPrio;
	}
	
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public String getElapsed() {
		return elapsed;
	}
	public void setElapsed(String elapsed) {
		this.elapsed = elapsed;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getNid() {
		return nid;
	}
	public void setNid(String nid) {
		this.nid = nid;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}

	public Boolean getIsDaemon() {
		return isDaemon;
	}
	public void setIsDaemon(Boolean isDaemon) {
		this.isDaemon = isDaemon;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public List<StackMethod> getStack() {
		return stack;
	}
	public void setStack(List<StackMethod> stack) {
		this.stack = stack;
	}
	
}
