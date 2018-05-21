package com.mock.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.util.Map;

//@RestController
//@RequestMapping(value = "/nbp-smzf-hzf")
public class MSServer {
    private static final Logger logger = LoggerFactory.getLogger(MSServer.class);

//    @Autowired
//    private Service service1;

    @ResponseBody
    @RequestMapping(value = "/gateway", method = RequestMethod.POST)
    public void getPay(@RequestParam Map<String, String> map , PrintWriter out){
        logger.debug("MAP:"+map);
        String service=map.get("service");
        if("pay.alipay.native".equals(service)){
            logger.debug("************接收到支付宝扫码支付**********************");
//            AliScanpay.PayReq(map,out);
        }else if("pay.weixin.native".equals(service)){
            logger.debug("************接收到微信扫码支付**********************");
//            WechatScanPay.PayReq(map,out);
        }else {
            logger.debug("************未加入到测试桩**********************");
            System.out.println("************未加入到测试桩**********************");
        }
//        out.print("get is"+map);

    }


    @RequestMapping(value = "/gateway/api/backTransReq")
    public void testRetry() throws Exception {
//        Service.service();
    }
}

