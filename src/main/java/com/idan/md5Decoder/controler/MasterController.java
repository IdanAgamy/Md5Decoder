package com.idan.md5Decoder.controler;

import com.idan.md5Decoder.exceptions.ApplicationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

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
        ResponseEntity<int[]> returnReq = rt.postForEntity(uri, request, int[].class);
    }

    public void decodeHash(String hashToDecode) throws ApplicationException {
        validateHash(hashToDecode);
        if (minions.isEmpty()) {
            System.out.println("no minion to do decoding, can't decode");
            return;
        }
        for (String minion : minions) {
            sendDecodeRequestToMinion(minion, hashToDecode);
        }
        System.out.println("Hash :" + hashToDecode + "was sent to " + minions.size() + " minions");
    }

    private void validateHash(String hash) throws ApplicationException {
        if (hash.length() != 32) {
            throw new ApplicationException("The hash is not 32 char long or the password is not 10 char long");
        }
        if (!Pattern.compile("^[0-9A-F]+$").matcher(hash).matches()) {
            throw new ApplicationException("the hash is not in hex");
        }
    }

    private void sendDecodeRequestToMinion(String minionUri, String hashToDecode) {
        RestTemplate rt = new RestTemplate();
        String uri = "http://" + minionUri + "/decodeRequest";
        HttpEntity<String> request = new HttpEntity<>(hashToDecode);
        ResponseEntity<String> returnReq = rt.postForEntity(uri, request, String.class);
    }

    public void getResult(String[] decodingResults) throws ApplicationException {
        validatingHashDecodeResults(decodingResults);
        String decodedHash = decodingResults[0];
        String decodedPassword = decodingResults[1];
        System.out.println("password for hash " + decodedHash + " is " + decodedPassword);
        this.removeHashToDecode(decodedHash);
    }

    private void validatingHashDecodeResults(String[] decodingResults) throws ApplicationException {
        if (decodingResults.length != 2) {
            throw new ApplicationException("Wrong size of array string, invalid results");
        }
        String hash = decodingResults[0];
        String password = decodingResults[1];
        validateHash(hash);
        if (password.length() != 10) {
            throw new ApplicationException("The password is not 10 char long");
        }
        if (!Pattern.compile("^[0-9]+$").matcher(password).matches()) {
            throw new ApplicationException("the password is not all number");
        }
        if (password.startsWith("05")) {
            throw new ApplicationException("the password is not in the correct format '05XXXXXXXX'");
        }
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
            ResponseEntity<String> returnReq = rt.postForEntity(uri, request, String.class);
        }
    }

    public void isAlive() {
        synchronized (this.minions) {
            boolean isAllAlive = true;
            if (minions.isEmpty()) {
                System.out.println("no minion to do monitor");
                return;
            }
            RestTemplate rt = new RestTemplate();
            for (String minionUri : minions) {
                String uri = "http://" + minionUri + "/";
                try {
                    ResponseEntity<String> returnReq = rt.getForEntity(uri, String.class);
                    System.out.println("minion " + minionUri + " is alive");
                } catch (RestClientException e) {
                    System.out.println("minion " + minionUri + " is not responding, removing from list.");
                    removeMinion(minionUri);
                    isAllAlive = false;
                }
            }
            if (isAllAlive) {
                System.out.println("All minions responded");
                return;
            }
            System.out.println("Minions removed from list");
        }
    }

    private void removeMinion(String minionUri) {
        this.minions.remove(minionUri);
        updateMinionsSearchRange();
    }
}
