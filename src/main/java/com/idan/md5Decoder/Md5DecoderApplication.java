package com.idan.md5Decoder;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class Md5DecoderApplication {

	public static void main(String[] args) throws NoSuchAlgorithmException {
//		SpringApplication.run(Md5DecoderApplication.class, args);
		String hash = "35454B055CC325EA1AF2126E27707052";
		String password = "ILoveJava";

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(password.getBytes());
		byte[] digest = md.digest();
		String myHash = DatatypeConverter
				.printHexBinary(digest).toUpperCase();
		System.out.println(myHash);
		System.out.println(hash);

	}

	public static String returnMD5Hash(String str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str.getBytes());
		byte[] digest = md.digest();
		return DatatypeConverter.printHexBinary(digest).toUpperCase();
	}

}
