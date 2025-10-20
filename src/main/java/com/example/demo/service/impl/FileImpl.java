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

    private final FileUtil fileUtil;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        return fileUtil.uploadFile(file);
    }

    @Override
    public String url(MultipartFile file) {
        return fileUtil.url(file);
    }

    @Override
    public String generateDownloadUrl(String fileName) {
        return fileUtil.generateDownloadUrl(fileName);
    }

    @Override
    public String deleteFile(String file)throws IOException{
        return fileUtil.deleteFile(file);
    }
}
