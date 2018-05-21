package com.mock.action;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.mock.config.SystemConf;
import com.mock.pay.hx.HXNoticeServer;
import com.mock.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Component
public class RedisHXServer {

    private static final Logger logger = LoggerFactory.getLogger(RedisHXServer.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    String HXMOCK= SystemConf.HXMOCK_MSG;

    @Autowired
    private HXNoticeServer hxNoticeServer;

    @Scheduled(cron = "*/10 * * * * ?")
    public  void payNotify()   {
        int count= redisTemplate.opsForHash().keys(HXMOCK).size();
        logger.debug("**********读取海峡订单缓存个数："+count+"**********************");
        System.out.println("**********读取海峡订单缓存个数："+count+"**********************");
        String haskkey= StringUtil.getCode(8,0);
        String value=StringUtil.getRandomString(20);
//        redisTemplate.opsForHash().put(PFMOCK_MSG,haskkey,value);
        Set<Object> set  =redisTemplate.opsForHash().keys(HXMOCK);
        Iterator<Object> it = set.iterator();
        while (it.hasNext()){
            Object ob =  it.next();
            logger.debug("正在处理订单："+ob);
//            Object v = redisTemplate.opsForValue().get(ob);
//            System.out.println("key:" +ob +", value:" +v);
            Object v=   redisTemplate.opsForHash().get(HXMOCK,ob);
//            System.out.println("key:" +ob +", value:" +v);
//            redisTemplate.opsForHash().delete(PFMOCK,ob);

            JSONObject jsonObject = JSONObject.parseObject((String) v);
            Map<String, String> map = JSONObject.parseObject(jsonObject.toJSONString(),
                    new TypeReference<Map<String, String>>() {
                    });
            System.out.println(map);

            try {
                if(map.get("ret_code").equals("0")){
                    hxNoticeServer.payNotify(map);

                    logger.debug("订单处理完成"+ob);
                }else {
                    logger.debug("订单支付code为非0000，不做通知处理");
                }

                redisTemplate.opsForHash().delete(HXMOCK,ob);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }



    }
}
