package com.mock.pay.hx;

import com.alibaba.fastjson.JSONObject;
import com.mock.config.SystemConf;
import com.mock.pay.pf.impl.PFAlipayMicropayServiceImpl;
import com.mock.sign.MD5;
import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

//海峡
@Service
public class PaySereverImpl implements payServer{
    private static final Logger logger = LoggerFactory.getLogger(PaySereverImpl.class);
    String HXMOCK= SystemConf.HXMOCK_MSG;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

//    @Value("${hx.pay.rate}")
//    private double fee_rate;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public void payResp(Map<String, String> map, PrintWriter out) throws IOException {
        Map<String, String> cacheMap = new HashMap<String, String>();
        String ret_code="";
        if(map.containsKey("auth_code")){
            String auth_code=map.get("auth_code");
            String code=auth_code.substring(0,6);
//            if(code.contains("888888")){
                 ret_code="0";
//            }else {
//                ret_code="1";
//            }

        }else {
            ret_code="0";
        }


        String ret_msg="success";
        String mch_id=map.get("mch_id");
        String out_order_no=map.get("out_order_no");
        String order_no= StringUtil.getStringDate("yyMMddHHmmss")+StringUtil.getCode(8,0);
        String pay_platform=map.get("pay_platform");
        String pay_type=map.get("pay_type");
        String payment_fee=map.get("payment_fee");
        String notify_url=map.get("notify_url");
        String body=map.get("body");

        JSONObject jsonObject = new JSONObject();

        if(ret_code.equals("0")){

            jsonObject.put("mch_id",mch_id);
            jsonObject.put("out_order_no",out_order_no);
            jsonObject.put("order_no",order_no);
            jsonObject.put("payment_fee",payment_fee);


            cacheMap.put("ret_code",ret_code);
            cacheMap.put("mch_id",mch_id);
            cacheMap.put("out_order_no",out_order_no);
            cacheMap.put("order_no",order_no);
            cacheMap.put("payment_fee",payment_fee);
            cacheMap.put("pay_platform",pay_platform);
            cacheMap.put("pay_type",pay_type);
            cacheMap.put("create_time",StringUtil.getStringDate("yyyy-MM-dd HH:mm:ss"));

            if(pay_type.equals("NATIVE")){
                if(pay_platform.equals("WXPAY")){
                    String qrcode="weixin://wxpay/bizpayurl?pr="+order_no;
                    jsonObject.put("qrcode",qrcode);
                    cacheMap.put("qrcode",qrcode);
                }else if(pay_platform.equals("ALIPAY")){
                    String qrcode="https://qr.alipay.com/bax00019raqx"+order_no;
                    jsonObject.put("qrcode",qrcode);
                    cacheMap.put("qrcode",qrcode);
                }else if(pay_platform.equals("SQPAY")){
                    String qrcode="https://qpay.qq.com/qr/"+order_no;
                    jsonObject.put("qrcode",qrcode);
                    cacheMap.put("qrcode",qrcode);
                }


            }else  if(pay_type.equals("JSAPI")){
//                String pay_params="{\"prepayid\":\"WX1217752501201407033233368018\"}";
//                jsonObject.put("pay_params",pay_params);
//                cacheMap.put("pay_params",pay_params);
                JSONObject pay_params = new JSONObject();
                if(pay_platform.equals("WXPAY")){
                    Timestamp d = new Timestamp(System.currentTimeMillis());

                    pay_params.put("appId","wxc3f90f556263fb0c");
                    pay_params.put("nonceStr",StringUtil.getRandomString(8));
                    pay_params.put("package","prepay_id=wx20180118181049f0ee0f571c0844000151");
                    pay_params.put("paySign","98CFA15394CCB0B4F5E083D648031BAF");
                    pay_params.put("signType","MD5");
                    pay_params.put("timeStamp",d);

                }
                jsonObject.put("pay_params",pay_params);

            }




            String biz_content=jsonObject.toString();
            logger.info("biz_content:"+biz_content);

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("biz_content",jsonObject);
            jsonObject2.put("ret_code",ret_code);
            jsonObject2.put("ret_msg",ret_msg);
//            jsonObject2.put("sign_type","MD5");

//            cacheMap.put("ret_code",ret_code);
//            cacheMap.put("sign_type","MD5");


//            String sql = "SELECT md5_key FROM hx_mchid_key WHERE mch_id = ?";
//            String key = jdbcTemplate.queryForObject(sql,new Object[] {mch_id},String.class);

            String command="SELECT * FROM hx_mchid_key WHERE mch_id='"+mch_id+"'";
            logger.info("command:"+command);
            Map<String,Object>  mch_info=  jdbcTemplate.queryForMap(command);
            String key =mch_info.get("md5_key").toString();
            String mch_name=mch_info.get("mch_name").toString();
            String rate=mch_info.get("fee_rate").toString();
            double fee_rate=Double.valueOf(rate);



//            // 过滤map
//            Map<String, String> params = SignUtils.paraFilter(cacheMap);
//
//            if (params.containsKey("sign"))
//                params.remove("sign");
//            StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
//            SignUtils.buildPayParamsSign(buf, params, false);
//            String signatureStr = buf.toString();
//            logger.info("预签名字串：" + signatureStr );
//            String sign2 = MD5.sign(signatureStr, "&key=" + key, "utf-8");//gbk 验理证    //默认要保证一致utf-8
//            String sign=StringUtil.shift(sign2);
//            logger.debug("preStr+key:" + signatureStr + "&key=" + key);
            String str= "biz_content="+jsonObject2.getString("biz_content")+"&key="+key;
//            String sign = MD5.MD5Encode(str).toUpperCase();
            logger.info("预签名Json串：" + str );
            System.out.println("预签名Json串：" + str );

//            String sign2 = MD5.sign(str, "&key=" + key, "utf-8");//gbk 验理证    //默认要保证一致utf-8
//            String sign= StringUtil.shift(sign2);
            String str2= "biz_content="+jsonObject2.getString("biz_content")+"&key="+key;
            String sign = MD5.MD5Encode(str2).toUpperCase();
            System.out.println("生成Sign：" + sign );


            jsonObject2.put("signature",sign);
            jsonObject2.put("sign_type","MD5");
            jsonObject2.put("ret_msg","success");
            out.print(jsonObject2.toString());
            logger.info("返回参数："+jsonObject2.toString());



//
//            jsonObject2.put("signature",sign);
//            out.print(jsonObject2.toString());



            //notify_url
            //**********将参数存入redis中
            logger.info("*********正在进行redis订单缓存******");
            cacheMap.put("notify_url",map.get("notify_url"));
            System.out.println(cacheMap);
            String   tmpString = JSONObject.toJSONString(cacheMap);
            redisTemplate.opsForHash().put(HXMOCK,out_order_no,tmpString);



            //**********将参数存入Mysql中

          String  order_status="1";// 1-未支付；2-成功 3-失败
            int fee=(int) (Integer.valueOf(payment_fee).intValue()*fee_rate/1000);
//            String creattime=StringUtil.getStringDate("yyyy-MM-dd HH:mm:ss");
            String bill_date=StringUtil.getStringDate("yyyyMMdd");
            String creat_time=StringUtil.getStringDate("yyyy-MM-dd HH:mm:ss");

            String sqlcommand="INSERT INTO hx_trans_logs (mch_id,mch_name,out_order_no,order_no,pay_platform,pay_type,ret_code,payment_fee,fee_rate,fee,notify_url,body,bill_date,creat_time,order_status)"+
                    "VALUES('"+mch_id+"','"+mch_name+"','"+out_order_no+"','"+order_no+"','"+pay_platform+"','"+pay_type+"','"+ret_code+"','"+payment_fee+"','"+fee_rate+"','"+fee+"','"+notify_url+"','"+body+"','"+bill_date+"','"+creat_time+"','"+order_status+"')";

            logger.debug("插入订单数据至mysql:"+sqlcommand);

            jdbcTemplate.execute(sqlcommand);



        }else {

            JSONObject jsonObject2 = new JSONObject();

            jsonObject2.put("ret_code",ret_code);
            jsonObject2.put("ret_msg","Fail");
            out.print(jsonObject2.toString());

        }
    }

}
