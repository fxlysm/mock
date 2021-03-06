package com.mock.pay.zx.wechat;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface WechatAppService {
    void payResp(HttpServletResponse resp, Map<String, String> map) throws IOException;
    void payNotify(Map<String, String> map) throws IOException, InterruptedException;
}
