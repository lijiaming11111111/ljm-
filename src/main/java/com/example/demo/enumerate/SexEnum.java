package com.example.demo.enumerate;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SexEnum {

    WOMAN("女",0,"WOMAN"),
    MAN("男",1,"MAN");

    private final String type;

    @JsonValue
    private final Integer code;

    @EnumValue
    private final String enumName;
}
