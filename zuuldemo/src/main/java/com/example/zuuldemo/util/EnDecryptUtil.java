package com.example.zuuldemo.util;


import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author :liuqi
 * @date :2018-08-29 13:33.
 */
public class EnDecryptUtil {
    // 无需创建对象
    private EnDecryptUtil() {

    }

    /**
     * SHA1加密Bit数据
     *
     * @param source byte数组
     * @return 加密后的byte数组
     */
    public static byte[] SHA1Bit(byte[] source) {
        try {
            MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");
            sha1Digest.update(source);
            byte targetDigest[] = sha1Digest.digest();
            return targetDigest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * SHA1加密字符串数据
     *
     * @param source 要加密的字符串
     * @return 加密后的字符串
     */
    public static String SHA1(String source) {
        return byte2HexStr(SHA1Bit(source.getBytes()));
    }

    /**
     * MD5加密Bit数据
     *
     * @param source byte数组
     * @return 加密后的byte数组
     */
    public static byte[] MD5Bit(byte[] source) {
        try {
            // 获得MD5摘要算法的 MessageDigest对象
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            md5Digest.update(source);
            // 获得密文
            return md5Digest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * MD5加密字符串,32位长
     *
     * @param source 要加密的内容
     * @return 加密后的内容
     */
    public static String MD5(String source) {
        return byte2HexStr(MD5Bit(source.getBytes()));
    }

    /**
     * AES加密
     *
     * @param content  待加密的内容
     * @param password 加密密码
     * @return
     */
    public static byte[] encryptBitAES(byte[] content, String password) {
        try {
            Cipher encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            encryptCipher.init(Cipher.ENCRYPT_MODE, getKey(password));// 初始化
            byte[] result = encryptCipher.doFinal(content);
            return result; // 加密
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // return null;
    }

    /**
     * AES解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static byte[] decryptBitAES(byte[] content, String password) {
        try {
            Cipher decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            decryptCipher.init(Cipher.DECRYPT_MODE, getKey(password));// 初始化
            byte[] result = decryptCipher.doFinal(content);
            return result; // 加密结果
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // return null;
    }

    /**
     * AES字符串加密
     *
     * @param content  待加密的内容
     * @param password 加密密码
     * @return
     */
    public static String encryptAES(String content, String password) {
        return byte2HexStr(encryptBitAES(content.getBytes(), password));
    }

    /**
     * AES字符串解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    public static String decryptAES(String content, String password) {
        return new String(decryptBitAES(hexStr2Bytes(content), password));
    }

    /**
     * 从指定字符串生成密钥
     *
     * @param password 构成该秘钥的字符串
     * @return 生成的密钥
     * @throws NoSuchAlgorithmException
     */
    private static Key getKey(String password) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        // 生成KEY
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(128, secureRandom);
        SecretKey secretKey = kgen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        // 转换KEY
        SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
        return key;
    }

    /**
     * 将byte数组转换为表示16进制值的字符串. 如：byte[]{8,18}转换为：0812 和 byte[]
     * hexStr2Bytes(String strIn) 互为可逆的转换过程.
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的字符串
     */
    public static String byte2HexStr(byte[] bytes) {
        int bytesLen = bytes.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer hexString = new StringBuffer(bytesLen * 2);
        for (int i = 0; i < bytesLen; i++) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                hexString.append(0);// 如果为1位 前面补个0
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 将表示16进制值的字符串转换为byte数组, 和 String byte2HexStr(byte[] bytes) 互为可逆的转换过程.
     *
     * @param strIn
     * @return 转换后的byte数组
     */
    public static byte[] hexStr2Bytes(String strIn) {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

}
