package com.mock.mysql;

import com.mock.config.MySqlConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class PFSqlServerimpl implements PFSqlServer{
    private static final Logger logger = LoggerFactory.getLogger(PFSqlServerimpl.class);

    @Value("${mockserver.mysql.url}")
    private  String URL;

    @Value("${mockserver.mysql.account}")
    private  String ACCOUNT;

    @Value("${mockserver.mysql.password}")
    private  String PASSWORD;

    public   Map<String, String> GetReportLogs(String subMchId){
        Map<String, String> cacheMap = new HashMap<String, String>();
        String sql_command="SELECT * FROM pf_report_logs WHERE ServerName='"+subMchId+"'";
        logger.debug(sql_command);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
//            , true, ScanPayGetValue.class.getClass().getClassLoader()
//            System.out.println("成功加载MySQL驱动！");
//            logger.debug("成功加载MySQL驱动");
            Connection conn;
            conn = DriverManager.getConnection(URL, ACCOUNT, PASSWORD);  //
            Statement stmt = conn.createStatement(); //创建Statement对象
//            System.out.println("成功连接到数据库！");
//            logger.debug("成功连接到数据库");
            ResultSet rs = stmt.executeQuery(sql_command);//创建数据对象
            while (rs.next()){
                cacheMap.put("requestNo", rs.getString("requestNo"));
                cacheMap.put("version", rs.getString("version"));
                cacheMap.put("transId", rs.getString("transId"));
                cacheMap.put("payWay", rs.getString("payWay"));
                cacheMap.put("merNo", rs.getString("merNo"));
                cacheMap.put("subMchId", rs.getString("subMchId"));
                cacheMap.put("subMechantName", rs.getString("subMechantName"));
                cacheMap.put("subMerchantShortname", rs.getString("subMerchantShortname"));
                cacheMap.put("contact", rs.getString("contact"));
                cacheMap.put("contactPhone", rs.getString("contactPhone"));
                cacheMap.put("contactEmail", rs.getString("contactEmail"));
                cacheMap.put("merchantRemark", rs.getString("merchantRemark"));
                cacheMap.put("respCode",rs.getString("respCode"));
                cacheMap.put("respDesc", rs.getString("respDesc"));


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


    /**
     * 更新修改mysqql 方法
     * @param map
     */
    public    void InsertReport(Map<String, String> map){
        String requestNo=map.get("requestNo");
        String version=map.get("version");
        String transId=map.get("transId");
        String payWay=map.get("payWay");
        String merNo=map.get("merNo");
        String subMchId=map.get("subMchId");
        String subMechantName=map.get("subMechantName");
        String subMerchantShortname=map.get("subMerchantShortname");
        String contact=map.get("contact");
        String contactPhone=map.get("contactPhone");
        String contactEmail=map.get("contactEmail");
        String merchantRemark=map.get("merchantRemark");
        String respCode=map.get("respCode");
        String respDesc=map.get("respDesc");



        String commandString="INSERT INTO pf_report_logs (requestNo,version,transId,payWay,merNo,subMchId,subMechantName,subMerchantShortname,contact,contactPhone,contactEmail,merchantRemark,respCode,respDesc)" +
                "VALUES('"+requestNo+"','"+version+"','"+transId+"','"+payWay+"','"+merNo+"','"+subMchId+"','"+subMechantName+"','"+subMerchantShortname+"','"+contact+"','"+contactPhone+"','"+contactEmail+"','"+merchantRemark+"','"+respCode+"','"+respDesc+"')";
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");//com.mysql.jdbc.Driver  //com.mysql.cj.jdbc.Driver
            Connection conn;
            conn = DriverManager.getConnection(URL, ACCOUNT, PASSWORD);  //
            Statement stmt = conn.createStatement(); //创建Statement对象
//
//         ResultSet rs = stmt.executeQuery(commandString);//创建数据对象
//         System.out.println("mysql command:"+commandString);
            PreparedStatement pst = conn.prepareStatement(commandString);
            pst.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
