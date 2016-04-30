package com.nirav.modi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.nirav.modi.repository.PersonRepository;

@Controller
@RequestMapping("/persons")
public class PersonsController {

	@Autowired
	private PersonRepository personRepo;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String persons(ModelMap map) {
		map.put("persons", personRepo.findAll());
		return "persons";
	}

}
