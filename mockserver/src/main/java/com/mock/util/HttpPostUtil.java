package com.mock.util;


import com.mock.sign.RSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class HttpPostUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpPostUtil.class);
    public static void  Post(String Url,String key, Map map){

        RestTemplate restTemplate=new RestTemplate();
   //     String url="http://www.testXXX.com";
    /* 注意：必须 http、https……开头，不然报错，浏览器地址栏不加 http 之类不出错是因为浏览器自动帮你补全了 */
        Map<String, String> params = SignUtils.paraFilter2(map);
        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);

        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();
        try {
            String sign = URLEncoder.encode(RSA.signRSA256(preStr, key, "utf-8"), "UTF-8");
            String bodyValTemplate=preStr+"&sign="+sign;
            logger.info("Body:"+bodyValTemplate);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity entity = new HttpEntity(bodyValTemplate, headers);
            restTemplate.exchange(Url, HttpMethod.POST, entity, String.class);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }



    public void  Sentt(){
        RestTemplate restTemplate=new RestTemplate();
        String url="http://www.testXXX.com";
    /* 注意：必须 http、https……开头，不然报错，浏览器地址栏不加 http 之类不出错是因为浏览器自动帮你补全了 */
        String bodyValTemplate = "%E6%B5%8B%E8%AF%95%E7%9A%84%E5%8F%82%E6%95%B01&var2=test+val2";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity entity = new HttpEntity(bodyValTemplate, headers);
        restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
