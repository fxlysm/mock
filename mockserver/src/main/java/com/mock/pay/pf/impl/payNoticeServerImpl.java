package com.mock.pay.pf.impl;

import com.mock.config.SystemConf;
import com.mock.pay.pf.payNoticeServer;
import com.mock.sign.ChinaCardPosUtil;

import com.mock.sign.KeyString;
import com.mock.util.*;
import com.mock.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class payNoticeServerImpl implements payNoticeServer {

    private static final Logger logger = LoggerFactory.getLogger(payNoticeServerImpl.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

//    @Value("${pufa.rsa.private_key}")
//    private String privateKey;
private String privateKey= KeyString.privateKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

       @Override
    public void  payNotify(Map<String, String> map) throws IOException, InterruptedException {

        String transId=map.get("transId");//交易类型
        String productId=map.get("productId");//产品类型
        String merNo=map.get("merNo");//商户号
        String subMchId=map.get("subMchId");//二级商户编号
        String orderDate=map.get("orderDate");//订单日期
        String orderNo=map.get("orderNo");//商户订单号
        String returnUrl=map.get("returnUrl");//页面通知地址
        String notifyUrl=map.get("notifyUrl");//异步通知地址
        String transAmt=map.get("transAmt");//交易金额
        String commodityName=map.get("commodityName");//商品名称

        int totalfee=Integer.valueOf(transAmt).intValue();
           String  transactionId= jdbcTemplate.queryForObject("Select pftransId from pf_trans_logs where orderNo=?",new Object[]{orderNo }, String.class);


        logger.info("查询到的transactionId:"+transactionId);

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("productId",productId);
        respMap.put("transId",transId);
        respMap.put("merNo",merNo);
        respMap.put("orderNo",orderNo);
        respMap.put("transAmt",transAmt);
        respMap.put("orderDate",orderDate);
        respMap.put("respCode","0000");
        respMap.put("respDesc","success");

        respMap.put("transactionId",transactionId);//第三方流水号
        respMap.put("timeEnd",StringUtil.getStringDate("yyyyMMddHHmmss"));//支付完成时间
        respMap.put("orderId",transactionId);//内部订单号
//        respMap.put("bankType",bankType);


        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);

        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String signatureStr = buf.toString();
        logger.info("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        String signature = null;
        try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
            signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("签名：" + signature );
        respMap.put("signature",signature);
        logger.info("回调通知地址：" + notifyUrl );
        logger.info("回调通知参数：" + respMap );

        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );

        try {
            String pstr= HttpUtil.doPost(notifyUrl , respMap);
            logger.info("返回结果："+pstr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void  RefundpayNotify(Map<String, String> map) throws IOException, InterruptedException {
           logger.info("Rofund Map:"+map);

        String transId=map.get("transId");//交易类型
        String productId=map.get("productId");//产品类型
        String merNo=map.get("merNo");//商户号
        String subMchId=map.get("subMchId");//二级商户编号
        String orderDate=map.get("orderDate");//订单日期
        String orderNo=map.get("orderNo");//商户订单号
        String returnUrl=map.get("returnUrl");//页面通知地址
        String notifyUrl=map.get("notifyUrl");//异步通知地址
        String transAmt=map.get("transAmt");//交易金额
        String commodityName=map.get("commodityName");//商品名称

//        String   transactionId= jdbcTemplate.queryForObject("Select pfrefundId from pf_trans_logs where orderNo=?",new Object[]{orderNo }, String.class);

//        logger.info("查询到的transactionId:"+transactionId);

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("productId",productId);
        respMap.put("transId",transId);
        respMap.put("merNo",merNo);
        respMap.put("orderNo",orderNo);
        respMap.put("transAmt",transAmt);
        respMap.put("orderDate",orderDate);
        respMap.put("respCode","0000");
        respMap.put("respDesc","success");

//        respMap.put("transactionId",transactionId);//第三方流水号
//        respMap.put("timeEnd",StringUtil.getStringDate("yyyyMMddHHmmss"));//支付完成时间
//        respMap.put("orderId",transactionId);//内部订单号
//        respMap.put("bankType",bankType);


        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);

        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String signatureStr = buf.toString();
        logger.info("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        String signature = null;
        try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
            signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("签名：" + signature );
        respMap.put("signature",signature);
        logger.info("回调通知地址：" + notifyUrl );
        logger.info("回调通知参数：" + respMap );

        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );

        try {
            String pstr= HttpUtil.doPost(notifyUrl , respMap);
            logger.info("返回结果："+pstr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
