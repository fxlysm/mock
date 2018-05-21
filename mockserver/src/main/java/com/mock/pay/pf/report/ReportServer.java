package com.mock.pay.pf.report;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public interface ReportServer {
    void reportResp(Map<String, String> map,PrintWriter out) throws IOException;
    void reportquery( Map<String, String> map,PrintWriter out) throws IOException;
}
