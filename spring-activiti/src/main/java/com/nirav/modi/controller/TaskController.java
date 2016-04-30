package com.nirav.modi.controller;

import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/tasks")
public class TaskController {

	@Autowired
	private TaskService taskService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String tasks(ModelMap map) {
		map.put("tasks", taskService.createTaskQuery().list());
		return "tasks";
	}

	@RequestMapping(value = "/complete/{taskId}", method = RequestMethod.GET)
	public String comlete(@PathVariable String taskId) {
		taskService.complete(taskId);
		return "redirect:/tasks/list";
	}

}
