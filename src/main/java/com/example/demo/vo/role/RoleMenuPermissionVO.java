package com.example.demo.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: 汪润杰
 * @Date: 25 4月 2025 12:29
 */
@Data
public class RoleMenuPermissionVO {

    @Schema(description = "拥有的菜单ID")
    private List<String> menuIds;

    @Schema(description = "拥有的权限ID")
    private List<String> permissionIds;

}
