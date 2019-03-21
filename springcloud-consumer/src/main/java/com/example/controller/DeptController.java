package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Result;
import com.example.service.DeptService;

@RequestMapping("/dept")
@RestController
public class DeptController {
	
	@Autowired
	private DeptService deptService;
	
	private Logger log = LoggerFactory.getLogger(DeptController.class);
	
	@RequestMapping("/get/{id}")
	public Result get(@PathVariable("id") Integer id) {
		log.info("Consumer 层 get method() 被调用");
		
		return deptService.get(id);
	}
}
