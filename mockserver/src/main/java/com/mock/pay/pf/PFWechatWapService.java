package com.mock.pay.pf;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public interface PFWechatWapService {
    void payResp( Map<String, String> map,  PrintWriter out) throws IOException;
}
