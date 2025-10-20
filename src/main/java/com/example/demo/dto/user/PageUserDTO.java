package com.example.demo.dto.user;

import com.example.demo.enumerate.SexEnum;
import com.example.demo.enumerate.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class PageUserDTO {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "邮箱")
    private String mail;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "性别枚举对象WOMAN女，MAN男")
    private SexEnum sexEnum;


    @Schema(description = "账号状态枚举")
    private StatusEnum statusEnum;

    @Schema(description = "页码", defaultValue = "1",required = true)
    @NotNull(message = "页码为空")
    @Min(value = 1, message = "页码必须大于0")
    private int page;

    @Schema(description = "每页显示记录数", defaultValue = "10",required = true)
    @NotNull(message = "页大小为空")
    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 50, message = "页大小不能超过50")
    private int pageSize;
}
