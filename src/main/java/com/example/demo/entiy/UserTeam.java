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
@TableName("user_team")
public class UserTeam {
    @Schema(description = "团队关联id")
    private Long id;

    @Schema(description = "团队id")
    private Long teamId;

    @Schema(description = "用户id")
    private Long userId;

}
