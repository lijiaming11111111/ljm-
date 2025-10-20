package com.example.demo.vo.role;



import com.example.demo.enumerate.role.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RoleGroupListVO {

    @Schema(description = "角色组名称")
    private String groupName;

    @Schema(description = "角色组")
    private RoleType roleType;

    @Schema(description = "角色列表")
    private List<RoleDataVO> roleList;

}
