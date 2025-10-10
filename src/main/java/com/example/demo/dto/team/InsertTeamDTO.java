package com.example.demo.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class InsertTeamDTO {
    @Schema(description = "团队名称",required = true)
    private String teamName;

    @Schema(description = "团队介绍",required = true)
    private String teamIntroduction;

    @Schema(description = "联系方式",required = true)
    private String contactInformation;

    @Schema(description = "地址",required = true)
    private String address;
}
