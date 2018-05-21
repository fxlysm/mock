package com.mock.pay.pf.report;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface addMerchantServer {
    void addMerchant(HttpServletResponse resp, Map<String, String> map) throws IOException;
}
