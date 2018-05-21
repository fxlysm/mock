package com.mock.pay.hx;

import com.alibaba.fastjson.JSONObject;

import com.mock.sign.MD5;
import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import com.mock.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class HXPayHXNoticeServerImpl implements HXNoticeServer {
    private static final Logger logger = LoggerFactory.getLogger(HXPayHXNoticeServerImpl.class);

//    private String privateKey= KeyString.privateKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void  payNotify(Map<String, String> map) throws IOException, InterruptedException {
        logger.info("Redis Map:"+map);

        String notify_url=map.get("notify_url");
        String ret_code=map.get("ret_code");
        String mch_id=map.get("mch_id");
        String out_order_no=map.get("out_order_no");
        String order_no=map.get("order_no");
        String payment_fee=map.get("payment_fee");

        String pay_platform=map.get("pay_platform");
        String pay_type=map.get("pay_type");
        String create_time=map.get("create_time");
        String pay_time= StringUtil.getStringDate("yyyy-MM-dd HH:mm:ss");
        JSONObject jsonObject = new JSONObject();

        if(ret_code.equals("0")){
//            Map<String, String> cacheMap = new HashMap<String, String>();
//            cacheMap.put("ret_code",ret_code);
//            cacheMap.put("mch_id",mch_id);
//            cacheMap.put("out_order_no",out_order_no);
//            cacheMap.put("order_no",order_no);
//            cacheMap.put("payment_fee",payment_fee);
//            cacheMap.put("pay_platform",pay_platform);
//            cacheMap.put("pay_type",pay_type);
//            cacheMap.put("create_time",create_time);
//            cacheMap.put("pay_time",pay_time);
//
//
//
//
//
//            cacheMap.put("sign_type","MD5");







            jsonObject.put("mch_id",mch_id);
            jsonObject.put("out_order_no",out_order_no);
            jsonObject.put("order_no",order_no);
            jsonObject.put("payment_fee",payment_fee);
            jsonObject.put("pay_platform",pay_platform);
            jsonObject.put("pay_type",pay_type);
            jsonObject.put("create_time",create_time);
            jsonObject.put("pay_time",pay_time);


            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("ret_code",ret_code);
            jsonObject2.put("biz_content",jsonObject);



            String sql = "SELECT md5_key FROM hx_mchid_key WHERE mch_id = ?";
            String key = jdbcTemplate.queryForObject(sql,new Object[] {mch_id},String.class);

//            // 过滤map
//            Map<String, String> params = SignUtils.paraFilter(cacheMap);
//
//            if (params.containsKey("sign"))
//                params.remove("sign");
//            StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
//            SignUtils.buildPayParamsSign(buf, params, false);
//            String signatureStr = buf.toString();
//            logger.info("预签名字串：" + signatureStr );
//
//            logger.debug("preStr+key:" + signatureStr + "&key=" + key);


            String str= "biz_content="+jsonObject.toString()+"&key="+key;
//            String sign = MD5.MD5Encode(str).toUpperCase();
            logger.info("预签名Json串：" + str );
            System.out.println("预签名Json串：" + str );

        //       String sign2 = MD5.sign(str, "&key=" + key, "utf-8");//gbk 验理证    //默认要保证一致utf-8

            String str2= "biz_content="+jsonObject2.getString("biz_content")+"&key="+key;
            String sign = MD5.MD5Encode(str2).toUpperCase();

//            String sign=StringUtil.shift(sign2);

            System.out.println("生成Sign：" + sign );



            jsonObject2.put("signature",sign);
//            jsonObject2.put("sign_type","MD5");
            jsonObject2.put("ret_msg","success");

            String setstatus="UPDATE hx_trans_logs SET order_status='2'WHERE out_order_no='"+out_order_no+"'";
            logger.info("更改状态语句："+setstatus);
            jdbcTemplate.update(setstatus);

            logger.info("支付通知参数："+jsonObject2.toString());

            logger.info("支付通知地址;"+notify_url);
            try {
//                String pstr= HttpUtil.httpSend(notify_url,cacheMap);
                String pstr= HttpUtil.PostJson(notify_url,jsonObject2);
                logger.info("返回结果："+pstr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {

        }






    }
}
