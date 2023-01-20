package com.demiglace.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.demiglace.kafka.dto.User;

@Service
public class UserConsumerService {
	@KafkaListener(topics= {"user-topic"})
	public void consumeUserData(User user) {
		System.out.println("Received person age: " + user.getAge());
		System.out.println("Favorite genre: " + user.getFavGenre());
	}
}
