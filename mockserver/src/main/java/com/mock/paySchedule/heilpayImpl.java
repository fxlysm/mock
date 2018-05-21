package com.mock.paySchedule;

import com.mock.sign.ChinaCardPosUtil;
import com.mock.util.HttpPostUtil;
import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import com.mock.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class heilpayImpl implements heilpay{
    private static final Logger logger = LoggerFactory.getLogger(heilpayImpl.class);

    @Value("${test.pufa.cpid}")
    private String cpId;

    @Value("${test.cp.pivkey}")
    private String privateKey;

    @Value("${test.posturl}")
    private String posturl;

    @Value("${test.notifyUrl}")
    private String notifyUrl;

    @Override
    public void wechatscan(){

        String nonceStr=StringUtil.getRandomString(8);
        String cpOrderId=StringUtil.getStringDate("yyMMddHHmmss")+StringUtil.getCode(6,0);
        String totalFee=StringUtil.gettotal("100","50000");

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("serviceName","hhly.pay.weixin.native");
        respMap.put("cpId",cpId);
        respMap.put("nonceStr",nonceStr);
        respMap.put("cpOrderId",cpOrderId);
        respMap.put("totalFee",totalFee);
        respMap.put("notifyUrl",notifyUrl);
        respMap.put("body","test");
        respMap.put("mchCreateIp","192.168.1.1");

        HttpPostUtil.Post(posturl,privateKey,respMap);

//        // 过滤map
//        Map<String, String> params = SignUtils.paraFilter(respMap);
//
//        if (params.containsKey("sign"))
//            params.remove("sign");
//        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
//        SignUtils.buildPayParams(buf, params, false);
//        String signatureStr = buf.toString();
//        logger.info("预签名字串：" + signatureStr );
////        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
//        String signature = null;
//        try {
////            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
//            signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        logger.info("签名：" + signature );
//        respMap.put("signature",signature);
//        logger.info("请求URL地址：" + posturl );
//        logger.info("请求参数MAP：" + respMap );
//
//        Map<String, String> postparams = SignUtils.paraFilter(respMap);
//        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
//        SignUtils.buildPayParams(postbuf, postparams, false);
//        String postStr = postbuf.toString();
//        logger.info("请求参数x-www-form-urlencoded：" + postStr );
//
//        try {
//          String pstr= HttpUtil.httpRequest(posturl,"POST",postStr);
//         //   String pstr= HttpUtil.doPost(posturl , respMap);
//
//            logger.info("返回结果："+pstr);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
