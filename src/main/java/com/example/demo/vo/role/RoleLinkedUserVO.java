package com.example.demo.vo.role;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: 汪润杰
 * @Date: 24 4月 2025 15:40
 */
@Data
public class RoleLinkedUserVO {

    @Schema(description = "用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "用户名称")
    private String name;

    @Schema(description = "科室名称")
    private String department;

    @Schema(description = "是否关联(true:已关联, false:未关联)")
    private Boolean isLinked;

}
