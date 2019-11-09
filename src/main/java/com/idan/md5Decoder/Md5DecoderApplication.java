package com.idan.md5Decoder;

import com.idan.md5Decoder.threads.IsAliveTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

@SpringBootApplication
public class Md5DecoderApplication {

	@Autowired
	private IsAliveTask task;

	public static void main(String[] args) throws NoSuchAlgorithmException {
		SpringApplication.run(Md5DecoderApplication.class, args);
//		Set<String> minions = new HashSet<>();
//		minions.add("a");
//		minions.add("b");
//		minions.add("asd");
//		minions.add("qwe");
//		minions.add("asf");
//		minions.add("qwae");
//		int rangestart = 0;
//		int rangeend = 100000000;
//		int numOfMinions = minions.size();
//		int searchRangeSize = ( rangeend -  rangestart) / numOfMinions;
//		int modulus = (rangeend -  rangestart) % searchRangeSize;
//		int rangeStart = rangestart;
//		int rangeEnd = searchRangeSize - 1;
//		for(String minion:minions){
//			if (modulus > 0){
//				rangeEnd += 1;
//				modulus -= 1;
//			}
//			System.out.println( rangeStart+"-"+ rangeEnd);
//			rangeStart = rangeEnd + 1;
//			rangeEnd += searchRangeSize;
//		}

	}

	@PostConstruct
	public void startThread() {
		Timer timer = new Timer();
		timer.schedule(task, 1000, 10 * 1000);
	}

}
