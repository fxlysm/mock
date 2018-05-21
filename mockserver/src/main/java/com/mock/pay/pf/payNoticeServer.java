package com.mock.pay.pf;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface payNoticeServer {
    void  payNotify(Map<String, String> map) throws IOException, InterruptedException;
    void  RefundpayNotify(Map<String, String> map) throws IOException, InterruptedException;
}
