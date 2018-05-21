package com.mock.pay.pf.impl;

import com.alibaba.fastjson.JSONObject;
import com.mock.config.SystemConf;
import com.mock.pay.pf.OtherServer;
import com.mock.sign.ChinaCardPosUtil;
import com.mock.sign.KeyString;
import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Service
public class OtherServerImpl implements OtherServer{
    private static final Logger logger = LoggerFactory.getLogger(OtherServerImpl.class);
    String PFMOCKREFOUND= SystemConf.PFMOCK_REFOUND;
    private String privateKey= KeyString.privateKey;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void  balancequery( Map<String, String> map,PrintWriter out) throws IOException{
        //余额查询
        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型
        String productId=map.get("productId");//产品类型
    }

    @Override
    public void  Transactionstatusquery( Map<String, String> map,PrintWriter out) throws IOException {
        //交易状态查询
        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型
        String merNo=map.get("merNo");//商户号
        String orderDate=map.get("orderDate");//订单日期
        String orderNo=map.get("orderNo");//商户订单号
//        String merNo=map.get("merNo");//商户号
        String command="SELECT * FROM pf_trans_logs WHERE orderNo='"+orderNo+"'";
        logger.info("command:"+command);
      Map<String,Object> translogs=  jdbcTemplate.queryForMap(command);
      if(translogs.containsKey("transAmt")){
        Object free= translogs.get("transAmt");
        String transAmt= String.valueOf(free);

          String respCode="0000";
          String respDesc="success";

          Map<String, String> respMap = new HashMap<String, String>();
          respMap.put("requestNo",requestNo);//请求流水号
          respMap.put("version",version);//版本号
          respMap.put("transId",transId);//交易类型
          respMap.put("merNo",merNo);//商户号
          respMap.put("orderDate",orderDate);//订单日期
          respMap.put("orderNo",orderNo);//商户订单号
          respMap.put("origRespCode",respCode);//原交易应答码
          respMap.put("origRespDesc",respDesc);//原交易应答码描述
          respMap.put("transAmt",transAmt);//交易金额
          respMap.put("refundAmt","0");//已退金额
          respMap.put("respCode",respCode);//应答码
          respMap.put("respDesc",respDesc);//应答码描述
          // 过滤map
          Map<String, String> params = SignUtils.paraFilter(respMap);

          if (params.containsKey("sign"))
              params.remove("sign");
          StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
          SignUtils.buildPayParams(buf, params, false);
          String signatureStr = buf.toString();
          logger.info("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
          String signature = null;
          try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
              signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
          } catch (Exception e) {
              e.printStackTrace();
          }

          logger.info("签名：" + signature );
          respMap.put("signature",signature);
//        resp.getWriter().write(String.valueOf(respMap));
          Map<String, String> postparams = SignUtils.paraFilter(respMap);
          StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
          SignUtils.buildPayParams(postbuf, postparams, false);
          String postStr = postbuf.toString();
          logger.info("返回参数：" + postStr );
          out.print(postStr);

      }else {
          String respCode="0028";
          String respDesc="fail";

          Map<String, String> respMap = new HashMap<String, String>();
          respMap.put("requestNo",requestNo);//请求流水号
          respMap.put("version",version);//版本号
          respMap.put("transId",transId);//交易类型
          respMap.put("merNo",merNo);//商户号
          respMap.put("orderDate",orderDate);//订单日期
          respMap.put("orderNo",orderNo);//商户订单号
          respMap.put("origRespCode",respCode);//原交易应答码
          respMap.put("origRespDesc",respDesc);//原交易应答码描述
          respMap.put("transAmt","0");//交易金额
          respMap.put("refundAmt","0");//已退金额
          respMap.put("respCode",respCode);//应答码
          respMap.put("respDesc",respDesc);//应答码描述
          // 过滤map
          Map<String, String> params = SignUtils.paraFilter(respMap);

          if (params.containsKey("sign"))
              params.remove("sign");
          StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
          SignUtils.buildPayParams(buf, params, false);
          String signatureStr = buf.toString();
          logger.info("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
          String signature = null;
          try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
              signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
          } catch (Exception e) {
              e.printStackTrace();
          }

          logger.info("签名：" + signature );
          respMap.put("signature",signature);
//        resp.getWriter().write(String.valueOf(respMap));
          Map<String, String> postparams = SignUtils.paraFilter(respMap);
          StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
          SignUtils.buildPayParams(postbuf, postparams, false);
          String postStr = postbuf.toString();
          logger.info("返回参数：" + postStr );
          out.print(postStr);
      }



    }



    @Override
    public void  Refund( Map<String, String> map,PrintWriter out) throws IOException {
        //退货
        String respCode="0000";
        String respDesc=null;

        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型
        String merNo=map.get("merNo");//商户号
        String orderDate=map.get("orderDate");//退货日期
        String orderNo=map.get("orderNo");//退货订单号
        String origOrderDate=map.get("origOrderDate");//订单日期
        String origOrderNo=map.get("origOrderNo");//商户订单号
        String returnUrl=map.get("returnUrl");//页面通知地址
        String notifyUrl=map.get("notifyUrl");//异步通知地址
        String transAmt=map.get("transAmt");//申请退货金额
        String refundReson=map.get("refundReson");//退货原因

        String pfrefundId= StringUtil.getStringDate("yMMddHHmmss")+StringUtil.getCode(10,0);
        //保存在数据MYSQL
        if("0000".equals(respCode)){
            respDesc="success";
            String sqlcommand="INSERT INTO pf_trans_logs (requestNo,transId,merNo,orderDate,orderNo,origOrderDate,origOrderNo,notifyUrl,returnUrl,transAmt,pfrefundId,refundReson)"+
                    "VALUES('"+requestNo+"','"+transId+"','"+merNo+"','"+orderDate+"','"+orderNo+"','"+origOrderDate+"','"+origOrderNo+"','"+notifyUrl+"','"+returnUrl+"','-"+transAmt+"','"+pfrefundId+"','"+refundReson+"')";

            logger.debug("插入退款数据至mysql:"+sqlcommand);

            jdbcTemplate.execute(sqlcommand);

            String sql = "SELECT productId FROM pf_trans_logs WHERE orderNo = ?";
            String productId=jdbcTemplate.queryForObject(sql,new Object[] {origOrderNo},String.class);
            logger.info("productId:"+productId);
            Map<String, String> cacheMap = new HashMap<String, String>();
            cacheMap.put("productId",productId);//交易类型
            cacheMap.put("requestNo",requestNo);
            cacheMap.put("version",version);
            cacheMap.put("transId",transId);
            cacheMap.put("merNo",merNo);
            cacheMap.put("orderDate",orderDate);
            cacheMap.put("orderNo",orderNo);
            cacheMap.put("returnUrl",returnUrl);
            cacheMap.put("notifyUrl",notifyUrl);
            cacheMap.put("transAmt",transAmt);
            cacheMap.put("respCode",respCode);

            // set redisCache
            if (StringUtils.isNotBlank(orderNo)) {
                logger.debug("set Refund rediscache...");
                String   tmpString = JSONObject.toJSONString(cacheMap);
//            redisCache.put(out_trade_no, tmpString);
                logger.info("rediscacheString:"+tmpString);
                redisTemplate.opsForHash().put(PFMOCKREFOUND,orderNo,tmpString);

            }

        }else {
            respDesc="fail";
        }

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("requestNo",requestNo);//请求流水号
        respMap.put("version","v1.1");//版本号
        respMap.put("transId",transId);//交易类型



        respMap.put("merNo",merNo);//商户号
        respMap.put("orderDate",orderDate);//退货日期
        respMap.put("orderNo",orderNo);//退货订单号
        respMap.put("origOrderDate",origOrderDate);//订单日期
        respMap.put("origOrderNo",origOrderNo);//商户订单号
        respMap.put("returnUrl",returnUrl);//页面通知地址
        respMap.put("notifyUrl",notifyUrl);//异步通知地址
        respMap.put("transAmt",transAmt);//交易金额--申请退货金额
        respMap.put("refundReson",refundReson);//退货原因
//        respMap.put("refundReson",refundReson);//退货原因
        respMap.put("respCode",respCode);//应答码
        respMap.put("pfrefundId",pfrefundId);//退款平台订单号
        respMap.put("orderTime", StringUtil.getStringDate("YYYYMMDDhhmmss"));//订单交易时间




        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);

        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String signatureStr = buf.toString();
        logger.info("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        String signature = null;
        try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
            signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("签名：" + signature );
        respMap.put("signature",signature);
//        resp.getWriter().write(String.valueOf(respMap));
        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );
        out.print(postStr);
    }

    @Override
    public void  CloseOrder( Map<String, String> map,PrintWriter out) throws IOException {
        //撤销
        String respCode="0000";
        String respDesc=null;

        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型
        String merNo=map.get("merNo");//商户号
        String orderDate=map.get("orderDate");//撤销日期

        String orderNo=map.get("orderNo");//撤销订单号
        String origOrderNo=map.get("origOrderNo");//商户订单号

        String returnUrl=map.get("returnUrl");//页面通知地址
        String notifyUrl=map.get("notifyUrl");//异步通知地址
        String transAmt=map.get("transAmt");//申请退货金额


        //保存在数据MYSQL
        if("0000".equals(respCode)){
            respDesc="success";
        }else {
            respDesc="fail";
        }
        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("requestNo",requestNo);//请求流水号
        respMap.put("version","v1.1");//版本号
        respMap.put("transId",transId);//交易类型
        respMap.put("merNo",merNo);//商户号
        respMap.put("orderDate",orderDate);//撤销日期
        respMap.put("orderNo",orderNo);//撤销订单号


        respMap.put("origOrderNo",origOrderNo);//商户订单号
        respMap.put("returnUrl",returnUrl);//页面通知地址
        respMap.put("notifyUrl",notifyUrl);//异步通知地址
        respMap.put("transAmt",transAmt);//交易金额

        respMap.put("respCode",respCode);//应答码
        respMap.put("respDesc",respDesc);//应答码描述
        respMap.put("orderTime", StringUtil.getStringDate("YYYYMMDDhhmmss"));//订单交易时间

// 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);

        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String signatureStr = buf.toString();
        logger.info("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        String signature = null;
        try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
            signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("签名：" + signature );
        respMap.put("signature",signature);
//        resp.getWriter().write(String.valueOf(respMap));
        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );
        out.print(postStr);
    }




    @Override
    public void  WechatOa( Map<String, String> map,PrintWriter out) throws IOException {
        String jsApiPath=null;
        String subAppId=null;
        String subScribeAppid=null;

        //撤销
        String respCode="0000";
        String respDesc=null;

        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型

        String payWay=map.get("payWay");//支付渠道   本接口固定传入WX
        String merNo=map.get("merNo");//商户号
        String subMchId=map.get("subMchId");//二级商户编号

        jsApiPath=map.get("jsApiPath");//授权目录
         subAppId=map.get("subAppId");//关联APPID
        subScribeAppid=map.get("subScribeAppid");//授权目录

        String sqlcommand="INSERT INTO pf_wechatoa (requestNo,version,transId,payWay,merNo,subMchId,jsApiPath,subAppId,subScribeAppid)"+
                "VALUES('"+requestNo+"','"+version+"','"+transId+"','"+payWay+"','"+merNo+"','"+subMchId+"','"+jsApiPath+"','"+subAppId+"','"+subScribeAppid+"')";


        jdbcTemplate.execute(sqlcommand);
        //保存在数据MYSQL
        if("0000".equals(respCode)){
            respDesc="success";
        }else {
            respDesc="fail";
        }
        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("requestNo",requestNo);//请求流水号
        respMap.put("version","v1.1");//版本号
        respMap.put("transId",transId);//交易类型

        respMap.put("merNo",merNo);//商户号
        respMap.put("payWay",payWay);//支付渠道
        respMap.put("merNo",merNo);//商户号
        respMap.put("subMchId",subMchId);//二级商户编号

        if(map.containsKey("jsApiPath")){

            respMap.put("jsApiPath",map.get("jsApiPath"));//授权目录
        }
        if(map.containsKey("subAppId")){

            respMap.put("subAppId",subAppId);//关联APPID
        }
        if(map.containsKey("subScribeAppid")){

            respMap.put("subScribeAppid",subScribeAppid);//授权目录
        }
        respMap.put("respCode",respCode);//应答码
        respMap.put("respDesc",respDesc);//应答码描述

// 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);

        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String signatureStr = buf.toString();
        logger.info("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        String signature = null;
        try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
            signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("签名：" + signature );
        respMap.put("signature",signature);
//        resp.getWriter().write(String.valueOf(respMap));
        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );
        out.print(postStr);
    }


    @Override
    public void  WechatOaQuery( Map<String, String> map,PrintWriter out) throws IOException {
        //撤销
        String respCode="0000";
        String respDesc=null;

        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型

        String payWay=map.get("payWay");//支付渠道   本接口固定传入WX
        String merNo=map.get("merNo");//商户号
        String subMchId=map.get("subMchId");//二级商户编号

        String command="SELECT * FROM pf_wechatoa WHERE merNo='"+merNo+"' AND subMchId='"+subMchId+"'";
       Map<String,Object> qmap= jdbcTemplate.queryForMap(command);

        //保存在数据MYSQL
        if("0000".equals(respCode)){
            respDesc="success";
        }else {
            respDesc="fail";
        }
        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("requestNo",requestNo);//请求流水号
        respMap.put("version","v1.1");//版本号
        respMap.put("transId",transId);//交易类型

        respMap.put("merNo",merNo);//商户号
        respMap.put("payWay",payWay);//支付渠道
        respMap.put("merNo",merNo);//商户号
        respMap.put("subMchId",subMchId);//二级商户编号


        if(qmap.containsKey("jsApiPath")){
            String jsApiPath= (String) qmap.get("jsApiPath");//授权目录
            respMap.put("jsApiPath",jsApiPath);//授权目录
        }
        if(qmap.containsKey("subAppId")){
            String subAppId=(String) qmap.get("subAppId");//关联APPID
            respMap.put("subAppId",subAppId);//关联APPID
        }
        if(qmap.containsKey("subScribeAppid")){
            String subScribeAppid=(String) qmap.get("subScribeAppid");//授权目录
            respMap.put("subScribeAppid",subScribeAppid);//授权目录
        }

        respMap.put("respCode",respCode);//应答码
        respMap.put("respDesc",respDesc);//应答码描述

// 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);

        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String signatureStr = buf.toString();
        logger.info("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        String signature = null;
        try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
            signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("签名：" + signature );
        respMap.put("signature",signature);
//        resp.getWriter().write(String.valueOf(respMap));
        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );
        out.print(postStr);
    }
}
