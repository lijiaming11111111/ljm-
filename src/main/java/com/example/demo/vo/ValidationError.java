package com.example.demo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ValidationError {

    @Schema(description = "字段名")
    private String field;

    @Schema(description = "错误信息")
    private String message;

}