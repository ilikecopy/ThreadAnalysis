package com.yiqin.tool.thread.analysis.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.ImmutableMap;
import com.yiqin.tool.thread.analysis.model.Report;
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
		for (Entry<String, ThreadInfo> entry : tiMap.entrySet()) {
			if (entry.getValue().isDaemon()) {
				daemonCount++;
			}
			
			threadElapsedMap.put(entry.getValue().getName(), entry.getValue().getElapsed());
		}
		r.setDaemonCountMap(ImmutableMap.<String, Integer>builder().put("daemon", daemonCount).put("non-daemon", (tiMap.size() - daemonCount)).build());
		r.setThreadElapsedMap(threadElapsedMap);
		
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
}
