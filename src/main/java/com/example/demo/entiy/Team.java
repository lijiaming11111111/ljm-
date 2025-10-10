package com.example.demo.entiy;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("team")
public class Team {
    @Schema(description = "团队ID")
    private Long id;

    @Schema(description = "团队名称")
    private String teamName;

    @Schema(description = "团队介绍")
    private String teamIntroduction;

    @Schema(description = "联系方式")
    private String contactInformation;

    @Schema(description = "地址")
    private String address;
}
