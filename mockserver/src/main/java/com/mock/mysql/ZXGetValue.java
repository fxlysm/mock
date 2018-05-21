package com.mock.mysql;

import com.mock.config.MySqlConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class ZXGetValue {
    private static final Logger logger = LoggerFactory.getLogger(ZXGetValue.class);
    public static Map<String, String> GetValue_Req(String servername){
        Map<String, String> cacheMap = new HashMap<String, String>();
        String sql_command="SELECT * FROM pay_req WHERE ServerName='"+servername+"'";
        logger.debug(sql_command);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
//            , true, ScanPayGetValue.class.getClass().getClassLoader()
//            System.out.println("成功加载MySQL驱动！");
//            logger.debug("成功加载MySQL驱动");
            Connection conn;
            conn = DriverManager.getConnection(MySqlConf.MOCK_JDBC_URL, MySqlConf.MOCK_MYSQL_Account, MySqlConf.MOCK_MYSQL_PWD);  //
            Statement stmt = conn.createStatement(); //创建Statement对象
//            System.out.println("成功连接到数据库！");
//            logger.debug("成功连接到数据库");
            ResultSet rs = stmt.executeQuery(sql_command);//创建数据对象
            while (rs.next()){
                cacheMap.put("version", rs.getString("version"));
                cacheMap.put("charset", rs.getString("charset"));
                cacheMap.put("sign_type", rs.getString("sign_type"));
                cacheMap.put("status", rs.getString("status"));
                cacheMap.put("result_code", rs.getString("result_code"));
                cacheMap.put("mch_id", rs.getString("mch_id"));
                cacheMap.put("device_info", rs.getString("device_info"));
                cacheMap.put("nonce_str", rs.getString("nonce_str"));
                cacheMap.put("code_url", rs.getString("code_url"));
                cacheMap.put("code_img_url", rs.getString("code_img_url"));
                cacheMap.put("mch_id_iswork", rs.getString("mch_id_iswork"));
                cacheMap.put("device_info_iswork", rs.getString("device_info_iswork"));
                cacheMap.put("nonce_str_iswork", rs.getString("nonce_str_iswork"));
                cacheMap.put("signkey", rs.getString("signkey"));
                cacheMap.put("key_iswork", rs.getString("key_iswork"));
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
//		System.out.println(cacheMap);
        return cacheMap;
    }



    public static Map<String, String> GetValue_Notice(String servername){
        Map<String, String> cacheMap = new HashMap<String, String>();
        String sql_command="SELECT * FROM notify_req WHERE servername='"+servername+"'";
        logger.debug("*********正在读取159数据库****************");
        logger.debug(sql_command);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
//            , true, ScanPayGetValue.class.getClass().getClassLoader()
//            System.out.println("成功加载MySQL驱动！");
//            logger.debug("成功加载MySQL驱动");
            Connection conn;
            conn = DriverManager.getConnection(MySqlConf.MOCK_JDBC_URL, MySqlConf.MOCK_MYSQL_Account, MySqlConf.MOCK_MYSQL_PWD);  //
            Statement stmt = conn.createStatement(); //创建Statement对象
//            System.out.println("成功连接到数据库！");
//            logger.debug("成功连接到数据库");

            ResultSet rs = stmt.executeQuery(sql_command);//创建数据对象
            while (rs.next()){
                cacheMap.put("version", rs.getString("version"));
                cacheMap.put("charset", rs.getString("charset"));
                cacheMap.put("sign_type", rs.getString("sign_type"));
                cacheMap.put("status", rs.getString("status"));
                cacheMap.put("result_code", rs.getString("result_code"));
                cacheMap.put("trade_type", rs.getString("trade_type"));
                cacheMap.put("fee_type", rs.getString("fee_type"));
                cacheMap.put("pay_result", rs.getString("pay_result"));
                cacheMap.put("nonce_str", rs.getString("nonce_str"));
                cacheMap.put("nonce_str_iswork", rs.getString("nonce_str_iswork"));
                cacheMap.put("mch_id", rs.getString("mch_id"));
                cacheMap.put("mch_id_iswork", rs.getString("mch_id_iswork"));
                cacheMap.put("transaction_id", rs.getString("transaction_id"));
                cacheMap.put("transaction_id_iswork", rs.getString("transaction_id_iswork"));
                cacheMap.put("out_trade_no", rs.getString("out_trade_no"));
                cacheMap.put("out_trade_no_iswork", rs.getString("out_trade_no_iswork"));
                cacheMap.put("device_info", rs.getString("device_info"));
                cacheMap.put("device_info_iswork", rs.getString("device_info_iswork"));
                cacheMap.put("total_fee", rs.getString("total_fee"));
                cacheMap.put("total_fee_iswork", rs.getString("total_fee_iswork"));
                cacheMap.put("signkey", rs.getString("signkey"));
                cacheMap.put("key_iswork", rs.getString("key_iswork"));

            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
//		System.out.println(cacheMap);
        return cacheMap;
    }






    public static String getKeyWithmichId(String mch_id){
        String keyString="";
        String purpose="json_extract(data,'$.payKey')";
        String command="select "+purpose+" from cp_info_mapping where CP_ID_THIRD='"+mch_id+"' and STATUS='1'";
//		System.out.println(command);
        logger.debug(command);
//		json_extract(data,'$.mchId')
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn;
            conn = DriverManager.getConnection(MySqlConf.TEST_JDBC_URL, MySqlConf.TEST_MYSQL_Account, MySqlConf.TEST_MYSQL_PWD);  //
            Statement stmt = conn.createStatement(); //创建Statement对象
            ResultSet rs = stmt.executeQuery(command);//创建数据对象
            while (rs.next()){
                keyString=rs.getString(purpose);
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        String newstr = keyString.replace("\"","");

        return newstr;
    }

    public static Map<String, String> GetValue_Refound(String servername){
        Map<String, String> cacheMap = new HashMap<String, String>();
        String sql_command="SELECT * FROM refund WHERE ServerName='"+servername+"'";
        logger.debug(sql_command);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
//            , true, ScanPayGetValue.class.getClass().getClassLoader()
//            System.out.println("成功加载MySQL驱动！");
//            logger.debug("成功加载MySQL驱动");
            Connection conn;
            conn = DriverManager.getConnection(MySqlConf.MOCK_JDBC_URL, MySqlConf.MOCK_MYSQL_Account, MySqlConf.MOCK_MYSQL_PWD);  //
            Statement stmt = conn.createStatement(); //创建Statement对象
//            System.out.println("成功连接到数据库！");
//            logger.debug("成功连接到数据库");
            ResultSet rs = stmt.executeQuery(sql_command);//创建数据对象
            while (rs.next()){
                cacheMap.put("version", rs.getString("version"));
                cacheMap.put("charset", rs.getString("charset"));
                cacheMap.put("sign_type", rs.getString("sign_type"));
                cacheMap.put("status", rs.getString("status"));
                cacheMap.put("result_code", rs.getString("result_code"));
                cacheMap.put("mch_id", rs.getString("mch_id"));
//                	cacheMap.put("device_info", rs.getString("device_info"));
                cacheMap.put("nonce_str", rs.getString("nonce_str"));
                cacheMap.put("transaction_id", rs.getString("transaction_id"));
                cacheMap.put("out_trade_no", rs.getString("out_trade_no"));
                cacheMap.put("out_refund_no", rs.getString("out_refund_no"));
                cacheMap.put("refund_id", rs.getString("refund_id"));
                cacheMap.put("refund_channel", rs.getString("refund_channel"));
                cacheMap.put("refund_fee", rs.getString("refund_fee"));
                cacheMap.put("mch_id_iswork", rs.getString("mch_id_iswork"));
                cacheMap.put("nonce_str_iswork", rs.getString("nonce_str_iswork"));
                cacheMap.put("transaction_id_iswork", rs.getString("transaction_id_iswork"));
                cacheMap.put("out_trade_no_iswork", rs.getString("out_trade_no_iswork"));
                cacheMap.put("out_refund_no_iswork", rs.getString("out_refund_no_iswork"));
                cacheMap.put("refund_id_iswork", rs.getString("refund_id_iswork"));
                cacheMap.put("refund_fee_iswork", rs.getString("refund_fee_iswork"));
                cacheMap.put("signkey", rs.getString("signkey"));
                cacheMap.put("key_iswork", rs.getString("key_iswork"));
            }
            rs.close();
            stmt.close();
            conn.close();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
//		System.out.println(cacheMap);
        return cacheMap;
    }

}
