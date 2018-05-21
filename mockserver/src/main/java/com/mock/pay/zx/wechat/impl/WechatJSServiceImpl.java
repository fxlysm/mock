package com.mock.pay.zx.wechat.impl;

import com.alibaba.fastjson.JSONObject;
import com.mock.config.SystemConf;
import com.mock.mysql.ZXGetValue;
import com.mock.pay.zx.wechat.WechatJSService;
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
public class WechatJSServiceImpl implements WechatJSService {
    private static final Logger logger = LoggerFactory.getLogger(WechatJSServiceImpl.class);
    String ZXMOCK= SystemConf.ZXMOCK_MSG;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void payResp(HttpServletResponse resp, Map<String, String> map) throws IOException {
        String Signkey = "";
        Map<String, String> dd = ZXGetValue.GetValue_Req(map.get("service"));
        logger.debug("*************************** 从数据库mysql中获取到的pay.alipay.native参数**************************");
        logger.debug(String.valueOf(dd));
        System.out.println(dd);
        logger.debug("\n\n");

        String status = dd.get("status");
        String result_code = dd.get("result_code");
        // String Signkey=dd.get("signkey");

        if ("1".equals(dd.get("key_iswork"))) {
            Signkey = ZXGetValue.getKeyWithmichId(map.get("mch_id"));
        } else {
            Signkey = dd.get("signkey");
        }

        String notify_url = map.get("notify_url");
        String out_trade_no = map.get("out_trade_no");
        String is_raw = map.get("is_raw");
        String is_minipg = map.get("is_minipg");

        logger.debug("out_trade_no:" + out_trade_no);

        String time_start = StringUtil.getStringDate("yyyyMMddHHmmssSSS");
        // 缓存对象
        Map<String, String> cacheMap = new HashMap<String, String>();

        cacheMap.put("out_trade_no", out_trade_no);

        if ("1".equals(is_raw)) {
            logger.debug("接收到的是【原生态】下的API");
        } else {
            logger.debug("接收到的是【非原生态】下的API");
        }
        cacheMap.put("is_raw", is_raw);

        if ("1".equals(is_minipg)) {
            logger.debug("接收到的是【小程序】支付");
        } else {
            logger.debug("接收到的是【非小程序】支付");
        }
        cacheMap.put("is_minipg", is_minipg);
        cacheMap.put("service", map.get("service"));
        cacheMap.put("mch_id", map.get("mch_id"));
        cacheMap.put("device_info", map.get("device_info"));
        cacheMap.put("sub_appid", map.get("sub_appid"));
        cacheMap.put("attach", map.get("attach"));
        cacheMap.put("total_fee", map.get("total_fee"));
        cacheMap.put("mch_create_ip", map.get("mch_create_ip"));
        cacheMap.put("notify_url", map.get("notify_url"));
        cacheMap.put("callback_url", map.get("callback_url"));
        cacheMap.put("time_start", time_start);
        cacheMap.put("goods_tag", map.get("goods_tag"));
        cacheMap.put("nonce_str", map.get("nonce_str"));
        cacheMap.put("limit_credit_pay", map.get("limit_credit_pay"));
        cacheMap.put("pay_status", status);
        cacheMap.put("pay_result_code", result_code);
        // set redisCache
        if (StringUtils.isNotBlank(out_trade_no)) {
            logger.debug("set rediscache...");
            String tmpString = JSONObject.toJSONString(cacheMap);
            redisTemplate.opsForHash().put(ZXMOCK,out_trade_no,tmpString);
        }
        logger.debug("set rediscache. map:" + cacheMap);

        // 参数返回
        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("appid", map.get("sub_appid"));
        respMap.put("version", "2.0");
        respMap.put("charset", "UTF-8");
        respMap.put("sign_type", "MD5");
        respMap.put("status", status);
        if (status.equals("0")) {
            respMap.put("result_code", result_code);

            // respMap.put("mch_id", map.get("mch_id"));
            // respMap.put("device_info",map.get("device_info"));
            // respMap.put("nonce_str", map.get("nonce_str"));
            if ("1".equals(dd.get("mch_id_iswork"))) {
                respMap.put("mch_id", map.get("mch_id")); // 商户号，由威富通分配----从网络接收
            } else {
                respMap.put("mch_id", dd.get("mch_id")); // 商户号，由威富通分配----从数据库中读取
            }
            if ("1".equals(dd.get("device_info_iswork"))) {
                respMap.put("device_info", map.get("device_info")); // 设备号
                // 威富通支付分配的终端设备号
            } else {
                respMap.put("device_info", dd.get("device_info")); // 商户号，由威富通分配----从数据库中读取
            }
            if ("1".equals(dd.get("nonce_str_iswork"))) {
                respMap.put("nonce_str", StringUtil.getRandomString(8)); // 随机字符串
            } else {
                respMap.put("nonce_str", dd.get("nonce_str")); // 随机字符串----从数据库中读取
            }
            respMap.put("err_code", map.get("err_code"));
            respMap.put("err_msg", map.get("err_msg"));
            if ("0".equals(result_code)) {
                respMap.put("token_id", StringUtil.getCode(8, 4)); // 6位 大小字母与数字的随机组合
                // 动态口令
                respMap.put("pay_info", "{\"appId\":\" " + map.get("sub_appid") + "\",\"nonceStr\":\""
                        + map.get("nonce_str")
                        + "\",\"package\":\"prepay_id=wx201707061510112bbb8b3e580545725040\",\"paySign\":\"90B782B8BF2E6DD2634F46DCF22F06F2\",\"signType\":\"MD5\",\"timeStamp\":\"1499325011210\"}");
            }
        }

        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);

        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();

        // logger.debug("\n\n*********************设置M5签名为gbk*****************************");
        String sign = MD5.sign(preStr, "&key=" + Signkey, "utf-8");// gbk 验理证
        // //默认要保证一致utf-8
        logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
        logger.debug("sign:" + sign);

        respMap.put("sign", sign); // MD5签名结果

        resp.setHeader("Content-type", "text/xml;charset=utf-8");
        String res = XmlUtils.toXml(respMap);
        resp.getWriter().write(res);

        logger.debug("\n\n\n");
    }

    @Override
    public  void payNotify(Map<String, String> map) throws IOException, InterruptedException {
        Map<String, String> dd = ZXGetValue.GetValue_Notice(map.get("service"));
        logger.debug("*******Read config from **.l59 mysql*******");
        logger.debug(String.valueOf(dd));
        System.out.println(dd);
        logger.debug("\n\n");
        // String Signkey=dd.get("signkey");
        String Signkey = ZXGetValue.getKeyWithmichId(map.get("mch_id"));
        String status = dd.get("status");
        String result_code = dd.get("result_code");
        String pay_result = dd.get("pay_result");
        logger.debug("Mchid:" + map.get("mch_id") + "\n");
        logger.debug("Signkey:" + Signkey + "\n");
        logger.debug("payNotify map params:" + map);
        String notifyUrl = map.get("notify_url");
        String mch_id = map.get("mch_id");

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("version", dd.get("version")); // is not null 版本号
        respMap.put("charset", dd.get("charset")); // is not null 字符集
        respMap.put("sign_type", dd.get("sign_type")); // 签名方式
        respMap.put("status", status);
        // respMap.put("message", "message");//返回信息，如非空，为错误原因签名失败参数格式校验错误
        if ("0".equals(status)) {
            respMap.put("result_code", result_code);

            // respMap.put("mch_id", map.get("mch_id"));
            // respMap.put("device_info", map.get("device_info"));
            // respMap.put("nonce_str", map.get("nonce_str"));
            if ("1".equals(dd.get("mch_id_iswork"))) {
                respMap.put("mch_id", mch_id); // 商户号，由威富通分配
            } else {
                respMap.put("mch_id", dd.get("mch_id"));
            }
            if ("1".equals(dd.get("device_info_iswork"))) {
                respMap.put("device_info", StringUtil.getRandomString(8)); // 设备号
                // 威富通支付分配的终端设备号
            } else {
                respMap.put("device_info", dd.get("device_info"));
            }
            if ("1".equals(dd.get("nonce_str_iswork"))) {
                respMap.put("nonce_str", StringUtil.getRandomString(8));
            } else {
                respMap.put("nonce_str", dd.get("nonce_str"));
            }
            // respMap.put("err_code", map.get("err_code"));
            // respMap.put("err_msg", map.get("err_msg"));

            if ("0".equals(result_code)) {
                respMap.put("openid", map.get("openid"));
                respMap.put("trade_type", "pay.weixin.jspay");
                respMap.put("is_subscribe", "Y");// 用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效
                respMap.put("pay_result", pay_result);// 支付结果：0—成功；其它—失败
                respMap.put("pay_info", "");// 支付结果信息，支付成功时为空

                // respMap.put("transaction_id",
                // map.get("transaction_id"));//平台交易号
                if ("1".equals(dd.get("transaction_id_iswork"))) {
                    respMap.put("transaction_id", StringUtil.getStringDate("yyyyMMddhhmmss") + StringUtil.getCode(6, 0)); // 对应支付宝交易记录账单详情中的商户订单号
                } else {
                    respMap.put("transaction_id", dd.get("transaction_id"));
                }

                respMap.put("out_transaction_id", map.get("out_transaction_id"));// 第三方订单号

                respMap.put("sub_is_subscribe", "Y");// 用户是否关注子公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效

                respMap.put("sub_appid", map.get("sub_appid"));

                // respMap.put("out_trade_no", map.get("out_trade_no"));
                // respMap.put("total_fee", map.get("total_fee"));
                if ("1".equals(dd.get("out_trade_no_iswork"))) {
                    respMap.put("out_trade_no", map.get("out_trade_no")); // 商户系统内部的定单号，32个字符内、可包含字母
                } else {
                    respMap.put("out_trade_no", dd.get("out_trade_no"));
                }

                if ("1".equals(dd.get("total_fee_iswork"))) {
                    respMap.put("total_fee", map.get("total_fee")); // 总金额，以分为单位，不允许包含任何字、符号
                } else {
                    respMap.put("total_fee", dd.get("total_fee"));
                }

                respMap.put("coupon_fee", map.get("coupon_fee"));
                respMap.put("fee_type", dd.get("fee_type"));
                respMap.put("attach", map.get("attach"));
                respMap.put("bank_type", map.get("bank_type"));
                respMap.put("bank_billno", map.get("bank_billno"));
                respMap.put("time_end", StringUtil.getStringDate("yyyyMMddHHmmssSSS"));

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
