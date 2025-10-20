package com.example.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ForgetPasswordDTO {
    @Schema(description = "邮箱")
    private String mail;

    @Schema(description = "电话号码")
    private String mobile;

    @Schema(description = "验证码")
    private String code;

    @Schema(description = "新密码")
    private String newPassword;

}
