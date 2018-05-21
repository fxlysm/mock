package com.mock.pay.hx;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public interface hxbilldownServer {
    void  billdownload(Map<String, String> map,  HttpServletResponse resp) throws IOException, InterruptedException;
}
