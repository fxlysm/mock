package com.mock.paySchedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
public class SentPayServer {
    private static final Logger logger = LoggerFactory.getLogger(SentPayServer.class);

    @Autowired
    private heilpay hipay;

    @Scheduled(cron = "*/5 * * * * ?")
    public  void Sentpay()   {
        logger.debug("**********正在发送支付**********************");
        hipay.wechatscan();
    }
}
