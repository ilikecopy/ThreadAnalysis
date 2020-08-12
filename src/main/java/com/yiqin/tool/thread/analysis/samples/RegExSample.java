package com.yiqin.tool.thread.analysis.samples;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExSample {

	public static void main(String[] args) {
		//http://www.tianxiaohui.com/index.php/Java%E7%9B%B8%E5%85%B3/Java-%E6%AD%A3%E5%88%99%E8%A1%A8%E8%BE%BE%E5%BC%8F.html
		Pattern pattern2 = Pattern.compile("\\w+@\\d{3}\\.+\\w{3}");
		Matcher matcher2 = pattern2.matcher("yiqin@163.com");
		System.out.println(matcher2.find());
		matcher2 = pattern2.matcher("yiqin@google.com");
		System.out.println(matcher2.find());
		
		pattern2 = Pattern.compile("^(.+)\\.(.+)$");
		matcher2 = pattern2.matcher("myhost.xxxx.edu.cn");
		if (matcher2.matches()) {
			System.out.println("\n1st matched:");
			System.out.println(matcher2.group(0));
			System.out.println(matcher2.group(1));
			System.out.println(matcher2.group(2));
		}
		
		pattern2 = Pattern.compile("^(.+?)\\.(.+)$");
		matcher2 = pattern2.matcher("myhost.xxxx.edu.cn");
		if (matcher2.matches()) {
			System.out.println("\n2nd matched:");
			System.out.println(matcher2.group(0));
			System.out.println(matcher2.group(1));
			System.out.println(matcher2.group(2));
		}
	}

}
