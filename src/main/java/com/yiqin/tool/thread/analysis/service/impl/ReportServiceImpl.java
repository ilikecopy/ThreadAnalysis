package com.yiqin.tool.thread.analysis.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.yiqin.tool.thread.analysis.model.Report;
import com.yiqin.tool.thread.analysis.model.StackMethod;
import com.yiqin.tool.thread.analysis.model.ThreadDump;
import com.yiqin.tool.thread.analysis.model.ThreadInfo;
import com.yiqin.tool.thread.analysis.service.ReportService;

@Service
public class ReportServiceImpl implements ReportService {
	
	private static final Cache<String, Report> DUMP_REPORT_CACHE = CacheBuilder.newBuilder()
			.maximumSize(100)
			.expireAfterWrite(24, TimeUnit.HOURS)
			.removalListener(new RemovalListener<String, Report>() {
				
				@Override
				public void onRemoval(RemovalNotification<String, Report> notification) {
					System.out.println("we removed: \t" + notification.getKey());
				}
				
			})
			.build();
	
	public Report analysis(ThreadDump dump) {
		if (null == dump) {
			return null;//TODO or throw an exception?
		}
		
		//basic info part
		Report r = new Report();
		r.setDumpId(dump.getDumpId());
		r.setGenerateTime(dump.getGenerateTime());
		r.setJdkInfo(dump.getJdkInfo());
		
		
		Map<String, ThreadInfo> tiMap = dump.getThreadIdMap();
		r.setThreadCount(tiMap.size());
		
		int daemonCount = 0;
		Map<String, String> threadElapsedMap = new HashMap<String, String>();
		Multimap<String, String> stateMap = HashMultimap.create();
		for (Entry<String, ThreadInfo> entry : tiMap.entrySet()) {
			if (entry.getValue().isDaemon()) {
				daemonCount++;
			}
			
			stateMap.put(entry.getValue().getState(), entry.getKey());
			System.out.println(entry.getValue().getState());
			
			threadElapsedMap.put(entry.getValue().getName(), entry.getValue().getElapsed());
		}
		r.setDaemonCountMap(ImmutableMap.<String, Integer>builder().put("daemon", daemonCount).put("non-daemon", (tiMap.size() - daemonCount)).build());
		Map<String, Integer> countMap = new HashMap<String, Integer>();
		for (Entry<String, Collection<String>> entry : stateMap.asMap().entrySet()) {
			countMap.put(entry.getKey(), entry.getValue().size());
			System.out.println(entry.getKey() + "\t" + entry.getValue().size());
		}
		countMap.remove(null);
		r.setStateCountMap(countMap);
		r.setThreadElapsedMap(threadElapsedMap);
		
		Method root = new Method("root");
		Map<String, Method> classMethodDepthMap = new HashMap<String, Method>();
		Method parent = null;
		StringBuilder mapKey = new StringBuilder("");
		String mapKeyString = null;
		
		for (Map.Entry<String, ThreadInfo> entry : tiMap.entrySet()) {
			List<StackMethod> stack = entry.getValue().getStack();
			if (null != stack) {
				String classMethod = null;
				parent = root;
				
				mapKey.delete(0, mapKey.length());
				for (int i = stack.size() -1; i >= 0; i--) {
					StackMethod line = stack.get(i);
					classMethod = line.getMethod();
					mapKey.append(classMethod);
					
					mapKeyString = mapKey.toString();
					if (classMethodDepthMap.containsKey(mapKeyString)) {
						Method element = classMethodDepthMap.get(mapKeyString);
						element.addOne();
						
						parent = element;
					} else {
						Method element = new Method(classMethod);
						List<Method> children = parent.getChildren();
						if (null == children) {
							children = new ArrayList<Method>();
							parent.setChildren(children);
						}
						children.add(element);
						
						classMethodDepthMap.put(mapKeyString, element);
						parent = element;
					}
				}
			}
		}
		int total = 0;
		for (Method Method : root.getChildren()) {
			total += Method.getValue();
		};
		root.setValue(total);
		r.setFlameData(root);
		
		return r;
	}
	
	public String analysisAndSaveToCache(ThreadDump dump) {
		Report report = this.analysis(dump);
		String uuid = UUID.randomUUID().toString();
		DUMP_REPORT_CACHE.put(uuid, report);
		
		return uuid;
	}
	
	public Report getReportById(String uuid) {
		return DUMP_REPORT_CACHE.getIfPresent(uuid);
	}
	
	public class Method {
		public Method(String method) {
			this.name = method;
		}
		private String name;
		private int value = 1;
		
		private List<Method> children;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public List<Method> getChildren() {
			return children;
		}

		public void setChildren(List<Method> children) {
			this.children = children;
		}
		
		public void addOne() {
			this.value += 1;
		}
	}
}
