package com.mock.mysql;

import java.util.Map;

public interface PFSqlServer {
    Map<String, String> GetReportLogs(String subMchId);
    void InsertReport(Map<String, String> map);
}
