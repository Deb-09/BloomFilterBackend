package com.rubun.bloom_username_checker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BloomUsernameCheckerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BloomUsernameCheckerApplication.class, args);
		System.out.println("spring is running");
	}

}

/*
App startup

Spring creates BloomFilterService → injects UserRepository →
runs @PostConstruct → loads all usernames from MySQL → filter is warm and ready.

User types "john_doe" → GET /check?username=john_doe

UserController → UserService.checkUsername() → BloomFilterService.mightContain("john_doe")
→ returns true → UserRepository.existsByUsername() → DB confirms → returns TAKEN.

User types "zyx_newuser" → GET /check?username=zyx_newuser

UserController → UserService.checkUsername() → BloomFilterService.mightContain("zyx_newuser")
→ returns false → DB never called → returns AVAILABLE instantly.

User registers → POST /register

UserController → UserService.registerUser() → saves to MySQL via UserRepository.save()
→ calls BloomFilterService.add(username) → filter updated in memory.
 */
