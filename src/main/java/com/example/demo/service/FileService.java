package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    /**
     * 文件上传接口
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 接收文件元信息，生成可用于PUT上传的预签名URL
     * @return 包含预签名URL的响应
     */
    String url(MultipartFile file) throws IOException;

    /**
     * fileName 下载的文件铭
     * 通过请求参数传入文件名
     */
    String generateDownloadUrl(String fileName);

    /**
     * 删除文件接口
     */
    String deleteFile(String file)throws IOException;
}
