package com.example.demo.dto;

import lombok.Data;

@Data
public class MailSendDTO {

    private String recipientEmail;

    private String content;
}
