package com.mock.pay.hx;

import com.alibaba.fastjson.JSONObject;
import com.mock.sign.MD5;
import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 订单查询
 */
@Service
public class HxorderQueryServerImpl implements HxorderQueryServer {

    private static final Logger logger = LoggerFactory.getLogger(HxorderQueryServerImpl.class);

//    private String privateKey= KeyString.privateKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void  payOrderQuery(Map<String, String> map, PrintWriter out) throws IOException, InterruptedException {
        logger.info(" Map:" + map);
        String out_order_no=map.get("out_order_no");





        String command="SELECT * FROM hx_trans_logs tl,hx_mchid_key mk  WHERE tl.out_order_no='"+out_order_no+"' AND tl.mch_id=mk.mch_id";
        logger.info("command:"+command);

        JSONObject jsonObject = new JSONObject();

        Map<String,Object>  trans_info=  jdbcTemplate.queryForMap(command);
        logger.info("从数据中查询到的数据MAP:"+trans_info);
        String order_no=trans_info.get("order_no").toString();
        String mch_name=trans_info.get("mch_name").toString();
        String mch_id=trans_info.get("mch_id").toString();
        String payment_fee=trans_info.get("payment_fee").toString();
        String body=trans_info.get("body").toString();
        String pay_platform=trans_info.get("pay_platform").toString();
        String pay_type=trans_info.get("pay_type").toString();

        String refund_fee=trans_info.get("refund_fee").toString();
        String cur_type=trans_info.get("cur_type").toString();
        String order_status=trans_info.get("order_status").toString();
        String notify_url=trans_info.get("notify_url").toString();
        String create_time=trans_info.get("creat_time").toString();

        System.out.println("create_time;"+create_time);
        String ret_code=trans_info.get("ret_code").toString();

        Map<String, String> cacheMap = new HashMap<String, String>();

        cacheMap.put("out_order_no",out_order_no);
        cacheMap.put("order_no",order_no);
        cacheMap.put("mch_name",mch_name);
        cacheMap.put("mch_id",mch_id);
        cacheMap.put("payment_fee",payment_fee);
        cacheMap.put("body",body);
        cacheMap.put("pay_platform",pay_platform);
        cacheMap.put("pay_type",pay_type);
        cacheMap.put("refund_fee",refund_fee);
        cacheMap.put("cur_type",cur_type);
        cacheMap.put("order_status",order_status);
        cacheMap.put("pay_time",create_time);
        cacheMap.put("create_time",create_time);
        cacheMap.put("ret_code",ret_code);

        jsonObject.put("out_order_no",out_order_no);
        jsonObject.put("order_no",order_no);
        jsonObject.put("mch_name",mch_name);
        jsonObject.put("mch_id",mch_id);
        jsonObject.put("pay_platform",pay_platform);
        jsonObject.put("pay_type",pay_type);
        jsonObject.put("payment_fee",payment_fee);
        jsonObject.put("refund_fee",refund_fee);
        jsonObject.put("cur_type",cur_type);
        jsonObject.put("body",body);
        jsonObject.put("order_status",order_status);
        jsonObject.put("pay_time",create_time);
        jsonObject.put("create_time",create_time);



//        String keycommand="SELECT * FROM hx_mchid_key WHERE mch_id='"+mch_id+"'";
//        logger.info("command:"+keycommand);
//        Map<String,Object>  mch_info=  jdbcTemplate.queryForMap(keycommand);
        String key =trans_info.get("md5_key").toString();

        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        jsonObjects.add(jsonObject);


        JSONObject biz_content = new JSONObject();
        biz_content.put("lists", jsonObjects);


        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("biz_content",biz_content);
        jsonObject2.put("ret_code",ret_code);

//        // 过滤map
//        Map<String, String> params = SignUtils.paraFilter(cacheMap);
//
//        if (params.containsKey("sign"))
//            params.remove("sign");
//        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
//        SignUtils.buildPayParamsSign(buf, params, false);
//        String signatureStr = buf.toString();
//        logger.info("预签名字串：" + signatureStr );
//        String sign2 = MD5.sign(signatureStr, "&key=" + key, "utf-8");//gbk 验理证    //默认要保证一致utf-8
//        String sign= StringUtil.shift(sign2);
//        logger.debug("preStr+key:" + signatureStr + "&key=" + key);
        logger.info("Json:"+jsonObject2);

        String str= "biz_content="+jsonObject2.getString("biz_content")+"&key="+key;
//            String sign = MD5.MD5Encode(str).toUpperCase();
        logger.info("预签名Json串：" + str );
        System.out.println("预签名Json串：" + str );

//        String sign2 = MD5.sign(str, "&key=" + key, "utf-8");//gbk 验理证    //默认要保证一致utf-8
//        String sign= StringUtil.shift(sign2);
        String str2= "biz_content="+jsonObject2.getString("biz_content")+"&key="+key;
        String sign = MD5.MD5Encode(str2).toUpperCase();
        System.out.println("生成Sign：" + sign );

        jsonObject2.put("sign_type","MD5");
        jsonObject2.put("signature",sign);
        jsonObject2.put("ret_msg","success");

        out.print(jsonObject2.toString());
    }

    }
