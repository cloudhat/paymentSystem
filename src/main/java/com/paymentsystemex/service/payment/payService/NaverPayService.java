package com.paymentsystemex.service.payment.payService;

import com.paymentsystemex.domain.payment.PaymentMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;

@Service
public class NaverPayService implements PayService {


    @Value("${naverPay.clientId}")
    private static String clientId;

    @Value("${naverPay.secretKey}")
    private static String secretKey;

    @Value("${naverPay.transactionUrl}")
    private String transactionUrl;

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.NAVER_PAY;
    }


    @Override
    public void requestTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception {

        HashMap<String, Object> result;
        try {
            result = doRequest(paymentId, payKey, totalPayAmount);
        } catch (Exception e) {
            throw new IOException("Naver payment server call failed");
        }

        String code = (String) result.get("code");
        if (!code.equals("Success")) {
            throw new IOException(result.get("message").toString());
        }
    }

    private HashMap<String, Object> doRequest(Long paymentId, String payKey, int totalPayAmount) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", secretKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody = "paymentId=" + payKey;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        JSONParser parser = new JSONParser();
        ResponseEntity<HashMap> response = restTemplate.exchange(transactionUrl, HttpMethod.POST, request, HashMap.class);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RestClientException("");
        }

        HashMap<String, Object> body = response.getBody();

        String code = (String) body.get("code");
        if (!code.equals("Success")) {
            return body;
        }

        HashMap<String, Object> detail = (HashMap<String, Object>) body.get("detail");

        int responseTotalPayAmount = (int) detail.get("totalPayAmount");
        if (responseTotalPayAmount != totalPayAmount) {
            doCancel(payKey);
            body.put("code", "Fail");
            body.put("message", "결제금액이 상이합니다.");
            return body;
        }

        return body;

    }

    public JSONObject doCancel(String payKey) {
        return new JSONObject();
    }


}
