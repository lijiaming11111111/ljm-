package com.example.demo.dto.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.example.demo.enumerate.SexEnum;
import com.example.demo.enumerate.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Date;

@Data
public class UpdateUserDTO {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户id")
    @NotBlank(message = "用户id不能为空")
    private String id;

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

    @Schema(description = "出生年月")
    private Date birthday;

    @Schema(description = "账号状态枚举")
    private StatusEnum statusEnum;

}
