package com.idan.md5Decoder.api;

import com.idan.md5Decoder.beans.DecodeRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// test hash: 09f73b26119d26dada2376286278c19c => 0500000002
@RestController
public class MasterApi {

    @RequestMapping(method = RequestMethod.GET)
    public void startDecoding() throws InterruptedException {
        System.out.println("sending first");
        RestTemplate rt = new RestTemplate();
        String uri = "http://localhost:8989";
        DecodeRequest dr = new DecodeRequest(0, 10000000, "416234cf338a2303542d639bbfe7930e");
        HttpEntity<DecodeRequest> request = new HttpEntity<>(dr);
        ResponseEntity<DecodeRequest> returnReq = rt.postForEntity(uri, request, DecodeRequest.class);
        Thread.sleep(5000);
        System.out.println("sending second");
        DecodeRequest dr1 = new DecodeRequest(10, 10000000, "416234cf338a2303542d639bbfe7930e");
        request = new HttpEntity<>(dr1);
        returnReq = rt.postForEntity(uri, request, DecodeRequest.class);
    }
}
