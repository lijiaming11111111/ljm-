package com.example.demo.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import com.example.demo.entiy.File;
import com.example.demo.exception.BaseException;
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
    // S3 客户端对象，用于操作对象存储服务（如上传、下载、删除文件等），final 保证初始化后不可变
    private final S3Client s3Client;
    // S3 预签名器，用于生成带签名的临时 URL（如文件上传、下载的预签名地址），由 Spring 自动注入
    @Autowired
    private S3Presigner s3Presigner;
    // 对象存储桶名称，后续操作（上传、下载等）会用到该桶
    private String bucketName;
    // 文件Mapper，用于操作数据库中文件相关的表（如插入、查询、删除文件元数据）
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

        // 构建 S3 上传请求：配置存储桶、文件标识（key）、文件类型等元信息
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)// 指定文件要上传到的 S3 存储桶名称（需提前定义好）
                .key(fileName)  // 设置文件在 S3 中的唯一标识（后续下载、删除等用这个 key 找文件）
                .contentType(file.getContentType())// 设置文件的 MIME 类型（如 image/png、application/pdf 等）
                .build();

        // 执行文件上传：通过 S3 客户端，将文件流写入 S3 存储桶
        // RequestBody.fromInputStream 把 MultipartFile 的输入流、文件大小封装成请求体
        s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        // 数据库记录：将文件元信息存入数据库，用于业务层管理（如查询、关联业务数据等）
        File sqlFile=new File(); // 假设 File 是数据库实体类，对应文件信息表
        sqlFile.setId(IdWorker.getId());// 用分布式 ID 生成器（IdWorker）生成唯一主键
        sqlFile.setFileName(file.getOriginalFilename());// 记录用户上传的原始文件名
        sqlFile.setObjectName(fileName);// 记录文件在 S3 中的唯一标识（key）
        sqlFile.setBucketName(bucketName);// 记录文件所在的存储桶名称
        sqlFile.setUploadTime(LocalDateTime.now());// 记录文件上传时间（当前时间）
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
            // 构建 GetObjectRequest 对象，用于获取文件
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName) // 指定存储桶
                    .key(fileName)// 指定要下载的文件对象键
                    // 设置响应头，和上面的 map 作用一致（部分 S3 客户端可能两种方式都支持，按需选择）
                    .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
                    .build();

            // 生成预签名的 GetObject 请求（带临时访问权限和过期时间）
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
                    request -> request
                            .getObjectRequest(getObjectRequest)// 关联上面构建的获取文件请求
                            .signatureDuration(Duration.ofDays(1)) // 设置 URL 有效期
            );

            // 3. 获取预签名 URL 并返回
            URL presignedUrl = presignedRequest.url();
            return presignedUrl.toString();
        } catch (SdkException e) {
            // 文件不存在或 S3 访问错误
            throw new BaseException("文件不存在或无法访问");
        }
    }


    // 方法：生成文件上传的预签名 URL 并将文件信息存入数据库
    public String url(MultipartFile file)  {
        try {
            // 1. 解析文件原始名称和后缀
            // 获取用户上传文件的原始文件名（如 "example.jpg"）
            String originalName = file.getOriginalFilename();
            // 截取文件后缀（通过判断是否包含"."，从最后一个"."位置截取；若没有"."则后缀为空字符串）
            String fileExt = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            // 2. 生成唯一文件名（UUID + 后缀）
            // 用 UUID 保证文件名唯一性，避免重复，再拼接之前截取的后缀
            String uniqueFileName =  UUID.randomUUID() + fileExt;

            // 3. 构建对象存储的上传请求（以 S3 为例）
            // 创建 PutObjectRequest 对象，设置存储桶名称、对象键（唯一文件名）、文件内容类型
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();

            // 4. 生成预签名上传 URL（带过期时间）
            // 通过 s3Presigner 生成预签名请求，设置关联的上传请求和签名有效期（30 分钟）
            PresignedPutObjectRequest presignedPutObjectRequest = s3Presigner.presignPutObject(
                    (builder) -> builder.putObjectRequest(putObjectRequest)
                            .signatureDuration(Duration.ofMinutes(30))
            );
            // 获取最终可用于上传的预签名 URL 对象
            URL presignedUrl = presignedPutObjectRequest.url();

            // 5. 写入数据库：记录文件元信息
            // 新建 File 实体类对象，用于封装要存入数据库的文件信息
            File sqlFile=new File();
            // 设置文件 ID（假设 IdWorker 是生成唯一 ID 的工具类，需提前定义）
            sqlFile.setId(IdWorker.getId());
            // 存储原始文件名（用户上传时的名称）
            sqlFile.setFileName(originalName);
            // 存储对象存储中使用的唯一文件名
            sqlFile.setObjectName(uniqueFileName);
            // 存储文件所在的存储桶名称
            sqlFile.setBucketName(bucketName);
            // 设置文件上传时间为当前时间
            sqlFile.setUploadTime(LocalDateTime.now());
            fileMapper.insert(sqlFile);
            // 6. 返回预签名 URL 的字符串形式
            return String.valueOf(presignedUrl);

        } catch (Exception e) {
            // 异常处理：若上述流程出错，构建错误响应
            // 新建 HashMap 用于封装错误信息
            Map<String, Object> error = new HashMap<>();
            // 标记操作失败
            error.put("success", false);
            // 存入错误提示，说明是生成 URL 失败及具体异常信息
            error.put("message", "生成URL失败：" + e.getMessage());
            // 返回错误响应的字符串形式（这里直接转字符串，实际可能需要更规范的响应处理）
            return String.valueOf(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
        }
    }

    public String deleteFile(String file) throws IOException {
        try {
            // 1. 构建对象存储的删除请求（指定桶名和文件唯一标识）
            DeleteObjectRequest deleteObjectRequest= DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file)
                    .build();
            // 2. 调用S3客户端删除对象存储中的文件
            s3Client.deleteObject(deleteObjectRequest);
            // 3. 构建数据库删除条件（通过fileId匹配要删除的记录）
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("object_name", file); // 字段名需与数据库一致
            fileMapper.delete(queryWrapper);
            return "删除成功";
        }catch (S3Exception e) {
            throw new BaseException("删除失败");
        }
    }
}
