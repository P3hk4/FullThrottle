package com.fullthrottle.DTO;

import lombok.*;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse{
    private String token;
    private Long userId;
    private String email;
    private String username;
    private String role;
}