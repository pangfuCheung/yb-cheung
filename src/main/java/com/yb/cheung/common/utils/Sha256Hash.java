package com.yb.cheung.common.utils;

import com.qiniu.util.Hex;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Sha256Hash {

    private static final char[] HEX_DIGITS = {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};

    private String text;

    private String SALT_VALUE = "abcdefghijklmnopqrstuvwxyz123456789!@#$%^&*()_+|";

    public Sha256Hash(){
        this.text = "123456";
    }

    public Sha256Hash(String text){
        this.text = text;
    }

    public Sha256Hash(String text,String salt){
        this.text = text;
        if (null==salt || "".equals(salt)){
            this.SALT_VALUE = salt;
        }
    }

    public String toHex(){
        return toSHA003(this.text);
    }


    public static String getSalt(){
        SecureRandom s  =new SecureRandom();
        byte b[] = new byte[15];
        s.nextBytes(b);
        return Base64.encodeBase64String(b);
    }

    public static void main(String[] args) {
        /*toSHA001("SHA-256adminYzcmCZNvbXocrsz9dm8e");
        toSHA002("admin");
        toSHA003("admin");*/
        String salt = getSalt();
        System.out.println(salt);
        System.out.println(new Sha256Hash("123456",salt).toHex());
    }

    private void toSHA001(String text) {

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));
            String output = Hex.encodeHexString(hash);
            System.out.println(output);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public String toSHA002(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
            System.out.println(encodeStr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }

    private String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

    private String toSHA003(String text) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte b[] = (text + this.byte2Hex(SALT_VALUE.getBytes())).getBytes();
            messageDigest.update(b);
            byte[] digest = messageDigest.digest();
            int length = digest.length;
            StringBuilder buf = new StringBuilder(length*2);
            //密文转换成16进制
            for(int j = 0 ; j < length ; j++){
                buf.append(HEX_DIGITS[(digest[j] >> 4) & 0x0f]);
                buf.append(HEX_DIGITS[digest[j] & 0x0f]);
            }
            System.out.println();
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
