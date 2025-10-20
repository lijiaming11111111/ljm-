package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(MultipartFile file) throws IOException;

    String url(MultipartFile file) throws IOException;

    String generateDownloadUrl(String fileName);

    String deleteFile(String file)throws IOException;
}
