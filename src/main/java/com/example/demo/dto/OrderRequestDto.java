package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotNull(message = "Kullanıcı ID zorunludur")
    private Long userId;

    @NotBlank(message = "Ad soyad zorunludur")
    private String fullname;

    @NotBlank(message = "Banka adı zorunludur")
    private String bankName;
}