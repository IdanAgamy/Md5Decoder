package com.idan.md5Decoder.Md5Decoder.controler;

import org.springframework.stereotype.Controller;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Controller
public class MinionController {

    private String hashToDecode;

    public String getHashToDecode() {
        return hashToDecode;
    }

    public void setHashToDecode(String hashToDecode) {
        this.hashToDecode = hashToDecode;
    }

    private static String returnMD5Hash(String str) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest).toUpperCase();
    }

    public boolean isCorrectPassword(String passwordAttempt) throws NoSuchAlgorithmException {
        return this.hashToDecode.equals(returnMD5Hash(passwordAttempt));
    }
}
