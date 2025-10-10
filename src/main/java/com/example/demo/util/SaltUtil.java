package com.example.demo.util;

import java.security.SecureRandom;

public class SaltUtil {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 生成随机盐
     * @param length 盐长度
     * @return 盐
     */
    public static String generateSalt(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder salt = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            salt.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return salt.toString();
    }

}
