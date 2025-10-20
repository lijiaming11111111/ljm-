package com.example.demo.enumerate.role;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "角色类型（1.系统预设；2.自定义）")
public enum RoleType {

    /**
     * 角色类型-系统预设
     */
    SYSTEM(1, "系统预设"),
    /**
     * 角色类型-自定义
     */
    CUSTOM(2, "自定义");

    @JsonValue
    @EnumValue
    private final Integer code;

    private final String desc;


}
