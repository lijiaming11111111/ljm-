package com.example.demo.vo.role.permission;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public  class PermissionVO {

    @Schema(description = "权限ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "权限名称")
    private String name;

    @Schema(description = "权限描述")
    private String description;
}