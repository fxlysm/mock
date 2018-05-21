package com.mock.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.util.Map;

//@RestController
//
@RequestMapping(value = "/service")
public class JDServer {
    private static final Logger logger = LoggerFactory.getLogger(JDServer.class);

//    @Autowired
//    private Service service1;

//    payconfig.jdUniorderUrl = https://paygate.jd.com/service/uniorder
//    payconfig.jdRefundUrl = https://paygate.jd.com/service/refund
//    payconfig.jdQueryUrl = https://paygate.jd.com/service/query
//    payconfig.jdCancelUrl = https://paygate.jd.com/service/revoke
//    payconfig.jdPcPayUrl = https://wepay.jd.com/jdpay/saveOrder

    @ResponseBody
    @RequestMapping(value = "/uniorder", method = RequestMethod.POST)
    public void getPay(@RequestParam Map<String, String> map , PrintWriter out){
        logger.debug("MAP:"+map);
        logger.debug("************接收到京东统一下单接口**********************");
        String service=map.get("service");



    }

    @ResponseBody
    @RequestMapping(value = "/refund", method = RequestMethod.POST)
    public void getrefund(@RequestParam Map<String, String> map , PrintWriter out){
        logger.debug("MAP:"+map);
        logger.debug("************接收到京东退款**********************");
        String service=map.get("service");



    }

    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public void getquery(@RequestParam Map<String, String> map , PrintWriter out){
        logger.debug("MAP:"+map);
        logger.debug("************接收到京东退款**********************");
        String service=map.get("service");



    }

    @ResponseBody
    @RequestMapping(value = "/revoke", method = RequestMethod.POST)
    public void getrevoke(@RequestParam Map<String, String> map , PrintWriter out){
        logger.debug("MAP:"+map);
        logger.debug("************接收到京东退款**********************");
        String service=map.get("service");



    }
    @ResponseBody
    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST)
    public void gesaveOrder(@RequestParam Map<String, String> map , PrintWriter out){
        logger.debug("MAP:"+map);
        logger.debug("************接收到京东退款**********************");
        String service=map.get("service");



    }
}
