package com.mock.pay.zx.refund.impl;

import  com.mock.config.SystemConf;
import com.mock.mysql.ZXGetValue;
import com.mock.pay.zx.refund.RefundSsrvice;
import com.mock.sign.MD5;
import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import com.mock.util.XmlUtils;
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
public class RefundServiceImpl implements RefundSsrvice{
    private static final Logger logger = LoggerFactory.getLogger(RefundServiceImpl.class);
    String ZXMOCK= SystemConf.ZXMOCK_MSG;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public  void RefundqueryResp(HttpServletResponse resp, Map<String, String> map) throws IOException {
        Map<String, String> dd = ZXGetValue.GetValue_Refound(map.get("service"));
        logger.debug("*************************** 从数据库mysql中获取到的pay.alipay.native参数**************************");
        logger.debug(String.valueOf(dd));
        String status = "0";
        String result_code = "0";
        String mch_id = map.get("mch_id");
        String Signkey = ZXGetValue.getKeyWithmichId(mch_id);
        logger.debug("Mchid:" + mch_id + "\n");
        logger.debug("Signkey:" + Signkey + "\n");
        String out_trade_no = map.get("out_trade_no");


        String transaction_id = map.get("transaction_id");
        String out_refund_no = map.get("out_refund_no");

        String refund_id = map.get("refund_id");
        String nonce_str=map.get("nonce_str");


        Map<String, String> respMap = new HashMap<String, String>();

        respMap.put("version", "2.0"); //
        respMap.put("charset", "UTF-8"); //
        respMap.put("sign_type", "MD5"); //
        respMap.put("status", status); //
        if("0".equals(status)){
            respMap.put("result_code", result_code); //
            respMap.put("mch_id", map.get("mch_id")); //
            respMap.put("nonce_str", StringUtil.getRandomString(8)); //

            if("0".equals(result_code)){
                if(!map.get("transaction_id").isEmpty()||map.get("transaction_id").length()!=0){
                    respMap.put("transaction_id", map.get("transaction_id")); // 平台订单号

                }
                if(!map.get("out_trade_no").isEmpty()||map.get("out_trade_no").length()!=0){
                    respMap.put("out_trade_no", map.get("out_trade_no")); // 平台订单号

                }
                respMap.put("refund_count", "1"); //
                respMap.put("out_refund_no_0", StringUtil.getStringDate("yyyyMMddHHmmss")+StringUtil.getCode(6, 0));
                respMap.put("refund_id_$n", StringUtil.getStringDate("yyyyMMddHHmmss")+StringUtil.getCode(6, 0));
                respMap.put("refund_channel_0", "ORIGINAL");
                respMap.put("refund_fee_0", "100");
                respMap.put("coupon_refund_fee_0", "100");
                respMap.put("refund_time_0", StringUtil.getStringDate("yyyyMMddHHmmss"));
                respMap.put("refund_status_0", "SUCCESS");

            }


        }





        respMap.put("couponRefundFee", map.get("couponRefundFee")); // 现金券退款金额,
        // 现金券退款金额 ,
        respMap.put("outRefundNo", map.get("outRefundNo")); // 商户退款单号 ,
        respMap.put("refundChannel", "ORIGINAL"); // 退款渠道, ORIGINAL-原路退款，默认 ,
        respMap.put("refundFee", map.get("refundFee")); // 退款金额,
        // 退款总金额,单位为分,可以做部分退款 ,
        respMap.put("refundId", map.get("refundId")); // 平台退款单号 ,
        respMap.put("refundStatus", "SUCCESS"); // 退款状态 = ['SUCCESS', 'FAIL',
        // 'PROCESSING', 'NOTSURE',
        // 'CHANGE']stringEnum:"SUCCESS",
        // "FAIL", "PROCESSING",
        // "NOTSURE", "CHANGE",
        respMap.put("refundTime", StringUtil.getStringDate("yyyyMMddhhmmss")); // 退款时间

        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);
        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);

        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();

        String sign = MD5.sign(preStr, "&key=" + Signkey, "utf-8");

        respMap.put("sign", sign);

        logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
        logger.debug("sign:" + sign);

        resp.setHeader("Content-type", "text/xml;charset=utf-8");
        String res = XmlUtils.toXml(respMap);
        resp.getWriter().write(res);

    }

    /****
     * 退款请求
     *
     * @param resp
     * @param map
     * @throws IOException
     */
    @Override
    public  void RefundPayResp(HttpServletResponse resp, Map<String, String> map) throws IOException {
        logger.debug("接收到的MAP:"+map);
        String Signkey = "";
        Map<String, String> dd = ZXGetValue.GetValue_Refound("unified.trade.refund");
        logger.debug("*******Read config from **.l59 mysql*******");
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

        String mch_id = map.get("mch_id");
//		String Signkey = ScanPayGetValue.getKeyWithmichId(mch_id);
        logger.debug("Mchid:" + mch_id + "\n");
        logger.debug("Signkey:" + Signkey + "\n");

        Map<String, String> respMap = new HashMap<String, String>();

        respMap.put("version", dd.get("version")); // is not null 版本号 "2.0"
        respMap.put("charset", dd.get("charset")); // 可选值 UTF-8 ，默认为 UTF-8
        respMap.put("sign_type", dd.get("sign_type")); // 签名方式
        respMap.put("status", status);
        // respMap.put("message", "");

        if ("0".equals(status)) {
            respMap.put("result_code", result_code);
//			respMap.put("mch_id", map.get("mch_id"));
            if ("1".equals(dd.get("mch_id_iswork"))) {
                respMap.put("mch_id", map.get("mch_id")); // 商户号，由威富通分配----从网络接收
            } else {
                respMap.put("mch_id", dd.get("mch_id")); // 商户号，由威富通分配----从数据库中读取
            }
//			respMap.put("device_info", "device_info");
            if ("1".equals(dd.get("nonce_str_iswork"))) {
                respMap.put("nonce_str", StringUtil.getRandomString(8)); // 随机字符串
            } else {
                respMap.put("nonce_str", dd.get("nonce_str")); // 随机字符串----从数据库中读取
            }

//			respMap.put("err_code", "err_code");

            if ("0".equals(result_code)) {
//				respMap.put("transaction_id", map.get("transaction_id"));
                if ("1".equals(dd.get("transaction_id_iswork"))) {
                    if(map.containsKey("transaction_id")){
                        respMap.put("transaction_id", map.get("transaction_id")); // 若传了transaction_id则直接由MAP中传值
                    }else {
                        respMap.put("transaction_id", StringUtil.getStringDate("yyyyMMddHHmmss")+StringUtil.getCode(8, 0)); // 随机生成一个
                    }

                } else {
                    respMap.put("transaction_id", dd.get("transaction_id")); // 随机字符串----从数据库中读取
                }

                if ("1".equals(dd.get("out_trade_no_iswork"))) {
                    if(map.containsKey("out_trade_no")){
                        respMap.put("out_trade_no", map.get("out_trade_no")); // 直接由MAP中传值
                    }else {
                        respMap.put("out_trade_no", StringUtil.getStringDate("yyyyMMddHHmmss")+StringUtil.getCode(8, 0)); // 	随机生成一个
                    }

                } else {
                    respMap.put("out_trade_no", dd.get("out_trade_no")); // 随机字符串----从数据库中读取
                }
                if ("1".equals(dd.get("out_refund_no_iswork"))) {

                    respMap.put("out_refund_no", map.get("out_refund_no")); //直接由MAP中传值
                } else {
                    respMap.put("out_refund_no", dd.get("out_refund_no")); // 随机字符串----从数据库中读取
                }
                if ("1".equals(dd.get("refund_id_iswork"))) {
                    respMap.put("refund_id", StringUtil.getStringDate("yyyyMMddHHmmss")+StringUtil.getCode(8, 0)); //
                } else {
                    respMap.put("refund_id", dd.get("refund_id")); // 随机字符串----从数据库中读取
                }

//				respMap.put("out_refund_no", map.get("out_refund_no"));
//				respMap.put("refund_id", tools.getStringDate("yyyyMMddHHmmss")+tools.getCode(8, 0));

                respMap.put("refund_channel", dd.get("refund_channel"));
                if ("1".equals(dd.get("refund_fee_iswork"))) {
                    respMap.put("refund_fee", map.get("refund_fee")); //
                } else {
                    respMap.put("refund_fee", dd.get("refund_fee")); // 随机字符串----从数据库中读取
                }
//				respMap.put("refund_fee", map.get("refund_fee"));
//				respMap.put("coupon_refund_fee", map.get("coupon_refund_fee"));

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

        respMap.put("sign", sign);

        logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
        logger.debug("sign:" + sign);

        resp.setHeader("Content-type", "text/xml;charset=utf-8");
        String res = XmlUtils.toXml(respMap);
        logger.debug("Res:" + res);
        resp.getWriter().write(res);

    }

    /***
     * 撤销订单 接口类型：unified.micropay.reverse
     *
     * @param map
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public  void Payreverse(HttpServletResponse resp, Map<String, String> map) {


        String status = "0";
        String result_code = "0";
        String mch_id = map.get("mch_id");
        String Signkey = ZXGetValue.getKeyWithmichId(mch_id);
        logger.debug("Mchid:" + mch_id + "\n");
        logger.debug("Signkey:" + Signkey + "\n");
        Map<String, String> respMap = new HashMap<String, String>();

        respMap.put("version", "2.0");
        respMap.put("charset", "UTF-8");
        respMap.put("sign_type", "MD5");
        respMap.put("status", status);

        if ("0".equals(status)) {
            respMap.put("result_code", result_code);
            respMap.put("mch_id", map.get("mch_id"));
            respMap.put("nonce_str", map.get("nonce_str"));
//			respMap.put("err_code", map.get("err_code"));
//			respMap.put("err_msg", map.get("err_msg"));

        }

        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);
        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);

        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();

        String sign = MD5.sign(preStr, "&key=" + Signkey, "utf-8");

        respMap.put("sign", sign);

        logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
        logger.debug("sign:" + sign);

        resp.setHeader("Content-type", "text/xml;charset=utf-8");
        String res = XmlUtils.toXml(respMap);
        try {
            resp.getWriter().write(res);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
