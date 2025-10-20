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
        String trueCode;
        try {
            trueCode = redisTemplate.opsForValue().get("code:" + key).replaceAll("\"", "");
        } catch (Exception e) {
            trueCode = null;
        }
        if (trueCode == null) {
            throw new BaseException("请先发送验证码");
        }
        if (code.equals(trueCode)) {
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