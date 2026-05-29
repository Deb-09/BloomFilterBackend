package com.rubun.bloom_username_checker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UsernameCheckResponse {
    private String username;
    private boolean available;
    private String checkedBy; // BLOOM FILTER or DATABASE
    private String message;
}
