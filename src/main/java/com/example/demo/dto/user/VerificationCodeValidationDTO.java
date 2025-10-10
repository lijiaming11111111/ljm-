package com.example.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VerificationCodeValidationDTO {
    @Schema(description = "邮箱")
    private String mail;

    @Schema(description = "验证码")
    private String verificationCode;
}
