package com.idan.md5Decoder.controler;

import com.idan.md5Decoder.beans.DecodeRequest;
import com.idan.md5Decoder.beans.DecodedHash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;

@Controller
public class MasterController {

    private final Set<String> minions;

    @Value("${master.rangeStart}")
    private int rangeStart;
    @Value("${master.rangeEnd}")
    private int rangeEnd;

    public MasterController() {
        this.minions = new HashSet<>();
    }

    public void registerMinionServer(String minionUri) {
        if (minions.contains(minionUri)) {
            System.out.println("minion already registered");
            return;
        }
        minions.add(minionUri);
        System.out.println("added " + minionUri + " to minions");
        updateMinionsSearchRange();
    }

    private void updateMinionsSearchRange() {
        synchronized (this.minions) {
            if (minions.isEmpty()) {
                System.out.println("no minion to do update");
                return;
            }
            int numOfMinions = minions.size();
            int searchRangeSize = (this.rangeEnd - this.rangeStart) / numOfMinions;
            int rangeStart = this.rangeStart;
            int modulus = (this.rangeEnd - this.rangeStart) % searchRangeSize;
            int rangeEnd = searchRangeSize - 1;
            for (String minion : minions) {
                if (modulus > 0) {
                    rangeEnd += 1;
                    modulus -= 1;
                }
                updateMinionsSearchRange(minion, rangeStart, rangeEnd);
                rangeStart = rangeEnd + 1;
                rangeEnd += searchRangeSize;
            }
        }
    }

    private void updateMinionsSearchRange(String minionUri, int rangeStart, int rangeEnd) {
        RestTemplate rt = new RestTemplate();
        String uri = "http://" + minionUri + "/updateRange";
        int[] range = {rangeStart, rangeEnd};
        HttpEntity<int[]> request = new HttpEntity<>(range);
        ResponseEntity<DecodeRequest> returnReq = rt.postForEntity(uri, request, DecodeRequest.class);
    }

    public void decodeHash(String hashToDecode) {
        if (minions.isEmpty()) {
            System.out.println("no minion to do decoding");
            return;
        }
        for (String minion : minions) {
            sendDecodeRequestToMinion(minion, 0, 1000000000, hashToDecode);
        }
    }

    private void sendDecodeRequestToMinion(String minionUri, int startNumber, int endNumber, String hashToDecode) {
        RestTemplate rt = new RestTemplate();
        String uri = "http://" + minionUri + "/decodeRequest";
        HttpEntity<String> request = new HttpEntity<>(hashToDecode);
        ResponseEntity<DecodeRequest> returnReq = rt.postForEntity(uri, request, DecodeRequest.class);
    }

    public void getResult(DecodedHash decodedHash) {
        System.out.println("password for hash " + decodedHash.getDecodedHash() + " is " + decodedHash.getDecodedPassword());
        this.removeHashToDecode(decodedHash.getDecodedHash());
    }

    private void removeHashToDecode(String hashToRemove) {
        if (minions.isEmpty()) {
            System.out.println("no minion to do decoding");
            return;
        }
        RestTemplate rt = new RestTemplate();
        for (String minionUri : minions) {
            String uri = "http://" + minionUri + "/removeHash";
            HttpEntity<String> request = new HttpEntity<>(hashToRemove);
            ResponseEntity<DecodeRequest> returnReq = rt.postForEntity(uri, request, DecodeRequest.class);
        }
    }

    public void isAlive() {
        synchronized (this.minions) {
            if (minions.isEmpty()) {
                System.out.println("no minion to do monitor");
                return;
            }
            RestTemplate rt = new RestTemplate();
            for (String minionUri : minions) {
                String uri = "http://" + minionUri + "/";
                try {
                    ResponseEntity<String> returnReq = rt.getForEntity(uri, String.class);
                } catch (RestClientException e) {
                    System.out.println("minion " + minionUri + " is not responding, removing from list.");
                    removeMinion(minionUri);
                }
            }
        }
    }

    private void removeMinion(String minionUri) {
        this.minions.remove(minionUri);
        updateMinionsSearchRange();
    }
}
