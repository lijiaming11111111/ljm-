package com.example.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SendVerificationCodeDTO {
    @Schema(description = "账号",required = true)
    private String mail;
}
