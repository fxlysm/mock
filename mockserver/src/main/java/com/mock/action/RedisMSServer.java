package com.mock.action;

import  com.mock.util.StringUtil;
import com.mock.config.SystemConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.Set;

//@Component
public class RedisMSServer {
    private static final Logger logger = LoggerFactory.getLogger(RedisMSServer.class);

    @Resource
    private  RedisTemplate<String, Object> redisTemplate;


    @Scheduled(cron = "*/15 * * * * ?")
    public  void payNotify()   {
        int count= redisTemplate.opsForHash().keys(SystemConf.MSMOCK_MSG).size();
        System.out.println("**********读取民生订单缓存个数："+count+"**********************");
        logger.debug("**********读取民生订单缓存个数："+count+"**********************");

        String haskkey= StringUtil.getCode(8,0);
        String value=StringUtil.getRandomString(20);
//        redisTemplate.opsForHash().put(MSMOCK_MSG,haskkey,value);
        Set<Object> set  =redisTemplate.opsForHash().keys(SystemConf.MSMOCK_MSG);
        Iterator<Object>  it = set.iterator();
        while (it.hasNext()){
           Object ob =  it.next();
           logger.debug("正在处理订单："+ob);
//            Object v = redisTemplate.opsForValue().get(ob);
//            System.out.println("key:" +ob +", value:" +v);
         Object v=   redisTemplate.opsForHash().get(SystemConf.MSMOCK_MSG,ob);
            System.out.println("key:" +ob +", value:" +v);
//            redisTemplate.opsForHash().delete(mokesmg,ob);
            redisTemplate.opsForHash().delete(SystemConf.MSMOCK_MSG,ob);
        }



    }
}
