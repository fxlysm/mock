package com.mock.pay.pf.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class upFileServerImpl implements upFileServer {

    private static final Logger logger = LoggerFactory.getLogger(upFileServerImpl.class);

    @Override
    public void upFile(HttpServletResponse resp, Map<String, String> map) throws IOException {
        String respCode="0000";
        String respDesc="success";

        Map<String, String> respMap = new HashMap<String, String>();
        respMap.put("respCode",respCode);
        respMap.put("respDesc",respDesc);


        resp.getWriter().write(String.valueOf(respMap));



    }
}
