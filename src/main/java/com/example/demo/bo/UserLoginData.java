package com.example.demo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class UserLoginData {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户组列表")
    private List<Long> roleIds;

    @Schema(description = "当前Token")
    private String token;
}
