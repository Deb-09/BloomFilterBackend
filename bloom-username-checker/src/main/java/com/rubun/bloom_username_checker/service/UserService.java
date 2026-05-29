package com.rubun.bloom_username_checker.service;

import com.rubun.bloom_username_checker.dto.UsernameCheckResponse;
import com.rubun.bloom_username_checker.model.User;
import com.rubun.bloom_username_checker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j

public class UserService {

    private final UserRepository userRepository;
    private final BloomFilterService bloomFilterService;

    public UsernameCheckResponse checkUsername(String username){
        validateUsername(username);
        log.info("Checking username: '{}'", username);

        // Step 1 — ask the bloom filter first
        boolean mightExist = bloomFilterService.mightContain(username);

        if(!mightExist){
            // Bloom filter is 100% certain — don't touch the DB
            log.info("'{}' → AVAILABLE (bloom filter short-circuit)", username);

            return UsernameCheckResponse.builder()
                    .username(username)
                    .available(true)
                    .checkedBy("BLOOM FILTER")
                    .message("Username is available")
                    .build();
        }

        // Step 2 — bloom filter said maybe, verify with DB
        boolean existsInDb = userRepository.existsByUsername(username);

        if(existsInDb){
            log.info("'{}' → TAKEN (confirmed by database)", username);
            return UsernameCheckResponse.builder()
                    .username(username)
                    .available(false)
                    .checkedBy("DATABASE")
                    .message("Username is already taken!")
                    .build();
        }
        else {
            // False positive — bloom filter said maybe but DB says free
            log.info("'{}' → AVAILABLE (false positive resolved by database)", username);

            return UsernameCheckResponse.builder()
                    .username(username)
                    .available(true)
                    .checkedBy("DATABASE")
                    .message("Username is available")
                    .build();

        }
    }

    public UsernameCheckResponse registerUser(String username , String email){
        validateUsername(username);

        boolean mightExist = bloomFilterService.mightContain(username);
        boolean existsInDb = userRepository.existsByUsername(username);

        if(mightExist && existsInDb){
            return UsernameCheckResponse.builder()
                    .username(username)
                    .available(false)
                    .checkedBy("DATABASE")
                    .message("Username already taken - registration failed!")
                    .build();
        }
        // else save to Database -> create a user -> save to the repo/db
        User newUser = User.builder()
                .username(username)
                .email(email)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(newUser);

        // Update bloom filter so it knows about the new username immediately
        bloomFilterService.add(username);

        log.info("Registered new user: '{}'", username);

        return UsernameCheckResponse.builder()
                .username(username)
                .available(true)
                .checkedBy("DATABASE")
                .message("User registered successfully!")
                .build();
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        if (username.length() > 20) {
            throw new IllegalArgumentException("Username cannot exceed 20 characters");
        }
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers and underscores");
        }
    }
}
