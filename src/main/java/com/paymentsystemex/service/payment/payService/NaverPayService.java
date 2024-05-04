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

    @Value("${naverPay.cancelTransactionUrl}")
    private String cancelTransactionUrl;

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.NAVER_PAY;
    }


    @Override
    public void requestTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception {

        HashMap<String, Object> result;
        try {
            result = doTransactionRequest(payKey, totalPayAmount);
        } catch (Exception e) {
            throw new IOException("Naver payment server call failed");
        }

        String code = (String) result.get("code");
        if (!code.equals("Success")) {
            throw new IOException(result.get("message").toString());
        }
    }

    @Override
    public void requestCancelTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception {

    }

    private HashMap<String, Object> doTransactionRequest(String payKey, int totalPayAmount) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", secretKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody = "paymentId=" + payKey;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
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
            doCancelRequest(payKey,totalPayAmount);
            body.put("code", "Fail");
            body.put("message", "결제금액이 상이합니다.");
            return body;
        }

        return body;

    }

    private HashMap<String, Object> doCancelRequest(String payKey, int cancelAmount) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", secretKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody = "paymentId=" + payKey + "&cancelAmount=" + cancelAmount;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<HashMap> response = restTemplate.exchange(transactionUrl, HttpMethod.POST, request, HashMap.class);

        if (!HttpStatus.OK.equals(response.getStatusCode())) {
            throw new RestClientException("");
        }

        HashMap<String, Object> body = response.getBody();

        return body;
    }


}
