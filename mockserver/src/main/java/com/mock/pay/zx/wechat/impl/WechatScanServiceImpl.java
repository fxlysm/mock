package com.mock.pay.zx.wechat.impl;

import com.alibaba.fastjson.JSONObject;
import com.mock.config.SystemConf;
import com.mock.mysql.ZXGetValue;
import com.mock.pay.zx.wechat.WechatScanService;
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
public class WechatScanServiceImpl implements WechatScanService {
    private static final Logger logger = LoggerFactory.getLogger(WechatScanServiceImpl.class);
    String ZXMOCK= SystemConf.ZXMOCK_MSG;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void payResp(HttpServletResponse resp, Map<String, String> map) throws IOException {
        String Signkey="";
        Map<String, String> dd=ZXGetValue.GetValue_Req(map.get("service"));
        logger.debug("*************************** 从数据库mysql中获取到的pay.alipay.native参数**************************");
        logger.debug(String.valueOf(dd));
        System.out.println(dd);
        logger.debug("\n\n");

        String status=dd.get("status");
        String result_code =dd.get("result_code");
//		String Signkey=dd.get("signkey");

        if("1".equals(dd.get("key_iswork"))){
            Signkey=ZXGetValue.getKeyWithmichId(map.get("mch_id"));
        }else {
            Signkey=dd.get("signkey");
        }
        logger.debug("Mchid:"+map.get("mch_id")+"\n");
        logger.debug("Signkey:"+Signkey+"\n");
        logger.debug("********************************支付响应回调********************************");
        String notify_url = map.get("notify_url");
        String out_trade_no = map.get("out_trade_no");
        String total_fee = map.get("total_fee");
        String mch_id = map.get("mch_id");
        String nonce_str = map.get("nonce_str");
        String service = map.get("service");


        //缓存对象
        Map<String, String> cacheMap = new HashMap<String, String>();
        cacheMap.put("out_trade_no", out_trade_no);
        cacheMap.put("notify_url", notify_url);
        cacheMap.put("total_fee", total_fee);
        cacheMap.put("mch_id", mch_id);
        cacheMap.put("nonce_str", nonce_str);
        cacheMap.put("service", service);
        cacheMap.put("pay_status", status);
        cacheMap.put("pay_result_code", result_code);

        cacheMap.put("transId", "transId_"+StringUtil.getStringDate("yyyymmddhhmmss"));
        //set redisCache
        if (StringUtils.isNotBlank(out_trade_no)) {
            logger.debug("set rediscache...");
            String tmpString = JSONObject.toJSONString(cacheMap);
            redisTemplate.opsForHash().put(ZXMOCK,out_trade_no,tmpString);
        }
        logger.debug("set rediscache. map:" + cacheMap);



        Map<String, String> respMap = new HashMap<String, String>();

        //接口-支付结果查询-参数检验

        respMap.put("version", dd.get("version")); // is not null 版本号 "2.0"
//		logger.debug("*******************设置字符类型为gdk**********************************************");
        respMap.put("charset", dd.get("charset")); // is not null 字符集   charset=UTF-8" charset=gbk" charset=gb2312"
        respMap.put("sign_type", dd.get("sign_type")); // 签名方式
        respMap.put("status", status); // is not null 返回状态码

        // respMap.put("message", ""); // 返回信息,如非空，为错误原因签名失败参数格式校验错误

        // 以下字段在 status 为 0的时候有返回
        if("0".equals(status)){
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

//			logger.debug("*******************设置随机字符大于32位**********************************************");
//			String monceString=tools.getRandomString(35);   //测试代码
//			logger.debug("设置nonce_str："+monceString);   //测试代码

            // respMap.put("err_code", "");
//			respMap.put("err_msg", "");

            //以下字段在 status 和 result_code 都为 0的时候有返回
            if("0".equals(result_code)){
                // 以下字段在 status 和 result_code 都为 0的时候有返回
                respMap.put("code_url", "http://192.168.74.163:2225/qrcode?uuid="+map.get(service)+StringUtil.getRandomString(8)); // 二维码链接
                respMap.put("code_img_url", "http://192.168.74.163:2225/qrcode?uuid="+map.get(service)+StringUtil.getRandomString(8)); // 二维码图片
            }



//			logger.debug("****************打印过滤map后的信息**************");
            // 过滤map
            Map<String, String> params = SignUtils.paraFilter(respMap);



            if (params.containsKey("sign"))
                params.remove("sign");
            StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
            SignUtils.buildPayParams(buf, params, false);
            String preStr = buf.toString();

//			logger.debug("\n\n*********************设置M5签名为gbk*****************************");

            String sign =MD5.sign(preStr, "&key=" + Signkey, "utf-8");//gbk 验理证    //默认要保证一致utf-8

            logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
            logger.debug("sign:" + sign);
            respMap.put("sign", sign);


        }

        logger.debug(String.valueOf(respMap));
        resp.setHeader("Content-type", "text/xml;charset=utf-8");
        String res = XmlUtils.toXml(respMap);
        logger.debug("支付响应回调XML:\n"+res+"\n");
        resp.getWriter().write(res);

        logger.debug("********************************支付响应回调End********************************");
    }

    @Override
    public  void payNotify(Map<String, String> map) throws IOException, InterruptedException {
        String Signkey="";
        Map<String, String> dd=ZXGetValue.GetValue_Notice(map.get("service"));
        logger.debug("*************************** 从数据库mysql中获取到的pay.alipay.native参数**************************");
        logger.debug(String.valueOf(dd));
        System.out.println(dd);
        logger.debug("\n\n");
        String payNotify_status=dd.get("status");
        String payNotify_result_code =dd.get("result_code");
        String pay_result=dd.get("pay_result");
//		 String Signkey=dd.get("signkey");

        if("1".equals(dd.get("key_iswork"))){
            Signkey= ZXGetValue.getKeyWithmichId(map.get("mch_id"));
        }else {
            Signkey=dd.get("signkey");
        }
//		 String Signkey=ScanPayGetValue.getKeyWithmichId(map.get("mch_id"));
        logger.debug("Mchid:"+map.get("mch_id")+"\n");
        logger.debug("Signkey:"+Signkey+"\n");
        logger.debug("********************************支付通知回调---Start********************************");


        logger.debug("payNotify map params:" + map);
        String notifyUrl = map.get("notify_url");
        String service = map.get("service");

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("version", dd.get("version")); // is not null 版本号
        respMap.put("charset", dd.get("charset")); // is not null 字符集
        respMap.put("sign_type", dd.get("sign_type")); // 签名方式
        respMap.put("status", payNotify_status); // is not null 返回状态码
        // respMap.put("message", ""); // 返回信息,如非空，为错误原因签名失败参数格式校验错误

        if("0".equals(payNotify_status)){// 以下字段在 status 为 0的时候有返回
            respMap.put("result_code", payNotify_result_code); // 业务结果 0表示成功非0表示失败

            if("1".equals(dd.get("mch_id_iswork"))){
                respMap.put("mch_id", map.get("mch_id")); // 商户号，由威富通分配
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




            if("0".equals(payNotify_result_code)){
                // 以下字段在 status 和 result_code 都为 0的时候有返回
                respMap.put("openid", "11111"); // 用户支付宝的账户名------非必传
                respMap.put("trade_type", map.get("service")); // 交易类型----非必传 map.get("service")
                respMap.put("pay_result", pay_result); // 支付结果：0—成功；其它—失败
//				respMap.put("pay_info", ""); // 支付结果信息，支付成功时为空

//				respMap.put("out_transaction_id", "0"); // 对应支付宝交易记录账单详情中的交易号
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
                //货币种类
                respMap.put("fee_type", dd.get("fee_type"));
                respMap.put("time_end", StringUtil.getStringDate("yyyyMMddHHmmss")); // 支付完成时间   ---非必传

            }
        }

        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);
        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();


        logger.debug("\n\n");

        logger.debug("微信扫码支付签名："+Signkey);
        String sign = MD5.sign(preStr, "&key=" + Signkey, "utf-8");//gbk 验理证    //默认要保证一致utf-8
        logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
        logger.debug("sign:" + sign);
        respMap.put("sign", sign);

        logger.debug("\n\n");


        String respXML = XmlUtils.toXml(respMap);

        logger.debug("httpPostRequestXML:" + notifyUrl + "\n" + respXML);

        String respString = httpUtil.httpPostRequestXML(notifyUrl, respXML);
        logger.debug("支付结果通知响应：" + respString);

        logger.debug("********************************支付通知回调---End********************************");
    }
}
