package com.rubun.bloom_username_checker.controller;

import com.rubun.bloom_username_checker.dto.UsernameCheckResponse;
import com.rubun.bloom_username_checker.service.BloomFilterService;
import com.rubun.bloom_username_checker.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = {"*",
                "https://bloom-filter-ui.vercel.app/"})
public class UserController {

    private final UserService userService;
    private final BloomFilterService bloomFilterService;

    //check if username is available
    @GetMapping("/check")
    public ResponseEntity<UsernameCheckResponse> checkUsername(
        @RequestParam String username){

        UsernameCheckResponse response = userService.checkUsername(username);
        return ResponseEntity.ok(response);
    }

    //register a new user
    @PostMapping("/register")
    public ResponseEntity<UsernameCheckResponse> registerUser(
            @RequestParam String username,
            @RequestParam String email){

        UsernameCheckResponse response = userService.registerUser(username,email);

        if (!response.isAvailable()) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
    // Bloom filter stats
    @GetMapping("/stats")
    public ResponseEntity<BloomFilterService.BloomFilterStats> getStats() {
        return ResponseEntity.ok(bloomFilterService.getStats());
    }
}
