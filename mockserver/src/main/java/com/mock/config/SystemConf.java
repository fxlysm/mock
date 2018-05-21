package com.mock.config;

public class SystemConf {
    public static String  ZXMOCK_MSG="zxmock:pay";
    public static String  PFMOCK_MSG="pfmock:pay";
    public static String  PFMOCK_REFOUND="pfmock:refund";
    public static String  MSMOCK_MSG="msmock:pay";
    public static String  JDMOCK_MSG="jdmock:pay";
    public static String  KFTMOCK_MSG="kftmock:pay";
    public static String  HXMOCK_MSG="hxmock:pay";

    public static String OLD_PAYCENTER_KEY="9d101c97133837e13dde2d32a5054abb";

//    **********************************************************
    /**
     * RAS_PUBLICKEY   为 接收到支付请求后进行验签用
     * RSA_PRIVATEKEY        提供给平台用----为发送支付请求
     * RAS_MOCK_PUBLICKEY    提供给平台用----接收到回调验签用
     * RSA_MOCK_PRIVATEKEY  发送回调使用的支付请求  生成签名使用
     */

    public static String PF_RAS_PUBLICKEY="";
    public static String PF_RSA_PRIVATEKEY="";
    public static String PF_RAS_MOCK_PUBLICKEY="";
    public static String PF_RSA_MOCK_PRIVATEKEY="";


    public static String MS_RAS_PUBLICKEY="";
    public static String MS_RSA_PRIVATEKEY="";
    public static String MS_RAS_MOCK_PUBLICKEY="";
    public static String MS_RSA_MOCK_PRIVATEKEY="";


    public static String KFT_RAS_PUBLICKEY="";
    public static String KFT_RSA_PRIVATEKEY="";
    public static String KFT_RAS_MOCK_PUBLICKEY="";
    public static String KFT_RSA_MOCK_PRIVATEKEY="";


    public static String JD_RAS_PUBLICKEY="";
    public static String JD_RSA_PRIVATEKEY="";
    public static String JD_RAS_MOCK_PUBLICKEY="";
    public static String JD_RSA_MOCK_PRIVATEKEY="";

}
