package com.mock.pay.pf.bill;

import com.mock.pay.pf.impl.PFAlipayMicropayServiceImpl;
import com.mock.sign.ChinaCardPosUtil;
import com.mock.sign.KeyString;
import com.mock.util.SignUtils;
import com.mock.util.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class BillDownloadServerImpl implements BillDownloadServer{

    private static final Logger logger = LoggerFactory.getLogger(BillDownloadServerImpl.class);

    private String privateKey= KeyString.billprivateKey;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public void payResp(Map<String, String> map, PrintWriter out) throws IOException {
        String requestNo=map.get("requestNo");//交易请求流水号
        String version=map.get("version");//版本号
        String transId=map.get("transId");//交易类型
        String agentId=map.get("agentId");//代理商编号
        String orderDate=map.get("orderDate");//对账日期
        String billfile="";
        String fileContentDetail="";

        /*
        由于agentId为440373----海 则这里不加些判断 去掉agentId
         */
//        String command="SELECT * FROM pf_trans_logs WHERE merNo= '"+agentId+"' AND orderDate = '"+orderDate+"'";
        String command="SELECT * FROM pf_trans_logs WHERE  orderDate = '"+orderDate+"' AND status='0'";
        logger.info("Mysql Command:"+command);
        List rows = jdbcTemplate.queryForList(command);
//        for(int i=0;i<rows.size();i++){
//
//            Map <String, String> userMap=rows.get(i);
//
//            System.out.println(userMap.get("id"));
//            System.out.println(userMap.get("name"));
//            System.out.println(userMap.get("age"));
//
//        }

        Iterator it = rows.iterator();
        while(it.hasNext()) {
            Map userMap = (Map) it.next();
            String servertype="";

            String commodityName= String.valueOf(userMap.get("commodityName"));//商品名称
            String transAmt= String.valueOf(userMap.get("transAmt"));//交易金额
            String payresult= String.valueOf(userMap.get("payresult"));//交易状态
            String subMchId= String.valueOf(userMap.get("subMchId"));//二级商户编号
            String pftransId= String.valueOf(userMap.get("pftransId"));//浦发平台流水单号
            String orderNo= String.valueOf(userMap.get("orderNo"));//商户订单号
            String productId= String.valueOf(userMap.get("productId"));//产品类型
            String creattime= String.valueOf(userMap.get("creattime"));//交易时间
            String fee_rate= String.valueOf(userMap.get("fee_rate"));//费率


            String  pfrefundId= String.valueOf(userMap.get("pfrefundId"));//退款平台订单号
            String merNo= String.valueOf(userMap.get("merNo"));//商户号

            String time=creattime.substring(0,creattime.length()-2);

            if(productId.equals("0108")){
                servertype="微信扫码支付";
            }else if(productId.equals("0109")){
                servertype="微信H5支付";
            }else if(productId.equals("0104")){
                servertype="微信APP支付";
            }else if(productId.equals("0112")){
                servertype="微信公众号支付";
            }else if(productId.equals("0119")){
                servertype="支付宝扫码支付";
            }else if(productId.equals("0113")){
                servertype="微信刷卡支付";
            }else if(productId.equals("0120")){
                servertype="支付宝刷卡支付";
            }
//            logger.info("commodityName:"+commodityName+" transAmt:"+transAmt+" payresult:"+payresult+" merNo:"+merNo+" pftransId:"+pftransId+" orderNo:"+orderNo);

            int totalfee=Integer.valueOf(transAmt).intValue();

//            billfile=orderDate+","+subMchId+","+productId+","+servertype+","+pftransId+","+time+","+transAmt+",0,"+transAmt+","+orderNo+","+time+","+"测试桩;\n";
           if(totalfee>0){
               String fee= String.valueOf(userMap.get("fee"));//手续费
               int fees=Integer.valueOf(fee).intValue();
               String earnings=String.valueOf(totalfee-fees);

               billfile=orderDate+","+merNo+","+productId+","+servertype+","+orderNo+","+time+","+transAmt+","+fee+","+earnings+","+pftransId+","+time+","+"测试桩;\n";

           }else {
               String origOrderNo=String.valueOf(userMap.get("origOrderNo"));//费率
               String sql = "SELECT transAmt FROM pf_trans_logs WHERE orderNo = ?";
               String origtransAmt = jdbcTemplate.queryForObject(sql,new Object[] {origOrderNo},String.class);
               int origtotalfee=Integer.valueOf(origtransAmt).intValue();
               String totalfree=String.valueOf(totalfee+origtotalfee);

               billfile=orderDate+","+subMchId+","+productId+","+servertype+","+orderNo+","+time+","+transAmt+",0,"+totalfree+","+pfrefundId+","+time+","+"测试桩;\n";

           }
                              //
            //记账日期+子商户号+支付产品+交易类型+商品订单号+交易时间+交易金额+交易手续费+清算金额+原商品订单号+原交易时间+备注
            fileContentDetail+=billfile;
            String setstatus="UPDATE pf_trans_logs SET status='1'WHERE orderNo='"+orderNo+"'";
//            logger.info("更改状态语句："+setstatus);
            jdbcTemplate.update(setstatus);
        }
        logger.info("对帐单文件内容："+fileContentDetail);
        String fileContentDetails= ZipUtils.gzip(fileContentDetail);
        logger.info("对帐单文件内容压缩后文件："+fileContentDetails);
            //**********
       String respCode="0000";
        String respDesc="success";

        //************** 以下信息为支付回调**************************

        Map<String, String> respMap = new HashMap<String, String>();

        respMap.put("requestNo",requestNo);
        respMap.put("version",version);
        respMap.put("transId",transId);
        respMap.put("agentId",agentId);

        respMap.put("fileContentDetail",fileContentDetails);
        respMap.put("respCode",respCode);
        respMap.put("respDesc",respDesc);



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
