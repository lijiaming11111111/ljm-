package com.example.demo.util;


import com.example.demo.exception.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;


@Component
public class CodeUtil {

    static RedisTemplate<String, String> redisTemplate;


    public static String generateCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    public static Boolean checkCode(String key, String code) {
        // 用于存储从 Redis 获取的真实验证码
        String trueCode;
        try {
            // 1. 从 Redis 获取验证码：
            //    key 拼接为 "code:" + key（规范 Redis key 格式，方便区分业务）
            //    get 方法获取值后，replaceAll("\"", "") 处理 Redis 返回值可能带的转义双引号（如值存储为 "123456" 时，会带引号）
            trueCode = redisTemplate.opsForValue().get("code:" + key).replaceAll("\"", "");
        } catch (Exception e) {
            // 2. 异常处理：Redis 读取失败（如 key 不存在、网络问题等），将 trueCode 置为 null
            trueCode = null;
        }
        // 3. 校验 Redis 中是否存在该验证码
        if (trueCode == null) {
            // 若不存在，抛出自定义异常（需提前定义 BaseException），提示用户先发送验证码
            throw new BaseException("请先发送验证码");
        }
        // 4. 比对用户输入的验证码和 Redis 中存储的真实验证码
        if (code.equals(trueCode)) {
            // 5. 校验通过：删除 Redis 中的验证码（防止重复使用）
            redisTemplate.delete("code:" + key);
            return true;
        }
        throw new BaseException("验证码错误");
    }

    @Autowired
    public void setUcClient(RedisTemplate<String, String> redisTemplate) {
        CodeUtil.redisTemplate = redisTemplate;
    }


}