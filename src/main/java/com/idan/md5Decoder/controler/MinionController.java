//package com.idan.md5Decoder.controler;
//
//import com.idan.md5Decoder.beans.Server;
//import org.springframework.stereotype.Controller;
//
//import javax.xml.bind.DatatypeConverter;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.List;
//
//@Controller
//public class MinionController extends ServerController implements Runnable {
//
//    private List<String> hashesToDecode;
//
//    public MinionController(Server thisServer) {
//        super(thisServer);
//    }
//
//    private static String returnMD5Hash(String str) throws NoSuchAlgorithmException {
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        md.update(str.getBytes());
//        byte[] digest = md.digest();
//        return DatatypeConverter.printHexBinary(digest).toUpperCase();
//    }
//
//    public boolean isCorrectPassword(String passwordAttempt, String hashToDecode) throws NoSuchAlgorithmException {
//        return hashToDecode.equals(returnMD5Hash(passwordAttempt));
//    }
//
//    @Override
//    public void run() {
//
//    }
//}
