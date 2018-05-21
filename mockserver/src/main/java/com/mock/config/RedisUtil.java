package com.mock.config;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class RedisUtil {

    @Resource
    private  RedisTemplate<String, Object> redisTemplate;

    public  void  SetKey(String key,String haskkey,String  msg){
        redisTemplate.opsForHash().put(key,haskkey,msg);
    }

}
