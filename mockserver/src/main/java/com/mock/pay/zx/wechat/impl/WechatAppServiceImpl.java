package com.mock.pay.zx.wechat.impl;

import com.alibaba.fastjson.JSONObject;
import com.mock.config.SystemConf;
import com.mock.mysql.ZXGetValue;
import com.mock.pay.zx.wechat.WechatAppService;
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
public class WechatAppServiceImpl implements WechatAppService {
    private static final Logger logger = LoggerFactory.getLogger(WechatAppServiceImpl.class);
    String ZXMOCK= SystemConf.ZXMOCK_MSG;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void payResp(HttpServletResponse resp, Map<String, String> map) throws IOException {
        String Signkey = "";
        // String status="0";
        // String result_code ="0";
        Map<String, String> dd = ZXGetValue.GetValue_Req(map.get("service"));
        logger.debug("*************************** 从数据库mysql中获取到的pay.alipay.native参数**************************");
        logger.debug(String.valueOf(dd));
        System.out.println(dd);
        logger.debug("\n\n");

        String status = dd.get("status");
        String result_code = dd.get("result_code");

        if ("1".equals(dd.get("key_iswork"))) {
            Signkey = ZXGetValue.getKeyWithmichId(map.get("mch_id"));
        } else {
            Signkey = dd.get("signkey");
        }
        // String Signkey=ScanPayGetValue.getKeyWithmichId(map.get("mch_id"));
        logger.debug("Mchid:" + map.get("mch_id") + "\n");
        logger.debug("Signkey:" + Signkey + "\n");
        String notify_url = map.get("notify_url");
        String out_trade_no = map.get("out_trade_no");
        logger.debug("out_trade_no:" + out_trade_no);
        String total_fee = map.get("total_fee");
        String mch_id = map.get("mch_id");
        String nonce_str = map.get("nonce_str");
        String service = map.get("service");
        String device_info = map.get("device_info");
        String transaction_id = StringUtil.getStringDate("yyyyMMddHHmmssSSS");
        // 缓存对象
        Map<String, String> cacheMap = new HashMap<String, String>();
        cacheMap.put("out_trade_no", out_trade_no);
        cacheMap.put("notify_url", notify_url);
        cacheMap.put("total_fee", total_fee);
        cacheMap.put("mch_id", mch_id);
        cacheMap.put("appid", map.get("appid"));
        cacheMap.put("nonce_str", nonce_str);
        cacheMap.put("service", service);
        cacheMap.put("device_info", device_info);
        cacheMap.put("transaction_id", transaction_id);
        cacheMap.put("sub_appid", map.get("sub_appid"));
        cacheMap.put("goods_tag", map.get("goods_tag"));
        cacheMap.put("limit_credit_pay", map.get("limit_credit_pay"));
        cacheMap.put("body", map.get("body"));
        cacheMap.put("groupno", map.get("groupno"));
        logger.debug(map.get("groupno"));

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
        // 接口-支付结果查询-参数检验

        respMap.put("version", dd.get("version")); // is not null 版本号 "2.0"
        respMap.put("charset", dd.get("charset")); // 可选值 UTF-8 ，默认为 UTF-8
        respMap.put("sign_type", dd.get("sign_type")); // 签名方式
        respMap.put("status", status); // is not null 返回状态码
        // respMap.put("message", "message");//返回信息，如非空，为错误原因签名失败参数格式校验错误

        if ("unified.trade.pay".equals(service)) { // 非原生态预下单 返回
            logger.debug("\n\n\n");
            logger.debug("****************【非原生态】预下单响应 返回*************************");
            // 以下字段在 status 为 0的时候有返回
            if ("0".equals(status)) {
                respMap.put("services", "unified.trade.pay");// 支持的支付类型，多个以“|”连接

                respMap.put("token_id", Signkey);// 支付授权码


            }

        } else if ("pay.weixin.raw.app".equals(service)) {// 原生态预下单 返回
            logger.debug("\n\n\n");
            logger.debug("****************【原生态】预下单 响应 返回*************************");
            if ("0".equals(status)) {// status 为 0的时候有返回
                respMap.put("result_code", result_code); // 0表示成功非0表示失败
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
                // respMap.put("err_code", "err_code");
//				respMap.put("err_msg", notify_url);

                if ("0".equals(result_code)) {// 以下字段在 status 和result_code 都为
                    // 0的时候有返回
                    // respMap.put("pay_info", "");//Json格式的字符串，微信官方SDK中需要的参数信息
                    respMap.put("out_trade_no", out_trade_no); // 商户订单号
                    respMap.put("transaction_id", transaction_id); // 平台订单号
                }
                // status 为 0的时候有返回sign

            }

        } // 原生态预下单 -----END

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
        logger.debug("payNotify map params:" + map);
        String Signkey = "";
        String notifyUrl = map.get("notify_url");
        // logger.debug("notify_url:" + notifyUrl);
        // String Signkey=ScanPayGetValue.getKeyWithmichId(map.get("mch_id"));
        Map<String, String> dd = ZXGetValue.GetValue_Notice(map.get("service"));
        logger.debug("*************************** 从数据库mysql中获取到的pay.alipay.native参数**************************");
        logger.debug(String.valueOf(dd));
        System.out.println(dd);
        logger.debug("\n\n");
        String payNotify_status = dd.get("status");
        String payNotify_result_code = dd.get("result_code");
        String pay_result = dd.get("pay_result");
        logger.debug("Mchid:" + map.get("mch_id") + "\n");
        logger.debug("Signkey:" + Signkey + "\n");

        if ("1".equals(dd.get("key_iswork"))) {
            Signkey = ZXGetValue.getKeyWithmichId(map.get("mch_id"));
        } else {
            Signkey = dd.get("signkey");
        }

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("version", dd.get("version")); // is not null 版本号 {非必传}
        respMap.put("charset", dd.get("charset")); // is not null 字符集 {非必传}
        respMap.put("sign_type", dd.get("sign_type")); // 签名方式 {非必传}
        respMap.put("status", payNotify_status); // is not null 返回状态码 {必传}
        // respMap.put("message", ""); // 返回信息,如非空，为错误原因签名失败参数格式校验错误
        if ("0".equals(payNotify_status)) {
            respMap.put("result_code", payNotify_result_code); // 0表示成功非0表示失败

            if ("1".equals(dd.get("mch_id_iswork"))) {
                respMap.put("mch_id", map.get("mch_id")); // 商户号，由威富通分配
            } else {
                respMap.put("mch_id", dd.get("mch_id"));
            }
            if ("1".equals(dd.get("device_info_iswork"))) {
                respMap.put("device_info", map.get("device_info")); // 设备号
                // 威富通支付分配的终端设备号
            } else {
                respMap.put("device_info", dd.get("device_info"));
            }
            if ("1".equals(dd.get("nonce_str_iswork"))) {
                respMap.put("nonce_str", StringUtil.getRandomString(8));
            } else {
                respMap.put("nonce_str", dd.get("nonce_str"));
            }

            // respMap.put("err_code", "");
            // respMap.put("err_msg", "");

            if ("0".equals(payNotify_result_code)) {
                respMap.put("openid", map.get("openid")); // 用户在商户 appid 下的唯一标识
                // ****必传参数
                respMap.put("trade_type", "pay.weixin.app"); // pay.weixin.app
                // // {非必传}
                respMap.put("pay_result", pay_result); // 支付结果：0—成功；其它—失败
                respMap.put("pay_info", map.get("pay_info")); // 支付结果信息，支付成功时为空
                // // {非必传}

                // respMap.put("out_transaction_id",
                // map.get("out_transaction_id")); //如：微信支付单号，支付宝支付单号 ****非传参数
                if ("1".equals(dd.get("transaction_id_iswork"))) {
                    respMap.put("transaction_id", StringUtil.getStringDate("yyyyMMddhhmmss") + StringUtil.getCode(6, 0)); // 对应支付宝交易记录账单详情中的商户订单号
                } else {
                    respMap.put("transaction_id", dd.get("transaction_id"));
                }

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

                respMap.put("fee_type", dd.get("fee_type")); // {非必传}
                // respMap.put("attach",
                // map.get("attach"));//商家数据包，原样返回预下单时自定义数据
                // respMap.put("bank_type", map.get("bank_type"));//银行类型 //{非必传}
                respMap.put("time_end", StringUtil.getStringDate("yyyyMMddhhmmss")); // ****必传参数
            }
        }

        // logger.debug("respMap:" + respMap);

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
