package com.yiqin.tool.thread.analysis.service.impl;

import org.springframework.stereotype.Service;

import com.yiqin.tool.thread.analysis.service.LongWorkService;

@Service
public class LongWorkServiceImp implements LongWorkService {

	public long sleep() {
		long duration = (long) (Math.random() * 1000 * 100);
		System.out.println(duration);
		
		try {
			System.out.println("start sleep");
			Thread.sleep(duration);
			System.out.println("end sleep");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return duration;
	}
}
