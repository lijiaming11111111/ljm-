package com.example.demo.redis;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisCode {
    CODE("code:");


    @JsonValue
    @EnumValue
    private final String prefix;
}
