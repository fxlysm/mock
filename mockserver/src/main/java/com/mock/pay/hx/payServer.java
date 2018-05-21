package com.mock.pay.hx;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public interface payServer {

    void payResp(Map<String, String> map, PrintWriter out) throws IOException;
}
