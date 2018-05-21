package com.mock.pay.pf.impl;

import com.alibaba.fastjson.JSONObject;
import com.mock.config.SystemConf;
import com.mock.pay.pf.PFWechatJSService;
import com.mock.sign.ChinaCardPosUtil;

import com.mock.sign.KeyString;
import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class PFWechatJSServiceImpl implements PFWechatJSService{//微信公从号

    private static final Logger logger = LoggerFactory.getLogger(PFWechatAPPServiceImpl.class);
    String PFMOCK= SystemConf.PFMOCK_MSG;

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Value("${pufa.rsa.private_key}")
//    private String privateKey;
private String privateKey= KeyString.privateKey;

    @Value("${pf.wechat.js.rate}")
    private double rate;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    public void payResp( Map<String, String> map,PrintWriter out) throws IOException {
        String respCode="0000";
        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型
        String productId=map.get("productId");//产品类型
        String merNo=map.get("merNo");//商户号
        String subMchId=map.get("subMchId");//二级商户编号
        String orderDate=map.get("orderDate");//订单日期
        String orderNo=map.get("orderNo");//商户订单号
        String clientIp=map.get("clientIp");//商户IP
        String subOpenId=map.get("subOpenId");//微信OPENID
        String returnUrl=map.get("returnUrl");//页面通知地址
        String notifyUrl=map.get("notifyUrl");//异步通知地址
        String transAmt=map.get("transAmt");//交易金额
        String commodityName=map.get("commodityName");//商品名称

//        String limitPay=map.get("limitPay");//限制卡类型
//        String timeExpire=map.get("timeExpire");//二维码有效时间
//        String storeId=map.get("storeId");//门店编号
        //************** 以下信息为支付回调**************************



        if("0000".equals(respCode)){
            // 缓存对象 ---将订单信息写入
            Map<String, String> cacheMap = new HashMap<String, String>();
            cacheMap.put("requestNo",requestNo);
            cacheMap.put("version",version);
            cacheMap.put("transId",transId);
            cacheMap.put("productId",productId);
            cacheMap.put("clientIp",clientIp);
            cacheMap.put("merNo",merNo);
            cacheMap.put("subOpenId",subOpenId);
            cacheMap.put("subMchId",subMchId);
            cacheMap.put("orderDate",orderDate);
            cacheMap.put("orderNo",orderNo);
            cacheMap.put("returnUrl",returnUrl);
            cacheMap.put("notifyUrl",notifyUrl);
            cacheMap.put("transAmt",transAmt);
            cacheMap.put("commodityName",commodityName);
            cacheMap.put("respCode",respCode);
            String pftransId= StringUtil.getStringDate("yyMMddHHmmss")+StringUtil.getCode(8,0);
//            String sqlcommand="INSERT INTO pf_trans_logs (requestNo,orderNo,transId,pftransId,productId,merNo,subMchId,transAmt,notifyUrl,commodityName,orderDate)"+
//                    "VALUES('"+requestNo+"','"+orderNo+"','"+transId+"','"+pftransId+"','"+productId+"','"+merNo+"','"+subMchId+"','"+transAmt+"','"+notifyUrl+"','"+commodityName+"','"+orderDate+"')";
            int fee=(int) (Integer.valueOf(transAmt).intValue()*rate/1000);
            String sqlcommand="INSERT INTO pf_trans_logs (requestNo,orderNo,transId,pftransId,productId,merNo,subMchId,transAmt,fee_rate,fee,notifyUrl,commodityName,orderDate)"+
                    "VALUES('"+requestNo+"','"+orderNo+"','"+transId+"','"+pftransId+"','"+productId+"','"+merNo+"','"+subMchId+"','"+transAmt+"','"+rate+"','"+fee+"','"+notifyUrl+"','"+commodityName+"','"+orderDate+"')";

            logger.debug("插入订单数据至mysql:"+sqlcommand);

            jdbcTemplate.execute(sqlcommand);
            // set redisCache
            if (StringUtils.isNotBlank(orderNo)) {
                logger.debug("set rediscache...");
                String   tmpString = JSONObject.toJSONString(cacheMap);
//            redisCache.put(out_trade_no, tmpString);
                redisTemplate.opsForHash().put(PFMOCK,orderNo,tmpString);

            }
        }
        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("requestNo",requestNo);
        respMap.put("version",version);
        respMap.put("transId",transId);
        respMap.put("productId",productId);
        respMap.put("merNo",merNo);
        respMap.put("subMchId",subMchId);
        respMap.put("orderDate",orderDate);
        respMap.put("orderNo",orderNo);
        respMap.put("clientIp",clientIp);
        respMap.put("returnUrl",returnUrl);
        respMap.put("notifyUrl",notifyUrl);
        respMap.put("transAmt",transAmt);
        respMap.put("commodityName",commodityName);
        respMap.put("respCode",respCode);
        if("0000".equals(respCode)){
            respMap.put("respDesc","success");
            respMap.put("payInfo"," ");
            respMap.put("formfield","{\"prepayid\":\"WX1217752501201407033233368018\"}");//唤起微信APP支付的参数
        }else {
            respMap.put("respDesc","fail");
        }


        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);

        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParamsSign(buf, params, false);
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
//        resp.getWriter().write(String.valueOf(respMap));

        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );
        out.print(postStr);
    }
}
