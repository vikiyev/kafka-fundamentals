package com.demiglace.kafka.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demiglace.kafka.dto.User;
import com.demiglace.kafka.service.UserProducerService;

@RestController
@RequestMapping("/userapi")
public class UserController {
	@Autowired
	private UserProducerService service;

	@PostMapping("/publishuserdata")
	public void sendUserData(@RequestBody User user) {
		service.sendUserData(user);
	}
}
