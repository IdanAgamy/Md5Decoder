package com.idan.md5Decoder.controler;

import com.idan.md5Decoder.enums.ErrorType;
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

    public void registerMinionServer(String minionUri) throws ApplicationException {
        if (minions.contains(minionUri)) {
            throw new ApplicationException("minion already registered", ErrorType.MINION_ALREADY_REGISTERED);
        }
        minions.add(minionUri);
        System.out.println("added " + minionUri + " to minions");
        updateMinionsSearchRange();
    }

    private void updateMinionsSearchRange() throws ApplicationException {
        synchronized (this.minions) {
            if (minions.isEmpty()) {
                throw new ApplicationException("no minion to do update", ErrorType.NO_MINIONS);
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

    private void updateMinionsSearchRange(String minionUri, int rangeStart, int rangeEnd) throws ApplicationException {
        RestTemplate rt = new RestTemplate();
        String uri = "http://" + minionUri + "/updateRange";
        int[] range = {rangeStart, rangeEnd};
        HttpEntity<int[]> request = new HttpEntity<>(range);
        try {
            ResponseEntity<int[]> returnReq = rt.postForEntity(uri, request, int[].class);
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new ApplicationException("Updating minion search range failed", e, ErrorType.HTTP_REQUEST_ERROR);
        }
    }

    public void decodeHash(String hashToDecode) throws ApplicationException {
        validateHash(hashToDecode);
        if (minions.isEmpty()) {
            throw new ApplicationException("no minion to do decoding, can't decode", ErrorType.NO_MINIONS);
        }
        for (String minion : minions) {
            sendDecodeRequestToMinion(minion, hashToDecode);
        }
        System.out.println("Hash :" + hashToDecode + "was sent to " + minions.size() + " minions");
    }

    private void sendDecodeRequestToMinion(String minionUri, String hashToDecode) throws ApplicationException {
        RestTemplate rt = new RestTemplate();
        String uri = "http://" + minionUri + "/decodeRequest";
        HttpEntity<String> request = new HttpEntity<>(hashToDecode);
        try {
            ResponseEntity<String> returnReq = rt.postForEntity(uri, request, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            throw new ApplicationException("Could not send hash to decode for minion: " + minionUri, e, ErrorType.HTTP_REQUEST_ERROR);
        }
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
            throw new ApplicationException("Wrong size of array string, invalid results", ErrorType.INCORRECT_VALIDATION);
        }
        String hash = decodingResults[0];
        String password = decodingResults[1];
        validateHash(hash);
        if (password.length() != 10) {
            throw new ApplicationException("The password is not 10 char long (" + password + ").", ErrorType.INCORRECT_VALIDATION);
        }
        if (!Pattern.compile("^[0-9]+$").matcher(password).matches()) {
            throw new ApplicationException("the password is not all number (" + password + ").", ErrorType.INCORRECT_VALIDATION);
        }
        if (!password.startsWith("05")) {
            throw new ApplicationException("the password is not in the correct format '05XXXXXXXX' (" + password + ").", ErrorType.INCORRECT_VALIDATION);
        }
    }

    private void removeHashToDecode(String hashToRemove) throws ApplicationException {
        if (minions.isEmpty()) {
            throw new ApplicationException("no minion to do remove", ErrorType.NO_MINIONS);
        }
        RestTemplate rt = new RestTemplate();
        for (String minionUri : minions) {
            String uri = "http://" + minionUri + "/removeHash";
            HttpEntity<String> request = new HttpEntity<>(hashToRemove);
            try {
                ResponseEntity<String> returnReq = rt.postForEntity(uri, request, String.class);
            } catch (RestClientException e) {
                e.printStackTrace();
                throw new ApplicationException("could not remove hash: " + hashToRemove + " for server: " + minionUri, e, ErrorType.HTTP_REQUEST_ERROR);
            }
        }
    }

    public void isAlive() throws ApplicationException {
        synchronized (this.minions) {
            boolean isAllAlive = true;
            if (minions.isEmpty()) {
                throw new ApplicationException("no minion to do monitor", ErrorType.NO_MINIONS);
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

    private void removeMinion(String minionUri) throws ApplicationException {
        this.minions.remove(minionUri);
        updateMinionsSearchRange();
    }

    private void validateHash(String hash) throws ApplicationException {
        if (hash.length() != 32) {
            throw new ApplicationException("The hash is not 32 char long or the password is not 10 char long " + hash + ").", ErrorType.INCORRECT_VALIDATION);
        }
        if (!Pattern.compile("^[0-9A-Fa-f]+$").matcher(hash).matches()) {
            throw new ApplicationException("the hash is not in hex " + hash + ").", ErrorType.INCORRECT_VALIDATION);
        }
    }
}
