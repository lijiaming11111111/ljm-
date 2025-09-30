package com.example.demo.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum Status {

    SUCCEED(1,"成功"),
    FAILED(2,"失败");


    @EnumValue
    final Integer code;

    @JsonValue
    final String name;
}
