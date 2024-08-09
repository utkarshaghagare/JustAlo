package com.JustAlo.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
public class MoOTOService {
    @Value("${sms.shop.sender.id}")
    private String senderId;

    @Value("${sms.shop.pe.id}")
    private String peId;

    @Value("${sms.shop.header.id}")
    private String headerId;

    @Value("${sms.shop.api.url}")
    private String apiUrl;

    @Value("${sms.shop.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public MoOTOService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String sendOtp(String phoneNumber, String otp) {
        String message = "Your OTP is: " + otp;
        String url = String.format("http://mysmsshop.in/V2/http-api.php?apikey=XXXXXXXXXXXXXXXX&senderid=XXXXXX&number=XXXXXXXXXXX,XXXXXXXXXXX,XXXXXXXXXXX&message=Welcome to Mysmsshop&format=json\n",
                apiUrl, apiKey, senderId, peId, headerId, phoneNumber, message);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response.getBody();
    }
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
