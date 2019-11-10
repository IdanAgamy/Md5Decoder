package com.idan.md5Decoder.threads;

import com.idan.md5Decoder.controler.MasterController;
import com.idan.md5Decoder.exceptions.ApplicationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

@Component
public class ReadFileTask extends TimerTask {

    private static final Logger logger = LogManager.getLogger(ReadFileTask.class);
    @Autowired
    ApplicationArguments appArgs;
    @Autowired
    MasterController controller;

    @Override
    public void run() {
        String[] args = appArgs.getSourceArgs();
        if (args.length != 1) {
            logger.error("wrong number of argument, only file path needed");
        }
        System.out.println(args[0]);
        List<String> hashesList = new ArrayList<>();
        try (BufferedReader br =
                     new BufferedReader(new FileReader(args[0]))) {
            String line;
            while ((line = br.readLine()) != null) {
                hashesList.add(line);
            }
            String[] hashes = hashesList.toArray(new String[0]);
            controller.decodeHash(hashes);
        } catch (IOException | ApplicationException e) {
            e.printStackTrace();
            logger.error("could not decode file", e);
        }
    }
}
