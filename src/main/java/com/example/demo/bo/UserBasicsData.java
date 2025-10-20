package com.example.demo.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserBasicsData {

    @Schema(description = "用户ID")
    private String Id;

    @Schema(description = "用户名称")
    private String name;
}
