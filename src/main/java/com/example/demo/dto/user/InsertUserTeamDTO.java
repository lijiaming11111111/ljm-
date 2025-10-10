package com.example.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class InsertUserTeamDTO {
    @Schema(description = "团队id",required = true)
    private List<Long> teamId;

    @Schema(description = "用户id",required = true)
    private List<Long> userId;

}
