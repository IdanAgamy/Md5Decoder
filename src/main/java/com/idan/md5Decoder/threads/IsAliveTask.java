package com.idan.md5Decoder.threads;

import com.idan.md5Decoder.controler.MasterController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

@Component
public class IsAliveTask extends TimerTask {

    @Autowired
    private MasterController controller;

    @Override
    public void run() {
        System.out.println("Monitoring minions");
        controller.isAlive();
    }
}
