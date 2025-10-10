package com.example.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DeleteUserTeamDTO {
    @Schema(description = "团队关联ID",required = true)
    private List<Long> ids;
}
