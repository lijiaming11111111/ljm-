package com.example.demo.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import com.example.demo.entiy.File;
import com.example.demo.mapper.FileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class FileUtil {

    private final S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    private String bucketName;

    private  FileMapper fileMapper;

    public FileUtil(S3Client s3Client, @Value("${tebi.bucket-name}") String bucketName, FileMapper fileMapper) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.fileMapper = fileMapper;
    }

    /**
     * 上传文件到 TEBI 桶
     * @param file 待上传的文件
     * @return 上传后的文件路径（S3 中的 key）
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // 生成唯一的文件名，避免重复
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        // 构建上传请求
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)  // S3 中的文件标识（key）
                .contentType(file.getContentType())
                .build();

        // 执行上传
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        //数据库
        File sqlFile=new File();
        sqlFile.setId(IdWorker.getId());
        sqlFile.setFileName(file.getOriginalFilename());
        sqlFile.setObjectName(fileName);
        sqlFile.setBucketName(bucketName);
        sqlFile.setUploadTime(LocalDateTime.now());
        fileMapper.insert(sqlFile);
        return fileName;  // 返回文件在 S3 中的 key
    }

    public String generateDownloadUrl(String fileName) {
        try {
            // 1. 检查文件是否存在（HEAD 请求，轻量高效）
            HeadObjectRequest headRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.headObject(headRequest);
            // 2. 生成预签名 GET 请求（用于下载文件）
            // 设置响应头，让浏览器下载文件
            Map<String, String> responseHeaders = new HashMap<>();
            // attachment 表示附件下载，filename 可指定下载后的文件名
            responseHeaders.put("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                    request -> request
                            .getObjectRequest(getObjectRequest)
                            .signatureDuration(Duration.ofDays(1)) // 设置 URL 有效期
            );

            // 3. 获取预签名 URL 并返回
            URL presignedUrl = presignedRequest.url();
            return presignedUrl.toString();
        } catch (SdkException e) {
            // 文件不存在或 S3 访问错误
            return "文件不存在或无法访问";
        }
    }


    public String url(MultipartFile file)  {
        try {
            String originalName = file.getOriginalFilename();
            String fileExt = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            String uniqueFileName =  UUID.randomUUID() + fileExt;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();

            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                    (builder) -> builder.putObjectRequest(putObjectRequest)
                            .signatureDuration(Duration.ofMinutes(30))
            );

            URL presignedUrl = presignedPutObjectRequest.url();

            //数据库
            File sqlFile=new File();
            sqlFile.setId(IdWorker.getId());
            sqlFile.setFileName(originalName);
            sqlFile.setObjectName(uniqueFileName);
            sqlFile.setBucketName(bucketName);
            sqlFile.setUploadTime(LocalDateTime.now());
            fileMapper.insert(sqlFile);
            return String.valueOf(presignedUrl);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "生成URL失败：" + e.getMessage());
            return String.valueOf(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
        }
    }

    public String deleteFile(String file) throws IOException {
        try {
            DeleteObjectRequest deleteObjectRequest= DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("object_name", file); // 字段名需与数据库一致
            fileMapper.delete(queryWrapper);
            return "删除成功";
        }catch (S3Exception e) {
            return "删除失败";
        }
    }
}
