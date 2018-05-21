//package com.mock.server;
//
//import com.mock.pay.zx.alipay.AlipayJsService;
//import com.mock.pay.zx.alipay.AlipayScanService;
//import com.mock.pay.zx.close.CloseService;
//import com.mock.pay.zx.jd.JDScanService;
//import com.mock.pay.zx.oldpaycenter.PayCenterService;
//import com.mock.pay.zx.qq.QQScanService;
//import com.mock.pay.zx.refund.RefundSsrvice;
//import com.mock.pay.zx.wechat.WechatAppService;
//import com.mock.pay.zx.wechat.WechatJSService;
//import com.mock.pay.zx.wechat.WechatScanService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.Map;
//
//@Controller
//@RequestMapping(value = "/pay")
//public class IndexServer {
//    private static final Logger logger = LoggerFactory.getLogger(IndexServer.class);
//    @Autowired
//    private checkserver service1;
//    //    @Autowired
////    private Service service1;
//    @Autowired
//    private PayCenterService payCenterService;
//
//    @Autowired
//    private AlipayScanService alipayScanService;
//
//    @Autowired
//    private AlipayJsService alipayJsService;
//
//    @Autowired
//    private WechatScanService wechatScanService;
//
//    @Autowired
//    private WechatAppService wechatAppService;
//
//    @Autowired
//    private WechatJSService wechatJSService;
//
//    @Autowired
//    private QQScanService qqScanService;
//
//    @Autowired
//    private JDScanService jdScanService;
//
//    @Autowired
//    private RefundSsrvice refundSsrvice;
//
//    @Autowired
//    private CloseService closeService;
//
//    @RequestMapping(value = "/go")
//    public String index(){
//        return "index";
//    }
//
//    @RequestMapping(value = "/gateway" , method = RequestMethod.POST)
//    public void getPay(@RequestParam Map<String, String> map , HttpServletResponse resp, PrintWriter out) throws IOException {
//        logger.debug("MAP:"+map);
//        String service=map.get("service");
//        if("pay.alipay.native".equals(service)){
//
//            if (map.containsKey("environmental")) {
//                logger.debug("************接收到Paycenter 旧支付中心-----[中信]支付宝扫码支付**********************");
//                payCenterService.payResp(resp,map);
//            }else {
//                logger.debug("************接收到[中信]支付宝扫码支付**********************");
//                alipayScanService.payResp(resp,map);
//            }
//        }else if("pay.weixin.native".equals(service)){
//
//            if (map.containsKey("environmental")) {
//                logger.debug("************接收到Paycenter 旧支付中心-----[中信]微信扫码支付**********************");
//                payCenterService.payResp(resp,map);
//            }else {
//                logger.debug("************接收到[中信]微信扫码支付**********************");
//                wechatScanService.payResp(resp, map);
//            }
//        }else if("pay.weixin.jspay".equals(service)){
//            logger.debug("************接收到[中信]微信公众号支付&小程序支付**********************");
//            wechatJSService.payResp(resp,map);
//        } else if("pay.tenpay.native".equals(service)){
//            logger.debug("************接收到[中信]QQ扫码支付**********************");
//            qqScanService.payResp(resp,map);
//        }else if("unified.trade.pay".equals(service) || "pay.weixin.raw.app".equals(service)){
//            logger.debug("************接收到[中信]微信APP支付**********************");
//            wechatAppService.payResp(resp,map);
//        }else if("pay.alipay.jspay".equals(service)){
//            logger.debug("************接收到[中信]支付宝服务窗**********************");
//            alipayJsService.payResp(resp,map);
//        }else if("pay.jdpay.native".equals(service)){
//            logger.debug("************接收到[中信]JD扫码支付**********************");
//            jdScanService.payResp(resp,map);
//        }
//        else if("unified.trade.refundquery".equals(service)){
//            logger.debug("************接收到[中信]退款结果查询请求**********************");
//            refundSsrvice.RefundqueryResp(resp,map);
//        }else if("unified.trade.refund".equals(service)){
//            logger.debug("************接收到[中信]撤销订单请求**********************");
//            refundSsrvice.RefundPayResp(resp,map);
//        }else if("unified.micropay.reverse".equals(service)){
//            logger.debug("************接收到[中信]撤销订单支付**********************");
//            refundSsrvice.Payreverse(resp,map);
//        }else if("unified.trade.close".equals(service)){
//            logger.debug("************接收到[中信]关闭订单支付**********************");
//            closeService.orderclosereq(resp,map);
//        }
//
//        else {
//            logger.error("请求服务不存在.......service=" + service);
//            logger.debug("map:" + map);
////            System.out.println("************未加入到测试桩**********************");
//
//        }
////        out.print("get is"+map);
//        logger.debug("*****************请求完成*****************");
//
//    }
//
//}
