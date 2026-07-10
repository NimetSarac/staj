package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private String token;
    private Long userId;
    private String username;
    private String role;
    // ŞİFRE YOK — hassas bilgi döndürülmüyor
}