package com.example.demo.service;

import com.example.demo.vo.file.FileUrlVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

public interface FileService {
    /**
     * 文件上传接口
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 接收文件元信息，生成可用于PUT上传的预签名URL
     *
     * @return 包含预签名URL的响应
     */
    FileUrlVO url(MultipartFile file) throws IOException;

    /**
     * fileName 下载的文件铭
     * 通过请求参数传入文件名
     */
    String generateDownloadUrl(String fileName);

    /**
     * 删除文件接口
     */
    String deleteFile(String file)throws IOException;


    /**
     * 生成文件可用于PUT上传的预签名URL
     *
     * @param file 相关文件
     * @return 预签名URL
     * @throws IOException 处理URL生成时可能抛出的IO异常
     */
    String uploadFileUrl(String url, MultipartFile file) throws IOException;
}
