package com.tcm.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 微信小程序工具类
 */
@Component
public class WechatUtil {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    private static final String CODE_TO_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";

    private static final String GET_QRCODE_URL = "https://api.weixin.qq.com/wxa/getwxacodeunlimit";

    private static final String SEND_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据code获取session信息
     */
    public JsonNode getSessionInfo(String code) throws Exception {
        String urlStr = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                CODE_TO_SESSION_URL, appid, secret, code);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return objectMapper.readTree(content.toString());
    }

    /**
     * 获取access token
     */
    public JsonNode getAccessToken() throws Exception {
        String urlStr = String.format("%s?grant_type=client_credential&appid=%s&secret=%s",
                ACCESS_TOKEN_URL, appid, secret);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return objectMapper.readTree(content.toString());
    }

    /**
     * 获取小程序码
     */
    public byte[] getQrCode(String scene, String page, String accessToken) throws Exception {
        String urlStr = String.format("%s?access_token=%s", GET_QRCODE_URL, accessToken);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String body = String.format("{\"scene\":\"%s\",\"page\":\"%s\",\"need_qr_code\":true}", scene, page);

        conn.getOutputStream().write(body.getBytes("UTF-8"));

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream().readAllBytes();
        } else {
            throw new RuntimeException("请求失败: " + responseCode);
        }
    }

    /**
     * 发送订阅消息
     */
    public JsonNode sendMessage(String touser, String templateId, Object data, String accessToken) throws Exception {
        String urlStr = String.format("%s?access_token=%s", SEND_MESSAGE_URL, accessToken);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String body = String.format("{\"touser\":\"%s\",\"template_id\":\"%s\",\"data\":%s}",
                touser, templateId, objectMapper.writeValueAsString(data));

        conn.getOutputStream().write(body.getBytes("UTF-8"));

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        conn.disconnect();

        return objectMapper.readTree(content.toString());
    }
}