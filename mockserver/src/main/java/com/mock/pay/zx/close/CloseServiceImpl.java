package com.mock.pay.zx.close;

import com.mock.config.SystemConf;
import com.mock.mysql.ZXGetValue;
import com.mock.sign.MD5;
import com.mock.util.SignUtils;
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
public class CloseServiceImpl implements CloseService{
    private static final Logger logger = LoggerFactory.getLogger(CloseServiceImpl.class);
    String ZXMOCK= SystemConf.ZXMOCK_MSG;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public  void orderclosereq(HttpServletResponse resp, Map<String, String> map) throws IOException {

//		Thread.sleep(4000);
        String status="0";
        String result_code ="0";
        //获取商户订单号
        String out_trade_no = map.get("out_trade_no");
        String service = map.get("service");
        String mch_id = map.get("mch_id");
        String Signkey = ZXGetValue.getKeyWithmichId(mch_id);
        logger.debug("Mchid:" + mch_id + "\n");
        logger.debug("Signkey:" + Signkey + "\n");
//        //通过 订单号查询数据
//        JSONObject jsonObject = JSONObject.parseObject((String) redisCache.get(out_trade_no));
//        Map<String, String> tmp = JSONObject.parseObject(jsonObject.toJSONString(), new TypeReference<Map<String, String>>(){});


        Map<String, String> respMap = new HashMap<String, String>();


        respMap.put("version", "2.0"); // is not null 版本号
        respMap.put("charset", "UTF-8"); // is not null 字符集
        respMap.put("sign_type", "MD5"); // 签名方式

        respMap.put("status", status); // is not null 返回状态码
        // respMap.put("message", ""); // 返回信息,如非空，为错误原因签名失败参数格式校验错误

        if("0".equals(status)){
            // 以下字段在 status 为 0的时候有返回
            respMap.put("result_code", result_code); // 业务结果 0表示成功非0表示失败
            respMap.put("mch_id", map.get("mch_id")); // 商户号，由威富通分配

            respMap.put("nonce_str", map.get("nonce_str"));
            // respMap.put("err_code", "");
            // respMap.put("err_msg", "");
            // respMap.put("sign", "");

        }


        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);
        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);

        SignUtils.buildPayParams(buf, params, false);
        String preStr = buf.toString();

        String sign = MD5.sign(preStr, "&key=" + Signkey, "utf-8");//gbk 验理证    //默认要保证一致utf-8
        logger.debug("preStr+key:" + preStr + "&key=" + Signkey);
        logger.debug("sign:" + sign);
        respMap.put("sign", sign);

        resp.setHeader("Content-type", "text/xml;charset=utf-8");
        String res = XmlUtils.toXml(respMap);
        logger.debug("关单回调参数" + res);
        resp.getWriter().write(res);

    }
}
