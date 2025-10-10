package com.example.demo.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusEnum {
    STOP("禁用", 0, "STOP"),
    START("启用", 1, "START");

    private final String type;

    @JsonValue
    private final Integer code;

    @EnumValue
    private final String enumName;
}
