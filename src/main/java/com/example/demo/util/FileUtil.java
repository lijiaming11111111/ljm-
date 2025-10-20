package com.example.demo.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import com.example.demo.entiy.File;
import com.example.demo.exception.BaseException;
import com.example.demo.mapper.FileMapper;
import com.example.demo.vo.file.FileUrlVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class FileUtil {
    // S3 客户端对象，用于操作对象存储服务
    private final S3Client s3Client;
    // S3 预签名器，用于生成带签名的临时 URL
    @Autowired
    private S3Presigner s3Presigner;
    // 对象存储桶名称
    private String bucketName;
    private  FileMapper fileMapper;

    private final RestTemplate restTemplate;

    public FileUtil(S3Client s3Client, @Value("${tebi.bucket-name}") String bucketName, FileMapper fileMapper, RestTemplate restTemplate) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.fileMapper = fileMapper;
        this.restTemplate = restTemplate;
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
        File sqlFile=new File();
        sqlFile.setId(IdWorker.getId());
        sqlFile.setFileName(file.getOriginalFilename());
        sqlFile.setObjectName(fileName);
        sqlFile.setBucketName(bucketName);
        sqlFile.setUploadTime(LocalDateTime.now());
        fileMapper.insert(sqlFile);
        return fileName;
    }

    /**
     * 生成文件下载的URL
     *
     * @param fileName 要下载的文件名
     * @return 下载URL
     */
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
                    // 设置响应头，和上面的 map 作用一致
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


    /**
     * 生成文件可用于PUT上传的预签名URL
     *
     * @param file 相关文件
     * @return 预签名URL
     */
    public FileUrlVO url(MultipartFile file)  {
        try {
            //获取原始文件名
            String originalName = file.getOriginalFilename();
            //获取文件扩展名
            String fileExt = originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";
            //文件唯一ID
            String uniqueFileName =  UUID.randomUUID() + fileExt;

            //指定存储桶
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .contentType(file.getContentType())
                    .build();
            //生成url
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
            fileMapper.selectById(sqlFile.getId());
            FileUrlVO fileUrlVO = new FileUrlVO();
            fileUrlVO.setUrl(presignedUrl);
            fileUrlVO.setId(sqlFile.getId());
            return fileUrlVO;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "生成URL失败：" + e.getMessage());
            return null;
        }
    }

    /**
     * 删除文件
     *
     * @param file 要删除的文件标识
     * @return 删除结果相关信息
     * @throws IOException 处理文件删除时可能抛出的IO异常
     */
    public String deleteFile(String file) throws IOException {
        try {
            // 1. 构建对象存储的删除请求
            DeleteObjectRequest deleteObjectRequest= DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file)
                    .build();
            // 2. 调用S3客户端删除对象存储中的文件
            s3Client.deleteObject(deleteObjectRequest);
            // 3. 构建数据库删除条件
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("object_name", file); // 字段名需与数据库一致
            fileMapper.delete(queryWrapper);
            return "删除成功";
        }catch (S3Exception e) {
            throw new BaseException("删除失败");
        }
    }

    /**
     * 通过预签名 URL 上传文件
     * @param url 已生成的预签名 URL
     * @param file 要上传的文件
     * @return 上传结果（成功/失败信息）
     */
    public String uploadFileUrl(String url, MultipartFile file) {
        try {

            // 1. 检查文件是否为空
            if (file.isEmpty()) {
                return "上传失败：文件为空";
            }
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.name());
            // 2. 设置请求头（根据 S3 协议，需指定文件 Content-Type）
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));
            headers.setContentLength(file.getSize());

            // 3. 构建请求体（文件字节流）
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            // 4. 发送 PUT 请求到预签名 URL
            ResponseEntity<Void> response = restTemplate.exchange(
                    decodedUrl,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );
            // 5. 检查响应状态（200/204 表示成功）
            if (response.getStatusCode().is2xxSuccessful()) {
                return "文件上传成功";
            } else {
                return "文件上传失败，状态码：" + response.getStatusCodeValue();
            }

        } catch (IOException e) {
            return "文件读取失败：" + e.getMessage();
        } catch (Exception e) {
            return "上传请求失败：" + e.getMessage();
        }
    }
}
