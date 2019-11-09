package com.idan.md5Decoder;

import com.idan.md5Decoder.threads.IsAliveTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Timer;

@SpringBootApplication
public class Md5DecoderApplication {

	@Autowired
	private IsAliveTask task;

	public static void main(String[] args) {
		SpringApplication.run(Md5DecoderApplication.class, args);
	}

	@PostConstruct
	public void startThread() {
		Timer timer = new Timer();
		timer.schedule(task, 10 * 1000, 10 * 1000);
	}

}
