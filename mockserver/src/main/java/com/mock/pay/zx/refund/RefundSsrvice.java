package com.mock.pay.zx.refund;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface RefundSsrvice {
    void RefundqueryResp(HttpServletResponse resp, Map<String, String> map) throws IOException;
    void RefundPayResp(HttpServletResponse resp, Map<String, String> map) throws IOException;

    void Payreverse(HttpServletResponse resp, Map<String, String> map);
}
