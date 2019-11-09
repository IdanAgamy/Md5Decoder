package com.idan.md5Decoder.api;

import com.idan.md5Decoder.controler.MasterController;
import com.idan.md5Decoder.exceptions.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// test hash: 09f73b26119d26dada2376286278c19c => 0500000002
@RestController
public class MasterApi {

    @Autowired
    private MasterController controller;

    @RequestMapping(value = "/registerMinionServer", method = RequestMethod.POST)
    public void registerMinionServer(@RequestBody String minionUri) throws ApplicationException {
        this.controller.registerMinionServer(minionUri);
    }

    @RequestMapping(value = "/decodeHash", method = RequestMethod.POST)
    public void decodeHash(@RequestBody String hashToDecode) throws ApplicationException {
        this.controller.decodeHash(hashToDecode);
    }

    @RequestMapping(value = "/getResult", method = RequestMethod.POST)
    public void getResult(@RequestBody String[] decodedHash) throws ApplicationException {
        this.controller.getResult(decodedHash);
    }
}
