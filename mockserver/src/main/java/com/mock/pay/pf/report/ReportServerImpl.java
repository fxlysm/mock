package com.mock.pay.pf.report;

import com.mock.config.SystemConf;
import com.mock.mysql.PFSqlServer;
import com.mock.mysql.PFSqlServerimpl;
import com.mock.sign.ChinaCardPosUtil;
import com.mock.sign.KeyString;
import com.mock.sign.RSA;

import com.mock.util.SignUtils;
import com.mock.util.StringUtil;
import com.mock.util.XmlUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServerImpl implements ReportServer{

    private static final Logger logger = LoggerFactory.getLogger(ReportServerImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

//    @Value("${pufa.rsa.private_key}")
    private String privateKey= KeyString.privateKey;

    @Autowired
    private PFSqlServer pfSqlServer;

    @Override
    public void reportResp( Map<String, String> map,PrintWriter out) throws IOException {
        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型
        String payWay=map.get("payWay");//WX-微信报备,ALIPAY-支付宝报备
        String merNo=map.get("merNo");//商户号
        String subMechantName=map.get("subMechantName");//二级商户名称
        String subMerchantShortname=map.get("subMerchantShortname");//二级商户简称
        String contact=map.get("contact");
        String contactPhone=map.get("contactPhone");
        String contactEmail=map.get("contactEmail");
        String merchantRemark=map.get("merchantRemark");
        String servicePhone=map.get("servicePhone");
        String business=map.get("business");

        String subMchId= StringUtil.getStringDate("yyyyMMddHH")+StringUtil.getCode(4,0);


        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("requestNo",requestNo);
        respMap.put("version",version);
        respMap.put("transId",transId);
        respMap.put("payWay",payWay);
        respMap.put("merNo",merNo);
        respMap.put("subMchId",subMchId);//二级商户编号
        respMap.put("subMechantName",subMechantName);
        respMap.put("business",business);//经营类目
        respMap.put("subMerchantShortname",subMerchantShortname);
        respMap.put("contact",contact);
        respMap.put("contactPhone",contactPhone);
        respMap.put("contactEmail",contactEmail);
        respMap.put("merchantRemark",merchantRemark);
        respMap.put("respCode","0000");
        respMap.put("respDesc","0000");

        String commandString="INSERT INTO pf_report_logs (requestNo,version,transId,payWay,merNo,subMchId,subMechantName,subMerchantShortname,contact,contactPhone,contactEmail,merchantRemark,respCode,respDesc)" +
                "VALUES('"+requestNo+"','"+version+"','"+transId+"','"+payWay+"','"+merNo+"','"+subMchId+"','"+subMechantName+"','"+subMerchantShortname+"','"+contact+"','"+contactPhone+"','"+contactEmail+"','"+merchantRemark+"','0000','susses')";
//        pfSqlServer.InsertReport(respMap);
        jdbcTemplate.execute(commandString);

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

        logger.info("回调参数MAP：" + respMap );
//        out.print(respMap);
//        resp.getWriter().write(String.valueOf(respMap));
        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );
        out.print(postStr);
    }

    @Override
    public void reportquery( Map<String, String> map,PrintWriter out) throws IOException {
        String subMchId=map.get("subMchId");
       // Map<String, String> comMap =pfSqlServer.GetReportLogs(map.get("subMechantName"));
        String commandString="SELECT * FROM pf_report_logs WHERE subMchId='"+subMchId+"'";
        Map<String, Object> comMap =jdbcTemplate.queryForMap(commandString);
        System.out.println("Mysql Map"+jdbcTemplate.queryForMap(commandString));
        logger.debug("Mysql Map"+jdbcTemplate.queryForMap(commandString));
        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型
        String payWay=map.get("payWay");//WX-微信报备,ALIPAY-支付宝报备
        String merNo=map.get("merNo");//商户号
        String subMechantName=map.get("subMechantName");//二级商户名称

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("requestNo",requestNo);
        respMap.put("version",version);
        respMap.put("transId",transId);
        respMap.put("payWay",payWay);
        respMap.put("merNo",merNo);
        respMap.put("subMchId",subMchId);//二级商户编号
        respMap.put("subMechantName",subMechantName);
        respMap.put("respDesc", String.valueOf(comMap.get("respDesc")));

        // 过滤map
        Map<String, String> params = SignUtils.paraFilter(respMap);
        if (params.containsKey("sign"))
            params.remove("sign");
        StringBuilder buf = new StringBuilder((params.size() + 1) * 10);
        SignUtils.buildPayParams(buf, params, false);
        String signatureStr = buf.toString();
        logger.info("预签名字串：" + signatureStr );
//        System.out.println("预签名字串：" + signatureStr );
//        String signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        logger.info("预签名私钥："+privateKey);
        String signature= null;
        try {
//            signature = RSAUtil.signByPrivate(signatureStr, privateKey,  "UTF-8");
            signature = ChinaCardPosUtil.signByPrivate(signatureStr, privateKey, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("签名：" + signature );
        respMap.put("signature",signature);

        logger.info("回调参数MAP：" + respMap );
//        out.print(respMap);
//        resp.getWriter().write(String.valueOf(respMap));

        Map<String, String> postparams = SignUtils.paraFilter(respMap);
        StringBuilder postbuf = new StringBuilder((postparams.size() + 1) * 10);
        SignUtils.buildPayParams(postbuf, postparams, false);
        String postStr = postbuf.toString();
        logger.info("返回参数：" + postStr );
        out.print(postStr);


    }




}
