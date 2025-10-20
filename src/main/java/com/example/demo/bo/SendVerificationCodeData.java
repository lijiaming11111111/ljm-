package com.example.demo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class SendVerificationCodeData {
    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "账号")
    private String mail;

    @Schema(description = "验证码")
    private String code;

    @Schema(description = "保存验证码过期时间")
    private Date endTime;
}
