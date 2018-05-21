package com.mock.pay.ms.impl;

import com.mock.config.SystemConf;
import com.mock.pay.ms.MSAlipayScanServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Service
public class MSAlipayScanServerImpl implements MSAlipayScanServer{
    private static final Logger logger = LoggerFactory.getLogger(MSAlipayScanServerImpl.class);
    String ZXMOCK= SystemConf.ZXMOCK_MSG;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void payResp(HttpServletResponse resp, Map<String, String> map) throws IOException {

    }

    @Override
    public  void payNotify(Map<String, String> map) throws IOException, InterruptedException {

    }
}
