package com.mock.pay.pf.bill;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public interface BillDownloadServer {
    void payResp(Map<String, String> map, PrintWriter out) throws IOException;
}
