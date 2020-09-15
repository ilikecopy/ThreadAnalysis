package com.yiqin.tool.thread.analysis.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yiqin.tool.thread.analysis.model.Report;
import com.yiqin.tool.thread.analysis.model.ThreadDump;
import com.yiqin.tool.thread.analysis.samples.mongodb.dao.StudentRepository;
import com.yiqin.tool.thread.analysis.samples.mongodb.entity.Student;
import com.yiqin.tool.thread.analysis.samples.mysql.dao.UserRepository;
import com.yiqin.tool.thread.analysis.samples.mysql.entity.User;
import com.yiqin.tool.thread.analysis.service.DumpParseService;
import com.yiqin.tool.thread.analysis.service.LongWorkService;
import com.yiqin.tool.thread.analysis.service.ReportService;

@Controller
public class WebController {

	@Autowired
	private LongWorkService longWorkService;

	@Autowired
	private DumpParseService parseService;

	@Autowired
	private ReportService reportService;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private StudentRepository studentRepository;
	

	@RequestMapping(value = "upload", method = RequestMethod.POST)
	public String upload(@RequestParam("file") MultipartFile file) {
		try {
			/**
			 * you can save to file and parse later or parse it now File targetFile = new
			 * File("/tmp/" + file.getOriginalFilename());//protocol
			 * FileUtils.copyToFile(file.getInputStream(), targetFile);
			 **/

			// handle the dump parse logic here
			ThreadDump dump = parseService.parse(file.getInputStream());
			String id = reportService.analysisAndSaveToCache(dump);

			return "redirect:/index.html?id=" + id;
		} catch (IOException e) {// TODO ModelAndView
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping("report/{id}")
	public @ResponseBody Report report(@PathVariable String id) {
		return this.reportService.getReportById(id);
	}

	@RequestMapping(value = "doLongWork")
	public @ResponseBody String doLongWork() {
		long duration = longWorkService.sleep();

		return "the work uses " + duration + "s";
	}

	@RequestMapping("mysqlUser")//TODO change to POST method
	public @ResponseBody User addNewUser(@RequestParam String name, @RequestParam String email) {
		User n = new User();
		n.setName(name);
		n.setEmail(email);
		userRepository.save(n);
		return n;
	}
	
	@RequestMapping("listMysqlUsers")
	public @ResponseBody Iterable<User> getAllUsers() {
	    return userRepository.findAll();
	  }
	
	@RequestMapping("student")//TODO change to POST method
	public @ResponseBody Student addNewStudent(@RequestParam String name, @RequestParam int age) {
		Student s = new Student();
		s.setUserName(name);
		s.setAge(age);
		studentRepository.save(s);
		return s;
	}
	
	@RequestMapping("listStudents")
	public @ResponseBody Iterable<Student> getAllStudents() {
	    return studentRepository.findAll();
	  }
}
