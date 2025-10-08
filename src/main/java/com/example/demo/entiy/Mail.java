package com.example.demo.entiy;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.demo.enumerate.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mail {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "邮件id")
    private Long id;

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
