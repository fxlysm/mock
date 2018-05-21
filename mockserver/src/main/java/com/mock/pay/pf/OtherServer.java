package com.mock.pay.pf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public interface OtherServer {
    void  balancequery( Map<String, String> map,PrintWriter out) throws IOException;

    void  Transactionstatusquery( Map<String, String> map,PrintWriter out) throws IOException;
    void  Refund( Map<String, String> map,PrintWriter out) throws IOException;
    void  CloseOrder( Map<String, String> map,PrintWriter out) throws IOException;
    void  WechatOa( Map<String, String> map,PrintWriter out) throws IOException;
    void  WechatOaQuery( Map<String, String> map,PrintWriter out) throws IOException;
}
