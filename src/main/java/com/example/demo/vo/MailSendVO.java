package com.example.demo.vo;

import com.example.demo.enumerate.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class MailSendVO {


    @Schema(description = "发送者邮箱")
    private String sendEmail;

    @Schema(description = "接收者邮箱")
    private String recipientEmail;

    @Schema(description = "发送时间")
    private Date sendDate;

    @Schema(description = "发送状态(1成功;2失败)")
    private Status status;

    @Schema(description = "邮件内容")
    private String content;
}
