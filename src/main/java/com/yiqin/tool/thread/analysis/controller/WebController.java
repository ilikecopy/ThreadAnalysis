package com.yiqin.tool.thread.analysis.controller;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.yiqin.tool.thread.analysis.service.LongWorkService;

@Controller
public class WebController {
	
	@Autowired
	private LongWorkService longWorkService;
	
	@RequestMapping(value="upload", method=RequestMethod.POST)
	public String upload(@RequestParam("file") MultipartFile file) {
		try {
			File targetFile = new File("/tmp/" + file.getOriginalFilename());//protocol
		    FileUtils.copyToFile(file.getInputStream(), targetFile);
		    
		    
		    return "redirect:/index.html?id=" + targetFile.getAbsolutePath();
		} catch (IOException e) {//TODO ModelAndView
			e.printStackTrace();
			return null;
		}
	}

	
	@RequestMapping(value="doLongWork")
	public @ResponseBody String doLongWork() {
		long duration = longWorkService.sleep();
		
		return "the work uses " + duration + "s";
	}
}
