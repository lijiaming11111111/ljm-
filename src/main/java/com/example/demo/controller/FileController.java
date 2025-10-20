package com.example.demo.controller;

import com.example.demo.result.Result;
import com.example.demo.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@Slf4j
@RequiredArgsConstructor
@Tag(name = "文件接口")
@Validated
@RestController
@RequestMapping("/api/upload")
public class FileController {

    private final FileService fileService;

    /**
     * 文件上传接口
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.success("请选择要上传的文件");
        }
        try {
            String fileKey = fileService.uploadFile(file);
            return Result.success("文件上传成功:" + fileKey);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }/**
     * 接收文件元信息，生成可用于PUT上传的预签名URL
     * @return 包含预签名URL的响应
     */
    @PutMapping("/uploadUrl")
    @Operation(summary = "上传的url")
    public Result<String> uploadUrl(@RequestParam("file") MultipartFile file) throws IOException {
        return Result.success("url生成成功",fileService.url(file));
    }

    /**
     * fileName 下载的文件铭
     * 通过请求参数传入文件名
     */
    @GetMapping("/downloadUrl")
    @Operation(summary = "下载的url")
    public Result<String> downloadUrl(@RequestParam("name") String fileName) {
        return Result.success(fileService.generateDownloadUrl(fileName),null);
    }

    /**
     * 删除文件接口
     */
    @GetMapping("/deleteFile")
    @Operation(summary = "删除文件")
    public Result<String> deleteFile(@RequestParam("file") String file) throws IOException {
        return Result.success(fileService.deleteFile(file),null);
    }
}
