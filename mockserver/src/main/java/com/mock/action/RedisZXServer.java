package com.mock.action;


import com.mock.config.SystemConf;
import com.mock.pay.zx.alipay.AlipayJsService;
import com.mock.pay.zx.alipay.AlipayScanService;
import com.mock.pay.zx.jd.JDScanService;
import com.mock.pay.zx.oldpaycenter.PayCenterService;
import com.mock.pay.zx.qq.QQScanService;
import com.mock.pay.zx.wechat.WechatAppService;
import com.mock.pay.zx.wechat.WechatJSService;
import com.mock.pay.zx.wechat.WechatScanService;
import com.mock.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class RedisZXServer {
    private static final Logger logger = LoggerFactory.getLogger(RedisZXServer.class);
    @Resource
    private  RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AlipayScanService alipayScanService;

    @Autowired
    private AlipayJsService alipayJsService;

    @Autowired
    private WechatScanService wechatScanService;

    @Autowired
    private WechatAppService wechatAppService;

    @Autowired
    private WechatJSService wechatJSService;

    @Autowired
    private QQScanService qqScanService;

    @Autowired
    private JDScanService jdScanService;

    @Autowired
    private PayCenterService payCenterService;

    @Scheduled(cron = "*/15 * * * * ?")
    public  void payNotify() throws IOException, InterruptedException {
       int count= redisTemplate.opsForHash().keys(SystemConf.ZXMOCK_MSG).size();
   //     System.out.println("**********读取中信订单缓存个数："+count+"**********************");
        logger.debug("**********读取中信订单缓存个数："+count+"**********************");
        String haskkey= StringUtil.getCode(8,0);
        String value=StringUtil.getRandomString(20);
//        redisTemplate.opsForHash().put(ZXMOCK_MSG,haskkey,value);
        Set<Object> set  =redisTemplate.opsForHash().keys(SystemConf.ZXMOCK_MSG);
        Iterator<Object> it = set.iterator();
        while (it.hasNext()){
            Object ob =  it.next();
            logger.debug("正在处理订单："+ob);
//            Object v = redisTemplate.opsForValue().get(ob);
//            System.out.println("key:" +ob +", value:" +v);
            Object v=   redisTemplate.opsForHash().get(SystemConf.ZXMOCK_MSG,ob);
            JSONObject jsonObject = JSONObject.parseObject((String) v);
            Map<String, String> map = JSONObject.parseObject(jsonObject.toJSONString(),
                    new TypeReference<Map<String, String>>() {
                    });
            System.out.println(map);
            String server=map.get("service");
            if ("pay.alipay.native".equals(server)) {
                if (map.containsKey("environmental")) {// 添加老的支付平台接入判断
                    logger.debug("***老支付中心----通知回调");
                    payCenterService.payNotify(map);
                }else {
                    if ("0".equals(map.get("pay_status")) && "0".equals(map.get("pay_result_code"))) {
                        alipayScanService.payNotify(map);
                    }else {
                        logger.debug("支付响应状态已失败！不再回调支付通知！");
                    }

                }
            }else if("pay.alipay.jspay".equals(server)){
                if ("0".equals(map.get("pay_status")) && "0".equals(map.get("pay_result_code"))) {
                    alipayJsService.payNotify(map);
                }else {
                    logger.debug("支付响应状态已失败！不再回调支付通知！");
                }

            }else if("pay.weixin.native".equals(server)){
                if (map.containsKey("environmental")) {// 添加老的支付平台接入判断
                    logger.debug("***老支付中心----通知回调");
                    payCenterService.payNotify(map);
                }else {
                    if ("0".equals(map.get("pay_status")) && "0".equals(map.get("pay_result_code"))) {
                        wechatScanService.payNotify(map);
                    }else {
                        logger.debug("支付响应状态已失败！不再回调支付通知！");
                    }

                }
            }else if("pay.weixin.jspay".equals(server)){
                if ("0".equals(map.get("pay_status")) && "0".equals(map.get("pay_result_code"))) {
                    wechatJSService.payNotify(map);
                }else {
                    logger.debug("支付响应状态已失败！不再回调支付通知！");
                }

            }else if("pay.tenpay.native".equals(server)){
                if ("0".equals(map.get("pay_status")) && "0".equals(map.get("pay_result_code"))) {
                    qqScanService.payNotify(map);
                }else {
                    logger.debug("支付响应状态已失败！不再回调支付通知！");
                }

            }else if("pay.weixin.raw.app".equals(server)){
                if ("0".equals(map.get("pay_status")) && "0".equals(map.get("pay_result_code"))) {
                    wechatAppService.payNotify(map);
                }else {
                    logger.debug("支付响应状态已失败！不再回调支付通知！");
                }

            }else if("pay.jdpay.native".equals(server)){
                if ("0".equals(map.get("pay_status")) && "0".equals(map.get("pay_result_code"))) {
                    jdScanService.payNotify(map);
                }else {
                    logger.debug("支付响应状态已失败！不再回调支付通知！");
                }

            }
            else {

            }

            System.out.println("key:" +ob +", value:" +v);
            redisTemplate.opsForHash().delete(SystemConf.ZXMOCK_MSG,ob);
        }


    }
}
