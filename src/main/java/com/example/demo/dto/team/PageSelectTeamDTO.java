package com.example.demo.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PageSelectTeamDTO {
    @Schema(description = "团队名称")
    private String teamName;

    @Schema(description = "团队介绍")
    private String teamIntroduction;

    @Schema(description = "联系方式")
    private String contactInformation;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "页码", defaultValue = "1",required = true)
    private Integer page;

    @Schema(description = "每页显示记录数", defaultValue = "10",required = true)
    private Integer pageSize;
}
