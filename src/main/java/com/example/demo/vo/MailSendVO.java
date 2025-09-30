package com.example.demo.vo;

import com.example.demo.enumerate.Status;
import lombok.Data;

import java.util.Date;

@Data
public class MailSendVO {


    private String sendEmail;

    private String recipientEmail;

    private Date sendDate;

    private Status status;

    private String content;
}
