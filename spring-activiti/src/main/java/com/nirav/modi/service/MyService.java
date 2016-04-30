package com.nirav.modi.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirav.modi.entity.Person;
import com.nirav.modi.repository.PersonRepository;

@Service
@Transactional
public class MyService {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private PersonRepository personRepository;

	public void startProcess(String assignee) {

		Person person = personRepository.findByUsername(assignee);

		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("person", person);
		runtimeService.startProcessInstanceByKey("oneTaskProcess", variables);
		runtimeService.startProcessInstanceByKey("multiTaskProcess", variables);
	}

	public List<Task> getTasks(String assignee) {
		return taskService.createTaskQuery().taskAssignee(assignee).list();
	}

	public void createDemoUsers() {
		if (personRepository.findAll().size() == 0) {
			personRepository.save(new Person("jbarrez", "Joram", "Barrez", new Date()));
			personRepository.save(new Person("nrvmodi", "Nirav", "Modi", new Date()));
			personRepository.save(new Person("sonammodi", "Sonam", "Modi", new Date()));
			personRepository.save(new Person("trademakers", "Tijs", "Rademakers", new Date()));
		}
	}

}