package com.hyd.appserver.utils;

import com.hyd.appserver.AppServerException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * 对字符串进行 3DES 加密/解密
 *
 * @author yiding.he
 */
public class TripleDESUtils {

    private static final int KEY_SIZE = 24;  // 3DES 加密的 key 长度必须是 24 字节。

    /**
     * 对字符串加密
     *
     * @param key  key
     * @param text 要加密的字符串
     *
     * @return 加密后的内容
     */
    public static byte[] encrypt(String key, String text) {
        byte[] encrypted;

        try {
            Cipher encrypter = createCipher(key, Cipher.ENCRYPT_MODE);
            encrypted = encrypter.doFinal(text.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new AppServerException(e);
        }
        return encrypted;
    }

    private static Cipher createCipher(String key, int mode)
            throws InvalidKeyException, InvalidKeySpecException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {

        if (key.length() > KEY_SIZE) {
            throw new IllegalArgumentException("Key must be no more than 24 bytes.");
        } else {
            key = expandSize(key);
        }

        String algorithm = "DESede";
        String transformation = "DESede/CBC/PKCS5Padding";
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);

        byte[] keyValue = key.getBytes();
        DESedeKeySpec keySpec = new DESedeKeySpec(keyValue);

        SecretKey secretKey = SecretKeyFactory.getInstance(algorithm).generateSecret(keySpec);
        Cipher encrypter = Cipher.getInstance(transformation);
        encrypter.init(mode, secretKey, iv);
        return encrypter;
    }

    // 使 key 符合指定的长度
    private static String expandSize(String key) {

        if (key.length() == KEY_SIZE) {
            return key;
        }

        StringBuilder stringBuilder = new StringBuilder(key);
        while (stringBuilder.length() < KEY_SIZE) {
            stringBuilder.append(" ");
        }
        key = stringBuilder.toString();
        return key;
    }
}
