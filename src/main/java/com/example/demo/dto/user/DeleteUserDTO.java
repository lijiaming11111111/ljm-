package com.example.demo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DeleteUserDTO {
    @Schema(description = "要删除的用户id")
    private List<Long> idlist;
}
