package pay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

public class TestSign {

	/** 把数组所有元素排序，并按照“key1=value1&key2=value2…”的格式拼接
     * @author 
     * @param payParams
     * @return
     */
    public static void buildPayParams(StringBuilder sb,Map<String, String> payParams,boolean encoding){
        List<String> keys = new ArrayList<String>(payParams.keySet());
        Collections.sort(keys);
        for(String key : keys){
            sb.append(key).append("=");
            if(encoding){
                sb.append(urlEncode(payParams.get(key)));
            }else{
                sb.append(payParams.get(key));
            }
            sb.append("&");
        }
        sb.setLength(sb.length() - 1);
    }
    
    public static String urlEncode(String str){
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Throwable e) {
            return str;
        } 
    }
    
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }
    
    /**
     * 签名字符串
     * @param md5Str 需要签名的字符串
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign(String md5Str, String input_charset) {
        return DigestUtils.md5Hex(getContentBytes(md5Str, input_charset));
    }
    
	/**
     * 校验签名
     * @param params 参数
     * @param key 加密key
     * @return
     */
    public static boolean verify(Map<String,String> params, String key) {
    	boolean result = false;
        if(params.containsKey("sign")){
            String sign = params.get("sign");
            params.remove("sign");
            StringBuilder buf = new StringBuilder((params.size() +1) * 10);
            buildPayParams(buf,params,false);
            String preStr = buf.toString();
            String signRecieve = sign(preStr + "&key=" + key, "utf-8");
            result = sign.equalsIgnoreCase(signRecieve);
        }
        return result;
    }
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "charset=UTF-8&code_img_url=http://public.13322.com/25031549.jpeg&code_url=http://public.13322.com/25031549.jpeg&device_info=device_info&err_msg=http://pay.1332255.com/payCenter/core/alipay.swiftAlipayNotify.do&mch_id=7551000001&nonce_str=54e183e3-c053-49bc-9bb8-b1632d19601e&result_code=0&sign_type=MD5&status=0&version=2.0";
		String signRecieve = sign(str + "&key=527508019920daf31cf31dd3e2c19232", "utf-8");
		System.out.println(signRecieve); 
	}

}
