package com.example.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class PageSelectUserTeamDTO {
    @Schema(description = "团队id")
    private List<Long> teamId;

    @Schema(description = "用户id")
    private List<Long> userId;

    @Schema(description = "页码", defaultValue = "1",required = true)
    private Integer page;

    @Schema(description = "每页显示记录数", defaultValue = "10",required = true)
    private Integer pageSize;
}
