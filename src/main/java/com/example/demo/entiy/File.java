package com.example.demo.entiy;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName("file")
public class File {
    @Schema(description = "文件ID")
    private Long id;

    @Schema(description = "源文件名")
    private String fileName;

    @Schema(description = "对象名")
    private String objectName;

    @Schema(description = "存储桶名称")
    private String bucketName;

    @Schema(description = "上传时间")
    private LocalDateTime uploadTime;

}
