package com.mock.pay.pf.report;

import com.mock.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class addMerchantServerImpl implements addMerchantServer{
    private static final Logger logger = LoggerFactory.getLogger(addMerchantServerImpl.class);

    @Override
    public void addMerchant(HttpServletResponse resp, Map<String, String> map) throws IOException {
        String requestNo=map.get("requestNo");
        String version=map.get("version");
        String transId=map.get("transId");
        String merNo= StringUtil.getCode(12,0);
        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("requestNo",requestNo);
        respMap.put("version",version);
        respMap.put("transId",transId);
        respMap.put("merNo",merNo);

        resp.getWriter().write(String.valueOf(respMap));



    }

}
