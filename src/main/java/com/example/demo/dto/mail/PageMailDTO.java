package com.example.demo.dto.mail;

import com.example.demo.enumerate.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class PageMailDTO {

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

    @Schema(description = "页码", defaultValue = "1",required = true)
    @NotNull(message = "页码为空")
    @Min(value = 1, message = "页码必须大于0")
    private Long page;

    @Schema(description = "每页显示记录数", defaultValue = "10",required = true)
    @NotNull(message = "页大小为空")
    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 50, message = "页大小不能超过50")
    private Long pageSize;
}
