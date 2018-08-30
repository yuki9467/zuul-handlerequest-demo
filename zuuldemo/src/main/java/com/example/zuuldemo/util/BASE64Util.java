package com.example.zuuldemo.util;
import java.util.HashMap;
import java.util.Map;
/**
 * Base64加解密算法
 * </p>
 * Base64加密算法：<br/>
 * 1、获取字符串中每个字符的ASCII码；<br/>
 * 2、按照每3个8bit的字符为一组来分组，即每组24bit；<br/>
 * 3、将这24bit划分成4个6bit的4个单位，每个单位前面添加2个0，得到4个8bit的单位；<br/>
 * 4、将每个8bit的单位转换成十进制数字，对照Base64编码表找到对应的字符进行拼接，得到最终的加密后的字符串。<br/>
 * </p>
 * Base64解密算法：<br/>
 * 1、读入4个字符，对照Base64编码表找到字符对应的索引，生成4个6为的值；<br/>
 * 2、将这4个6为的值拼接起来，形成一个24为的值；<br/>
 * 3、将这个24位的值按照8位一组截断成3个8位的值；<br/>
 * 4、对照ASCII表找到这三个8位的值对应的字符，即解码后的字符。<br/>
 * </p>
 * 注意事项：<br/>
 * 1、被编码的字符必须是8bit的，即必须在ASCII码范围内，中文不行；<br/>
 * 2、如果被编码的字符长度不是3的倍数，则在最后添加1或2个0，对应的输出字符为“=”；
 * 3、给定一个字符串，用Base64方法对其进行加密后解密，得到的结果就不是开始时候的字符串了。<br/>
 */
public class BASE64Util {
    private static final Map<Integer, Character> base64CharMap = new HashMap<>();
    private static final String base64CharString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

    private static BASE64Util instance;

    private BASE64Util() {
        for (int i = 0; i < base64CharString.length(); i++) {
            char c = base64CharString.charAt(i);
            base64CharMap.put(new Integer(i), new Character(c));
        }
    }

    public static BASE64Util getInstance() {
        if (instance == null) {
            synchronized (BASE64Util.class) {
                if (instance == null) {
                    instance = new BASE64Util();
                }
            }
        }
        return instance;
    }

    /**
     * This method is used to encode a normal string to base64 string @param
     * origin The String to be encoded @return The String after encoded.
     */
    public String encode(String origin) {
        if (origin == null) {
            return null;
        }
        if (origin.length() == 0) {
            return "";
        }
        int length = origin.length();
        String binaryString = "";
        // to binary String
        for (int i = 0; i < length; i++) {
            int ascii = origin.charAt(i);
            String binaryCharString = Integer.toBinaryString(ascii);
            while (binaryCharString.length() < 8) {
                binaryCharString = "0" + binaryCharString;
            }
            binaryString += binaryCharString;
        }

        // to base64 index
        int beginIndex = 0;
        int endIndex = beginIndex + 6;
        String base64BinaryString = "";
        String charString = "";
        while ((base64BinaryString = binaryString.substring(beginIndex, endIndex)).length() > 0) {
            // if length is less than 6, add "0".
            while (base64BinaryString.length() < 6) {
                base64BinaryString += "0";
            }
            int index = Integer.parseInt(base64BinaryString, 2);
            char base64Char = base64CharMap.get(index);
            charString = charString + base64Char;
            beginIndex += 6;
            endIndex += 6;
            if (endIndex >= binaryString.length()) {
                endIndex = binaryString.length();
            }
            if (endIndex < beginIndex) {
                break;
            }
        }
        if (length % 3 == 2) {
            charString += "=";
        }
        if (length % 3 == 1) {
            charString += "==";
        }
        return charString;
    }

    public String decode(String encodedString) {
        if (encodedString == null) {
            return null;
        }
        if (encodedString.length() == 0) {
            return "";
        }
        // get origin base64 String
        String origin = encodedString.substring(0, encodedString.indexOf("="));
        String equals = encodedString.substring(encodedString.indexOf("="));

        String binaryString = "";
        // convert base64 string to binary string
        for (int i = 0; i < origin.length(); i++) {
            char c = origin.charAt(i);
            int ascii = base64CharString.indexOf(c);
            String binaryCharString = Integer.toBinaryString(ascii);
            while (binaryCharString.length() < 6) {
                binaryCharString = "0" + binaryCharString;
            }
            binaryString += binaryCharString;
        }
        // the encoded string has 1 "=", means that the binary string has append
        // 2 "0"
        if (equals.length() == 1) {
            binaryString = binaryString.substring(0, binaryString.length() - 2);
        }
        // the encoded string has 2 "=", means that the binary string has append
        // 4 "0"
        if (equals.length() == 2) {
            binaryString = binaryString.substring(0, binaryString.length() - 4);
        }

        // convert to String
        String charString = "";
        String resultString = "";
        int beginIndex = 0;
        int endIndex = beginIndex + 8;
        while ((charString = binaryString.substring(beginIndex, endIndex)).length() == 8) {
            int ascii = Integer.parseInt(charString, 2);
            resultString += (char) ascii;
            beginIndex += 8;
            endIndex += 8;
            if (endIndex > binaryString.length()) {
                break;
            }
        }
        return resultString;
    }

    /*public static void main(String[] args) {
        String res = "I am the text to be encoded and decoded.";
        String key = "key";
        System.out.println("-------------------------BASE64--------------------------");
        String base64_encodedStr = BASE64Util.getInstance().encode(res);
        System.out.println("加密：" + base64_encodedStr);
        System.out.println("解密：" + BASE64Util.getInstance().decode(base64_encodedStr));
    }*/
}
