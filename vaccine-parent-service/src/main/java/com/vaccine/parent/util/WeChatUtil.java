package com.vaccine.parent.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序工具类
 */
@Slf4j
@Component
public class WeChatUtil {

    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String appSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 通过code获取openid和session_key
     */
    public Map<String, String> code2Session(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session" +
                "?appid=" + appId +
                "&secret=" + appSecret +
                "&js_code=" + code +
                "&grant_type=authorization_code";

        try {
            String response = restTemplate.getForObject(url, String.class);
            log.info("微信code2session响应: {}", response);

            JsonNode jsonNode = objectMapper.readTree(response);
            
            Map<String, String> result = new HashMap<>();
            
            if (jsonNode.has("errcode")) {
                // 有错误码
                result.put("error", jsonNode.get("errmsg").asText());
                return result;
            }
            
            result.put("openid", jsonNode.get("openid").asText());
            if (jsonNode.has("session_key")) {
                result.put("session_key", jsonNode.get("session_key").asText());
            }
            if (jsonNode.has("unionid")) {
                result.put("unionid", jsonNode.get("unionid").asText());
            }
            
            return result;
        } catch (Exception e) {
            log.error("调用微信code2session失败", e);
            Map<String, String> errorResult = new HashMap<>();
            errorResult.put("error", "微信API调用失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 解析手机号
     */
    public Map<String, String> getPhoneNumber(String code) {
        String accessToken = getAccessToken();
        
        if (accessToken.isEmpty()) {
            Map<String, String> errorResult = new HashMap<>();
            errorResult.put("error", "access_token为空");
            return errorResult;
        }

        String url = "https://api.weixin.qq.com/wxa/business/getuserphonenumber" +
                "?access_token=" + accessToken;

        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("code", code);
            
            String response = restTemplate.postForObject(url, requestBody, String.class);
            log.info("微信getPhoneNumber响应: {}", response);

            JsonNode jsonNode = objectMapper.readTree(response);
            
            Map<String, String> result = new HashMap<>();
            
            // 修复：errcode=0表示成功，errcode!=0才表示错误
            if (jsonNode.has("errcode")) {
                int errcode = jsonNode.get("errcode").asInt();
                if (errcode != 0) {
                    String errmsg = jsonNode.get("errmsg").asText();
                    result.put("error", errmsg);
                    return result;
                }
            }
            
            JsonNode phoneInfo = jsonNode.get("phone_info");
            
            if (phoneInfo == null) {
                result.put("error", "响应中没有phone_info节点");
                return result;
            }
            
            String phoneNumber = phoneInfo.get("phoneNumber").asText();
            String purePhoneNumber = phoneInfo.get("purePhoneNumber").asText();
            String countryCode = phoneInfo.get("countryCode").asText();
            
            result.put("phoneNumber", phoneNumber);
            result.put("purePhoneNumber", purePhoneNumber);
            result.put("countryCode", countryCode);
            
            return result;
        } catch (Exception e) {
            log.error("调用微信getPhoneNumber失败", e);
            Map<String, String> errorResult = new HashMap<>();
            errorResult.put("error", "微信API调用失败: " + e.getMessage());
            return errorResult;
        }
    }

    /**
     * 获取access_token
     */
    private String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token" +
                "?grant_type=client_credential" +
                "&appid=" + appId +
                "&secret=" + appSecret;

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);
            
            if (jsonNode.has("errcode")) {
                log.error("获取access_token失败: {}", jsonNode.get("errmsg").asText());
                return "";
            }
            
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("获取access_token失败", e);
            return "";
        }
    }
}