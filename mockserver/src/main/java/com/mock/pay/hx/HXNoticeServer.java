package com.mock.pay.hx;

import java.io.IOException;
import java.util.Map;

public interface HXNoticeServer {
    void  payNotify(Map<String, String> map) throws IOException, InterruptedException;
}
