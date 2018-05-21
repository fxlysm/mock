package com.mock.pay.pf.report;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public interface upFileServer {
    void upFile(HttpServletResponse resp, Map<String, String> map) throws IOException;
}
