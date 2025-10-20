package com.example.demo.service.impl;

import com.example.demo.service.FileService;
import com.example.demo.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileImpl implements FileService {
    // 注入 FileUtil 工具类，用于实际执行文件操作（上传、下载、生成 URL 等）
    // final 保证依赖不可变，提升线程安全和代码可靠性
    private final FileUtil fileUtil;

    // 实现 FileService 接口的文件上传方法
    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // 调用 FileUtil 的上传方法，将文件上传逻辑委托给工具类处理
        // 返回文件上传后的标识（如 S3 中的 key）
        return fileUtil.uploadFile(file);
    }

    // 实现 FileService 接口的生成文件访问 URL 方法
    @Override
    public String url(MultipartFile file) {
        // 调用 FileUtil 的 url 方法，生成文件可访问的 URL（如预签名上传 URL）
        return fileUtil.url(file);
    }

    // 实现 FileService 接口的生成文件下载 URL 方法
    @Override
    public String generateDownloadUrl(String fileName) {
        // 调用 FileUtil 的生成下载 URL 方法，返回带签名的临时下载地址
        return fileUtil.generateDownloadUrl(fileName);
    }

    // 实现 FileService 接口的文件删除方法
    @Override
    public String deleteFile(String file)throws IOException{
        // 调用 FileUtil 的删除方法，执行文件删除操作（对象存储 + 数据库记录）
        return fileUtil.deleteFile(file);
    }
}
