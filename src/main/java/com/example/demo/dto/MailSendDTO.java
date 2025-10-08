package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MailSendDTO {
    @Schema(description = "接收者邮箱")
    private String recipientEmail;

    @Schema(description = "邮件内容")
    private String content;
}
