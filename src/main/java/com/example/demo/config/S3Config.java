package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * S3Client工具类
 * 用于创建和配置Amazon S3客户端及预签名URL生成器
 * 提供了S3服务的访问凭证配置和客户端实例化功能
 */
@Configuration
public class S3Config {

    // S3服务端点URL，用于指定访问的S3服务地址
    // 可以是AWS官方S3服务(如https://s3.amazonaws.com)或兼容S3协议的其他对象存储服务
    String endpoint="http://s3.tebi.io";
    // AWS访问密钥ID，用于身份验证
    String accessKey="Gn9E8D9S6revGzrk";
    // AWS私有访问密钥，用于身份验证
    // 格式通常为40个字符的字母数字组合
    String secretKey="bT0dPfVjdSoOkZLN0O5CWXiLn0U5yn4oVxxNouYp";
    // AWS区域，用于指定服务所在的地理区域
    String  region="sgb";

    /**
     * 创建并配置S3客户端实例
     * 该客户端用于与Amazon S3服务进行交互
     *
     * @return 配置好的S3Client对象
     */
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                // 设置服务端点，覆盖默认的AWS S3端点
                .endpointOverride(URI.create(endpoint))
                // 配置凭证提供者，使用静态凭证
                .credentialsProvider(StaticCredentialsProvider.create(
                        // 创建AWS基本凭证对象
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                // 设置服务区域
                .region(Region.of(region))
                .build();
    }

    /**
     * 创建并配置S3预签名URL生成器
     * 用于生成具有时效性的S3对象访问URL，无需直接暴露凭证
     *
     * @return 配置好的S3Presigner对象
     */
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                // 设置服务端点
                .endpointOverride(URI.create(endpoint))
                // 配置凭证提供者，使用静态凭证
                .credentialsProvider(StaticCredentialsProvider.create(
                        // 创建AWS基本凭证对象
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .region(Region.of(region))
                .build();
    }
}