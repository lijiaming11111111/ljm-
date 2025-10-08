package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class DeleteMailDTO {
    @Schema(description = "要删除的邮件id")
    private List<Long>idlist;
}
