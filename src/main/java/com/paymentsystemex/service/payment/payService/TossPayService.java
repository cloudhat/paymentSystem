package com.paymentsystemex.service.payment.payService;

import com.paymentsystemex.domain.payment.PaymentMethod;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class TossPayService implements PayService {

    @Value("${tossPay.secretKey}")
    private static String secretKey;

    @Value("${tossPay.transactionUrl}")
    private String transactionUrl;

    @Value("${tossPay.cancelTransactionUrl}")
    private String cancelTransactionUrl;

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.TOSS_PAY;
    }


    @Override
    public void requestTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception {

        JSONObject jsonObject;
        try {
            jsonObject = doTransactionRequest(paymentId, payKey, totalPayAmount);
        } catch (Exception e) {
            throw new IOException("Toss payment server call failed");
        }

        if (jsonObject.containsKey("failure")) {
            JSONObject failureObj = (JSONObject) jsonObject.get("failure");
            throw new IOException(failureObj.get("message").toString());
        }
    }

    @Override
    public void requestCancelTransaction(Long paymentId, String payKey, int totalPayAmount) throws Exception {
        JSONObject jsonObject;
        try {
            jsonObject = doCancelTransactionRequest(payKey, totalPayAmount, "단순 변심");
        } catch (Exception e) {
            throw new IOException("Toss payment server call failed");
        }

        if (jsonObject.containsKey("failure")) {
            JSONObject failureObj = (JSONObject) jsonObject.get("failure");
            throw new IOException(failureObj.get("message").toString());
        }
    }

    private JSONObject doTransactionRequest(Long paymentId, String payKey, int totalPayAmount) throws Exception {
        JSONParser parser = new JSONParser();

        String paymentKey = payKey;
        Long orderId = paymentId;
        int amount = totalPayAmount;

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = secretKey;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);
        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL(transactionUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

        return (JSONObject) parser.parse(reader);
    }

    private JSONObject doCancelTransactionRequest(String payKey, int cancelAmount, String cancelReason) throws Exception {
        JSONParser parser = new JSONParser();

        JSONObject obj = new JSONObject();

        obj.put("cancelAmount ", cancelAmount);
        obj.put("cancelReason", cancelReason);

        String cancelTransactionUrlWithPayKey = String.format(cancelTransactionUrl, payKey);

        // 토스페이먼츠 API는 시크릿 키를 사용자 ID로 사용하고, 비밀번호는 사용하지 않습니다.
        // 비밀번호가 없다는 것을 알리기 위해 시크릿 키 뒤에 콜론을 추가합니다.
        String widgetSecretKey = secretKey;
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);
        // 결제를 승인하면 결제수단에서 금액이 차감돼요.
        URL url = new URL(cancelTransactionUrlWithPayKey);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(obj.toString().getBytes("UTF-8"));

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        // 결제 성공 및 실패 비즈니스 로직을 구현하세요.
        Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);

        return (JSONObject) parser.parse(reader);
    }


}
