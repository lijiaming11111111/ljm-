package com.example.demo.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AddTeamMailDTO {
    @Schema(description = "团队ID",required = true)
    private List<Long> id;

    @Schema(description = "邮件内容",required = true)
    private String content;
}
