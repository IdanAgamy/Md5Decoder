package com.idan.md5Decoder.threads;

import com.idan.md5Decoder.controler.MasterController;
import com.idan.md5Decoder.enums.ErrorType;
import com.idan.md5Decoder.exceptions.ApplicationException;
import com.idan.md5Decoder.exceptions.ExceptionsHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

@Component
public class IsAliveTask extends TimerTask {

    private static final Logger logger = LogManager.getLogger(ExceptionsHandler.class);

    @Autowired
    private MasterController controller;

    @Override
    public void run() {
        logger.debug("Monitoring minions");
        try {
            controller.isAlive();
        } catch (ApplicationException e) {
            if (e.getErrorType() != ErrorType.NO_MINIONS) {
                e.printStackTrace();
                logger.error("could not finish current monitoring: " + e.getMessage(), e);
            } else {
                logger.debug("no minion to monitor");
            }
        }
    }
}
