package com.mock.pay.zx.close;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface CloseService {
    void orderclosereq(HttpServletResponse resp, Map<String, String> map) throws IOException;
}
