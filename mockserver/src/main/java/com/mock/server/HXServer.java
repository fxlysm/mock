package com.mock.server;




import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mock.pay.hx.HxorderQueryServer;
import com.mock.pay.hx.hxbilldownServer;
import com.mock.pay.hx.payServer;
import com.mock.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

@RestController
@RequestMapping(value = "/hx")
public class HXServer {
    private static final Logger logger = LoggerFactory.getLogger(HXServer.class);

    @Autowired
    private payServer payserver;


    @Autowired
    private HxorderQueryServer hxorderQueryServer;


    @Autowired
    private hxbilldownServer billdownServer;


    @RequestMapping(value = "/unifiedorder", method = RequestMethod.POST)//,consumes="application/json"
    public void Hxpay(@RequestBody(required=true) String str , PrintWriter out ){
        logger.info("str0:"+str);

        try {
            String urlStr = URLDecoder.decode(str, "UTF-8");
            logger.info("str:"+urlStr);
//           Object succesResponse = JSON.parse(urlStr);    //先转换成Object
//            Map map = (Map)succesResponse;         //Object强转换为Map

            Map<String, String> map=StringUtil.getmap(urlStr);
            logger.info("map"+map);
            String biz_content=map.get("biz_content");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> maps = null;
            try {
                maps = objectMapper.readValue(
                        biz_content, Map.class);
                logger.info("maps"+maps);


                if(maps.get("pay_platform").toString().equals("WXPAY")){
                    //微信
                    logger.info("接收到微信相关支付");
                    if(maps.get("pay_type").toString().equals("NATIVE")){
                        logger.info("接收到微信扫码支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("JSAPI")){
                        logger.info("接收到微信公众号支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("APP")){
                        logger.info("接收到微信APP支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("MICROPAY")){
                        logger.info("接收到微信刷卡支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("WAP")){
                        logger.info("接收到微信WAP支付");
                        payserver.payResp(maps,out);
                    }

                }else if(maps.get("pay_platform").toString().equals("ALIPAY")){
                    //支付宝
                    logger.info("接收到支付宝相关支付");

                    if(maps.get("pay_type").toString().equals("NATIVE")){
                        logger.info("接收到支付宝扫码支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("JSAPI")){
                        logger.info("接收到支付宝公众号支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("APP")){
                        logger.info("接收到支付宝APP支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("MICROPAY")){
                        logger.info("接收到支付宝刷卡支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("WAP")){
                        logger.info("接收到支付宝WAP支付");
                        payserver.payResp(maps,out);
                    }
                }else if(maps.get("pay_platform").toString().equals("SQPAY")){
                    //QQ
                    logger.info("接收到QQ相关支付");
                    if(maps.get("pay_type").toString().equals("NATIVE")){
                        logger.info("接收到QQ扫码支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("JSAPI")){
                        logger.info("接收到QQ公众号支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("APP")){
                        logger.info("接收到QQAPP支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("MICROPAY")){
                        logger.info("接收到QQ刷卡支付");
                        payserver.payResp(maps,out);
                    }else if(maps.get("pay_type").toString().equals("WAP")){
                        logger.info("接收到QQ WAP支付");
                        payserver.payResp(maps,out);
                    }
                }else {
                    out.print("not support");
                }



            } catch (IOException e) {
                e.printStackTrace();
            }






        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }




    @RequestMapping(value = "/orderquery", method = RequestMethod.POST)
    public void getQueryOrder(@RequestBody(required=true) String str , PrintWriter out )  {
        logger.info("接收到的参数:"+str);


        try {
            String urlStr = URLDecoder.decode(str, "UTF-8");
            logger.info("str:"+urlStr);
//           Object succesResponse = JSON.parse(urlStr);    //先转换成Object
//            Map map = (Map)succesResponse;         //Object强转换为Map

            Map<String, String> map=StringUtil.getmap(urlStr);
            logger.info("map"+map);
            String biz_content=map.get("biz_content");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> maps = null;
            try {
                maps = objectMapper.readValue(
                        biz_content, Map.class);
                logger.info("*********执行订单查询************");
                logger.info("maps"+maps);

                try {
                    hxorderQueryServer.payOrderQuery(maps,out);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }






        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }







    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public void getbill(@RequestBody(required=true) String str , PrintWriter out , HttpServletResponse resp) {
        logger.info("接收到对帐单下载参数:" + str);


        try {
            String urlStr = URLDecoder.decode(str, "UTF-8");
            logger.info("str:" + urlStr);

            Map<String, String> map = StringUtil.getmap(urlStr);
            logger.info("map" + map);

            String biz_content=map.get("biz_content");
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> maps = null;
            try {
                maps = objectMapper.readValue(
                        biz_content, Map.class);
                logger.info("*********执行对帐单参数：************");
                logger.info("maps"+maps);

                try {
                    billdownServer.billdownload(maps,resp);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}


