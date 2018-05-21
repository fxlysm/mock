package com.mock.pay.hx;


import com.alibaba.fastjson.JSONObject;
import com.mock.sign.MD5;
import com.mock.util.StringUtil;
import com.mock.util.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class hxbilldownServerImpl implements hxbilldownServer{
    private static final Logger logger = LoggerFactory.getLogger(hxbilldownServerImpl.class);


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public void  billdownload(Map<String, String> map,  HttpServletResponse resp) throws IOException, InterruptedException {
        logger.info(" Map:" + map);
        String billfile="";
        String fileContentDetail="平台订单号,商户订单号,商户号,订单类型,交易金额,退款金额,手续费支出,支付平台,支付类型,支付时间\n";

        String mch_id=map.get("mch_id");
        String bill_date=map.get("bill_date");

        String sql = "SELECT md5_key FROM hx_mchid_key WHERE mch_id = ?";
        String key = jdbcTemplate.queryForObject(sql,new Object[] {mch_id},String.class);



        String command="SELECT * FROM hx_trans_logs WHERE  bill_date = '"+bill_date+"' AND mch_id='"+mch_id+"'"+"AND status='0' AND  order_status='2'";
        logger.info("Mysql Command:"+command);
        List rows = jdbcTemplate.queryForList(command);

        Iterator it = rows.iterator();
        while(it.hasNext()) {

            Map userMap = (Map) it.next();

            String order_no= String.valueOf(userMap.get("order_no"));//平台订单号
            String out_order_no= String.valueOf(userMap.get("out_order_no"));//商户订单号
            String mchid= String.valueOf(userMap.get("mch_id"));//商户号
            String  payment_fee= String.valueOf(userMap.get("payment_fee"));//交易金额
            String  refund_fee= String.valueOf(userMap.get("refund_fee"));//退款金额
            String  fee= String.valueOf(userMap.get("fee"));//手续费支出
            String  pay_platform= String.valueOf(userMap.get("pay_platform"));//支付平台
            String  pay_type= String.valueOf(userMap.get("pay_type"));//支付类型
            String pay_time=String.valueOf(userMap.get("creat_time"));//支付类型

//            平台订单号,商户订单号,商户号,订单类型,交易金额,退款金额,手续费支出,支付平台,支付类型,支付时间
//`81020180112201717336561382403302,`3717930393923584,`10002893,`SUCCESS,`20,`0,`0,`ALIPAY,`NATIVE,`2018-01-12 20:17:34

            billfile="`"+order_no+",`"+out_order_no+",`"+mchid+",`"+"SUCCESS,`"+payment_fee+",`"+refund_fee+",`"+fee+",`"+pay_platform+",`"+pay_type+",`"+pay_time+"\n";

            fileContentDetail+=billfile;
            String setstatus="UPDATE hx_trans_logs SET status='1'WHERE out_order_no='"+out_order_no+"'";
            logger.info("更改状态语句："+setstatus);
            jdbcTemplate.update(setstatus);
        }
//        logger.info("对帐单文件格式：平台订单号,商户订单号,商户号,订单类型,交易金额,退款金额,手续费支出,支付平台,支付类型,支付时间");
//        logger.info("对帐单文件内容："+fileContentDetail);
        String centent=fileContentDetail;
        logger.info("对帐单文件内容："+centent);
        String ret_code="0";
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("centent",centent);

        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("biz_content",jsonObject);
        jsonObject2.put("ret_code",ret_code);
//        if(fileContentDetail.length()>0){
//
//        }else {
//            jsonObject2.put("ret_msg","[]");
//        }
        jsonObject2.put("ret_msg","success");
        String str= "biz_content="+jsonObject2.getString("biz_content")+"&key="+key;
//            String sign = MD5.MD5Encode(str).toUpperCase();
        logger.info("预签名Json串：" + str );
        System.out.println("预签名Json串：" + str );

//        String sign2 = MD5.sign(str, "&key=" + key, "utf-8");//gbk 验理证    //默认要保证一致utf-8
//        String sign= StringUtil.shift(sign2);
        String str2= "biz_content="+jsonObject2.getString("biz_content")+"&key="+key;
        String sign = MD5.MD5Encode(str2).toUpperCase();
        System.out.println("生成Sign：" + sign );


        jsonObject2.put("signature",sign);
        jsonObject2.put("sign_type","MD5");

        resp.setCharacterEncoding("UTF-8");
        PrintWriter out=resp.getWriter();
        out.print(centent);
        logger.info("返回参数："+jsonObject2.toString());

    }


}
