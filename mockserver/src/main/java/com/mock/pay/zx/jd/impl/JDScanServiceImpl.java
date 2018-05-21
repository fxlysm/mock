package com.mock.pay.zx.jd.impl;

import com.alibaba.fastjson.JSONObject;
import com.mock.config.SystemConf;
import com.mock.mysql.ZXGetValue;


import com.mock.pay.zx.jd.JDScanService;
import com.mock.sign.MD5;
import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import com.mock.util.XmlUtils;
import com.mock.util.httpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class JDScanServiceImpl implements JDScanService {
    private static final Logger logger = LoggerFactory.getLogger(JDScanServiceImpl.class);
    String ZXMOCK= SystemConf.ZXMOCK_MSG;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void payResp(HttpServletResponse resp, Map<String, String> map) throws IOException {
        String Signkey="";
        Map<String, String> dd = ZXGetValue.GetValue_Req(map.get("service"));
        logger.debug("*************************** 从数据库mysql中获取到的pay.alipay.native参数**************************");
        logger.debug(String.valueOf(dd));
        System.out.println(dd);
        logger.debug("\n\n");

        String status = dd.get("status");
        String result_code = dd.get("result_code");
        if("1".equals(dd.get("key_iswork"))){
            Signkey=ZXGetValue.getKeyWithmichId(map.get("mch_id"));
        }else {
            Signkey=dd.get("signkey");
        }
//		String Signkey=ScanPayGetValue.getKeyWithmichId(map.get("mch_id"));

        String notify_url = map.get("notify_url");
        String out_trade_no = map.get("out_trade_no");
        String total_fee = map.get("total_fee");
        String mch_id = map.get("mch_id");
        String mch_create_ip = map.get("mch_create_ip");
        String service = map.get("service");
        // 缓存对象
        Map<String, String> cacheMap = new HashMap<String, String>();
        cacheMap.put("out_trade_no", out_trade_no);
        cacheMap.put("notify_url", notify_url);
        cacheMap.put("total_fee", total_fee);
        cacheMap.put("mch_id", mch_id);
        cacheMap.put("mch_create_ip", mch_create_ip);
        cacheMap.put("body", map.get("body"));
        cacheMap.put("service", service);

        cacheMap.put("pay_status", status);
        cacheMap.put("pay_result_code", result_code);
        // set redisCache
        if (StringUtils.isNotBlank(out_trade_no)) {
            logger.debug("set rediscache...");
            String tmpString = JSONObject.toJSONString(cacheMap);
            redisTemplate.opsForHash().put(ZXMOCK,out_trade_no,tmpString);
        }
        logger.debug("set rediscache. map:" + cacheMap);

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("version", dd.get("version")); // is not null 版本号
        respMap.put("charset", dd.get("charset")); // is not null 字符集
        respMap.put("sign_type",dd.get("sign_type")); // 签名方式
        respMap.put("status", status); // is not null 返回状态码
        // respMap.put("message", ""); // 返回信息,如非空，为错误原因签名失败参数格式校验错误

        if ("0".equals(status)) {
            // 以下字段在 status 为 0的时候有返回
            respMap.put("result_code", result_code); // 业务结果 0表示成功非0表示失败
            if("1".equals(dd.get("mch_id_iswork"))){
                respMap.put("mch_id", map.get("mch_id")); // 商户号，由威富通分配----从网络接收
            }else {
                respMap.put("mch_id", dd.get("mch_id")); // 商户号，由威富通分配----从数据库中读取
            }
            if("1".equals(dd.get("device_info_iswork"))){
                respMap.put("device_info", map.get("device_info")); // 设备号 威富通支付分配的终端设备号
            }else {
                respMap.put("device_info", dd.get("device_info")); // 商户号，由威富通分配----从数据库中读取
            }
            if("1".equals(dd.get("nonce_str_iswork"))){
                respMap.put("nonce_str", StringUtil.getRandomString(8)); // 随机字符串
            }else {
                respMap.put("nonce_str", dd.get("nonce_str")); //  随机字符串----从数据库中读取
            }

            // respMap.put("err_code", "");
//			respMap.put("err_msg", notify_url);
            // respMap.put("sign", "");

            if ("0".equals(result_code)) {
                // 以下字段在 status 和 result_code 都为 0的时候有返回
                respMap.put("code_url", "http://192.168.74.163:2225/qrcode?uuid="+map.get(service)+StringUtil.getRandomString(8)); // 二维码链接
                respMap.put("code_img_url", "http://192.168.74.163:2225/qrcode?uuid="+map.get(service)+StringUtil.getRandomString(8)); // 二维码图片

            }
        }

        // 货币种类
        // respMap.put("fee_type", "CNY");

        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);
        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();

        String sign = MD5.sign(preStr, "&key=" + Signkey, "utf-8");
        logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
        logger.debug("sign:" + sign);

        respMap.put("sign", sign);

        resp.setHeader("Content-type", "text/xml;charset=utf-8");
        String res = XmlUtils.toXml(respMap);
        resp.getWriter().write(res);
    }

    @Override
    public  void payNotify(Map<String, String> map) throws IOException, InterruptedException {
        String Signkey="";
        logger.debug("payNotify map params:" + map);

        Map<String, String> dd = ZXGetValue.GetValue_Notice(map.get("service"));
        logger.debug(
                "*************************** 从数据库mysql中获取到的" + map.get("service") + "参数**************************");
        logger.debug(String.valueOf(dd));
        System.out.println(dd);
        logger.debug("\n\n");
        String payNotify_status = dd.get("status");
        String payNotify_result_code = dd.get("result_code");
        String pay_result = dd.get("pay_result");
        String mch_id=map.get("mch_id");
//		String Signkey=ScanPayGetValue.getKeyWithmichId(mch_id);
        if("1".equals(dd.get("key_iswork"))){
            Signkey= ZXGetValue.getKeyWithmichId(map.get("mch_id"));
        }else {
            Signkey=dd.get("signkey");
        }
        String notifyUrl = map.get("notify_url");

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("version", dd.get("version")); // is not null 版本号
        respMap.put("charset", dd.get("charset")); // is not null 字符集
        respMap.put("sign_type", dd.get("sign_type")); // 签名方式
        respMap.put("status", payNotify_status); // is not null 返回状态码
        // respMap.put("message", ""); // 返回信息,如非空，为错误原因签名失败参数格式校验错误

        if ("0".equals(payNotify_status)) {
            // 以下字段在 status 为 0的时候有返回
            respMap.put("result_code", payNotify_result_code); // 业务结果
//			respMap.put("mch_id", mch_id); 												// 0表示成功非0表示失败
            if("1".equals(dd.get("mch_id_iswork"))){
                respMap.put("mch_id", mch_id); // 商户号，由威富通分配
            }else {
                respMap.put("mch_id", dd.get("mch_id"));
            }
            if("1".equals(dd.get("device_info_iswork"))){
                respMap.put("device_info", map.get("device_info")); // 设备号 威富通支付分配的终端设备号
            }else {
                respMap.put("device_info", dd.get("device_info"));
            }
            if("1".equals(dd.get("nonce_str_iswork"))){
                respMap.put("nonce_str", StringUtil.getRandomString(8));
            }else {
                respMap.put("nonce_str", dd.get("nonce_str"));
            }

            // respMap.put("err_code", "");
            // respMap.put("err_msg", "");
            // respMap.put("sign", "");
            if ("0".equals(payNotify_result_code)) {
                // 以下字段在 status 和 result_code 都为 0的时候有返回
                respMap.put("openid", "jd_4511esf1e51"); // 交易类型
                respMap.put("trade_type", map.get("service")); // 交易类型
                respMap.put("pay_result", pay_result); // 支付结果：0—成功；其它—失败
                // respMap.put("pay_info", ""); // 支付结果信息，支付成功时为空

//				respMap.put("out_transaction_id", "0"); // 对应支付宝交易记录账单详情中的交易号
//
                if("1".equals(dd.get("transaction_id_iswork"))){
                    respMap.put("transaction_id", StringUtil.getStringDate("yyyyMMddhhmmss")+StringUtil.getCode(6, 0)); // 对应支付宝交易记录账单详情中的商户订单号
                }else {
                    respMap.put("transaction_id", dd.get("transaction_id"));
                }

                if("1".equals(dd.get("out_trade_no_iswork"))){
                    respMap.put("out_trade_no", map.get("out_trade_no")); // 商户系统内部的定单号，32个字符内、可包含字母
                }else {
                    respMap.put("out_trade_no", dd.get("out_trade_no"));
                }

                if("1".equals(dd.get("total_fee_iswork"))){
                    respMap.put("total_fee", map.get("total_fee")); // 总金额，以分为单位，不允许包含任何字、符号
                }else {
                    respMap.put("total_fee", dd.get("total_fee"));
                }
                respMap.put("fee_type", dd.get("fee_type"));

                respMap.put("time_end", StringUtil.getStringDate("yyyyMMddhhmmss")); // 支付完成时间
                // 订单超时时间
            }
        }

        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);
        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();

        String sign = MD5.sign(preStr, "&key=" + Signkey, "utf-8");
        // String sign = MD5.sign(preStr,
        // "&key=527508019920daf31cf31dd3e2c19232", "utf-8");
        logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
        logger.debug("sign:" + sign);

        respMap.put("sign", sign);
        String respXML = XmlUtils.toXml(respMap);

        logger.debug("httpPostRequestXML:" + notifyUrl + "\n" + respXML);

        String respString = httpUtil.httpPostRequestXML(notifyUrl, respXML);

        logger.debug("支付结果通知响应：" + respString);
    }
}
