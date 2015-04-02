package com.hyd.appserver.utils;

import com.hyd.appserver.AppClientException;

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
            throw new AppClientException(e);
        }
        return encrypted;
    }

    /**
     * 解密字符串
     *
     * @param key       key
     * @param encrypted 被加密的内容
     *
     * @return 解密的内容
     */
    public static String decrypt(String key, byte[] encrypted) {
        String decrypted;

        try {
            Cipher encrypter = createCipher(key, Cipher.DECRYPT_MODE);
            decrypted = new String(encrypter.doFinal(encrypted), "UTF-8");
        } catch (Exception e) {
            throw new AppClientException(e);
        }
        return decrypted;
    }

    private static Cipher createCipher(String key, int mode)
            throws InvalidKeyException, InvalidKeySpecException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException {

        if (key.length() > KEY_SIZE) {
            throw new IllegalArgumentException("Key size must be under 24 characters.");
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

    private static String expandSize(String key) {
        StringBuilder stringBuilder = new StringBuilder(key);
        while (stringBuilder.length() < KEY_SIZE) {
            stringBuilder.append(" ");
        }
        key = stringBuilder.toString();
        return key;
    }
}
