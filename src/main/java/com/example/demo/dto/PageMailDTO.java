package com.example.demo.dto;

import com.example.demo.enumerate.Status;
import lombok.Data;

import java.util.Date;

@Data
public class PageMailDTO {

    private String sendEmail;

    private String recipientEmail;


    private Status status;

    private String content;

    private Long page;

    private Long pageSize;
}
