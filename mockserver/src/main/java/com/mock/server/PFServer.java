package com.mock.server;

import com.mock.pay.pf.*;
import com.mock.pay.pf.bill.BillDownloadServer;
import com.mock.pay.pf.report.ReportServer;
import com.mock.pay.pf.report.addMerchantServer;
import com.mock.pay.pf.report.upFileServer;
import com.mock.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RestController
@RequestMapping(value = "/payment-gate-web")
public class PFServer {
    private static final Logger logger = LoggerFactory.getLogger(PFServer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ReportServer reportServer;

    @Autowired
    private addMerchantServer addmerchant;

    @Autowired
    private upFileServer upfile;

    @Autowired
    private PFWechatWapService pfWechath5;

    @Autowired
    private PFWechatScanService pfWechatScan;

    @Autowired
    private PFWechatMicropayService pfWechatMicropay;

    @Autowired
    private PFWechatJSService pfWechatJS;

    @Autowired
    private PFWechatAPPService pfWechatAPP;

    @Autowired
    private  PFAlipayScanService pfAlipayScan;

    @Autowired
    private  PFAlipayMicropayService pfAlipayMicropay;

    @Autowired
    private BillDownloadServer billDownloadServer;

    @Autowired
    private OtherServer otherServer;

//    @ResponseBody
    @RequestMapping(value = "/gateway/api/backTransReq", method = RequestMethod.POST)
    public void getPay(@RequestParam Map<String, String> map2 , HttpServletResponse resp,  PrintWriter out) throws IOException {
//     String mstr= String.valueOf(map);
//      String s2 = mstr.substring(1, mstr.length() - 2);
//      System.out.println(s2);
//        Map<String, String> map2= JsonUtil.jsonToMap(s2);
//        System.out.println(map2);

        logger.debug("接收到参数:" + map2);
        logger.debug("********************************");
        String productId = map2.get("productId");
        String transId = map2.get("transId");
        if (transId.isEmpty()||transId.length()==0){
            out.println("FAIL");
        }else {

                if ("18".equals(transId)){
                    logger.debug("报备商户");
                    reportServer.reportResp(map2,out);

                }else if("19".equals(transId)){
                    logger.debug("查询商户");
                    reportServer.reportquery(map2,out);

                }else if("28".equals(transId)){
                    logger.debug("公众号支付配置接口");
                    otherServer.WechatOa(map2,out);


                }else if("27".equals(transId)){
                    logger.debug("公众号支付配置查询接口");
                    otherServer.WechatOaQuery(map2,out);


                }else if("21".equals(transId)){
                    logger.debug("对账文件接口");
                    billDownloadServer.payResp(map2,out);
                }else if("22".equals(transId)){
                    logger.debug("结算记录下载");

                }else if("14".equals(transId)){
                    logger.debug("单个商户下载对账文件接口");

                }else if("04".equals(transId)){
                    logger.debug("交易状态查询");
                    otherServer.Transactionstatusquery(map2,out);
                }else if("09".equals(transId)){
                    logger.debug("余额查询");
                    otherServer.balancequery(map2,out);
                }else if("02".equals(transId)){
                    logger.debug("退货");
                    otherServer.Refund(map2,out);
                }else if("03".equals(transId)){
                    logger.debug("撤销");
                    otherServer.CloseOrder(map2,out);
                }
                else if("10".equals(transId)&&"0108".equals(productId)){
                    logger.debug("微信扫码支付");
                    pfWechatScan.payResp(map2,out);

                }else if("12".equals(transId)&&"0109".equals(productId)){
                    logger.debug("微信H5支付");
                    pfWechath5.payResp(map2,out);
                }else if("11".equals(transId)&&"0104".equals(productId)){
                    logger.debug("微信APP支付");
                    pfWechatAPP.payResp(map2,out);
                }else if("16".equals(transId)&&"0112".equals(productId)){
                    logger.debug("微信公众号支付");
                    pfWechatJS.payResp(map2,out);
                }else if("17".equals(transId)&&"0113".equals(productId)){
                    logger.debug("微信刷卡支付");
                    pfWechatMicropay.payResp(map2,out);
                }else if("10".equals(transId)&&"0119".equals(productId)){
                    logger.debug("支付宝扫码支付");
                    pfAlipayScan.payResp(map2,out);
                }else if("17".equals(transId)&&"0120".equals(productId)){
                    logger.debug("支付宝刷卡支付");
                    pfAlipayMicropay.payResp(map2,out);
                }else {
                    logger.debug("暂未接入");
                }


        }






    }

    @RequestMapping(value = "/merchant/api/addMerchant", method = RequestMethod.POST)
    public void addMerchant(@RequestParam Map<String, String> map , HttpServletResponse resp,  PrintWriter out) throws IOException {
        String mstr = String.valueOf(map);
        System.out.println("map"+map);
        String s2 = mstr.substring(1, mstr.length() - 2);
        System.out.println(s2);
        Map<String, String> map2 = JsonUtil.jsonToMap(s2);
        System.out.println(map2);

        logger.debug("MAP:" + map2);
        String transId = map2.get("transId");
        if (transId.isEmpty() || transId.length() == 0) {
            out.println("FAIL");
        } else {
            if("25".equals(transId)){
                logger.debug("商户报件");
                addmerchant.addMerchant(resp,map2);

            }else {

                out.println("Fail");
            }

        }

    }


    @RequestMapping(value = "/upload/api/upFile", method = RequestMethod.POST)
    public void upFile(@RequestParam Map<String, String> map , HttpServletResponse resp,  PrintWriter out) throws IOException {
        String mstr = String.valueOf(map);
        String s2 = mstr.substring(1, mstr.length() - 2);
        System.out.println(s2);
        Map<String, String> map2 = JsonUtil.jsonToMap(s2);
        System.out.println(map2);

        logger.debug("MAP:" + map2 + "\nResp" + resp);
        String transId = map2.get("transId");
        if (transId.isEmpty() || transId.length() == 0) {
            out.println("FAIL");
        } else {
            if("26".equals(transId)){
                logger.debug("商户报件上传");
                upfile.upFile(resp,map2);
            }else {
                out.println("Fail");
            }

        }

    }


    @RequestMapping(value = "/gateway/api/backTransReq")
    public void testRetry() throws Exception {
//        Service.service();
    }



}
