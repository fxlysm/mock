package com.mock.util.http;

import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpUtil {

	private static Logger logger = Logger.getLogger(HttpUtil.class.getName());

	/** UTF-8 */
	private static final String UTF_8 = "UTF-8";
	/** 日志记录tag */
	private static final String TAG = "HttpClients";

	/** 用户host */
	private static String proxyHost = "127.0.0.1";
	/** 用户端口 */
	private static int proxyPort = 80;
	/** 是否使用用户端口 */
	private static boolean useProxy = false;

	/** 连接超时 */
	private static final int TIMEOUT_CONNECTION = 60000;
	/** 读取超时 */
	private static final int TIMEOUT_SOCKET = 60000;
	/** 重试3次 */
	private static final int RETRY_TIME = 3;

	/**
	 * @return
	 * @throws Exception
	 */
	public static String doHtmlPost(HttpClient httpClient, HttpPost httpPost)
			throws Exception {
		String responseBody = null;
		int statusCode = -1;
		try {

			HttpResponse httpResponse = httpClient.execute(httpPost);
			Header lastHeader = httpResponse.getLastHeader("Set-Cookie");
			if (null != lastHeader) {
				httpPost.setHeader("cookie", lastHeader.getValue());
			}
			statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("HTTP" + "  " + "HttpMethod failed: "
						+ httpResponse.getStatusLine());
			}
			InputStream is = httpResponse.getEntity().getContent();
			responseBody = getStreamAsString(is, HTTP.UTF_8);
		} catch (Exception e) {
			// 发生网络异常
			logger.error("发生网络异常", e);
			throw new Exception("发生网络异常", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
			httpClient = null;
		}
		return responseBody;
	}
	
    /** 
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     * @throws UnsupportedEncodingException 
     */
    public static String createLinkString(Map<String, String> params) throws UnsupportedEncodingException {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if(StringUtils.isNotBlank(value)){
            	value = URLEncoder.encode(value,"iso-8859-1");
            }
            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

	/**
	 * 
	 * 发起网络请求
	 * 
	 * @param url
	 *            URL
	 * @param reqParams
	 *            requestData
	 * @return INPUTSTREAM
	 * @throws Exception
	 */
	public static String doPost(String url, Map<String, String> reqParams)
			throws Exception {
		String responseBody = null;
		HttpPost httpPost = null;
		HttpClient httpClient = null;
		int statusCode = -1;
		int time = 0;
		do {
			try {
				httpPost = new HttpPost(url);
				httpClient = getHttpClient();
				// 设置HTTP POST请求参数必须用NameValuePair对象
				List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				for (String key : reqParams.keySet()) {
					String value = reqParams.get(key);
					params.add(new BasicNameValuePair(key, value));
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params,
						HTTP.UTF_8);
				// 设置HTTP POST请求参数
				httpPost.setEntity(entity);
				HttpResponse httpResponse = httpClient.execute(httpPost);
				statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					System.out.println("HTTP" + "  " + "HttpMethod failed: "
							+ httpResponse.getStatusLine());
				}
				InputStream is = httpResponse.getEntity().getContent();
				responseBody = getStreamAsString(is, HTTP.UTF_8);
				break;
			} catch (UnsupportedEncodingException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();

			} catch (ClientProtocolException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
			} catch (Exception e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return responseBody;
	}

	/**
	 * 
	 * 将InputStream 转化为String
	 * 
	 * @param stream
	 *            inputstream
	 * @param charset
	 *            字符集
	 * @return
	 * @throws IOException
	 */
	private static String getStreamAsString(InputStream stream, String charset)
			throws IOException {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					stream, charset), 8192);
			StringWriter writer = new StringWriter();

			char[] chars = new char[8192];
			int count = 0;
			while ((count = reader.read(chars)) > 0) {
				writer.write(chars, 0, count);
			}

			return writer.toString();
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * 得到httpClient
	 * 
	 * @return
	 */
	public HttpClient getHttpClient1() {
		final HttpParams httpParams = new BasicHttpParams();

		if (useProxy) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		HttpConnectionParams.setConnectionTimeout(httpParams,
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_SOCKET);
		HttpClientParams.setRedirecting(httpParams, true);
		final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.14) Gecko/20110218 Firefox/3.6.14";

		HttpProtocolParams.setUserAgent(httpParams, userAgent);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpClientParams.setCookiePolicy(httpParams, CookiePolicy.RFC_2109);

		HttpProtocolParams.setUseExpectContinue(httpParams, false);
		HttpClient client = new DefaultHttpClient(httpParams);

		return client;
	}

	/**
	 * 
	 * 得到httpClient
	 * 
	 * @return
	 */
	public static HttpClient getHttpClient() {
		final HttpParams httpParams = new BasicHttpParams();
		if (useProxy) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		HttpConnectionParams.setConnectionTimeout(httpParams,
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_SOCKET);
		HttpClientParams.setRedirecting(httpParams, true);
		final String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.14) Gecko/20110218 Firefox/3.6.14";

		HttpProtocolParams.setUserAgent(httpParams, userAgent);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpClientParams.setCookiePolicy(httpParams,
				CookiePolicy.BROWSER_COMPATIBILITY);
		HttpProtocolParams.setUseExpectContinue(httpParams, false);
		HttpClient client = new DefaultHttpClient(httpParams);

		return client;
	}

	/**
	 * 
	 * 发起网络请求
	 * 
	 * @param url
	 *            URL
	 * @return INPUTSTREAM
	 * @throws Exception
	 */
	public static String doGet(String url) throws Exception {
		String responseBody = null;
		HttpGet httpGet = null;
		HttpClient httpClient = null;
		int statusCode = -1;
		int time = 0;
		do {
			try {
				httpGet = new HttpGet(url);
				httpClient = getHttpClient();
				HttpResponse httpResponse = httpClient.execute(httpGet);
				statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					System.out.println("HTTP" + "  " + "HttpMethod failed: "
							+ httpResponse.getStatusLine());
				}
				InputStream is = httpResponse.getEntity().getContent();
				responseBody = getStreamAsString(is, HTTP.UTF_8);
				break;
			} catch (UnsupportedEncodingException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();

			} catch (ClientProtocolException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
			} catch (Exception e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
			} finally {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}
		} while (time < RETRY_TIME);
		return responseBody;
	}

	/** 
	* @Title: postToUrl 
	* @Description: 发送url post请求
	* @param @param url
	* @param @param postData
	* @param @return
	* @param @throws Exception    设定文件 
	* @return String    返回类型 
	* @throws 
	*/ 
	public static String postToUrl(String url, Map<String,String> postData) throws Exception {
		String postStr = createLinkString(postData);
		String postResult = "";
		URL u = new URL(url);
		URLConnection connection = u.openConnection();
		connection.setDoOutput(true);

		OutputStreamWriter out = new OutputStreamWriter(
				connection.getOutputStream());
		// out.write(URLEncoder.encode(postData, "UTF-8"));
		out.write(postStr);
		out.flush();
		out.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String decodedString;
		while ((decodedString = in.readLine()) != null) {
			postResult = postResult + decodedString;
		}
		in.close();
		return postResult;
	}
	
	/**
	 * 发送HTTPS	POST请求
	 * 
	 * @param url,POST访问的参数Map对象
	 * @return  返回响应值
	 * */
	public static final String sendHttpsRequestByPost(String url, Map<String, String> params) {
		String responseContent = null;
		HttpClient httpClient = new DefaultHttpClient();
		//创建TrustManager
		X509TrustManager xtm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		//这个好像是HOST验证
		X509HostnameVerifier hostnameVerifier = new X509HostnameVerifier() {
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
			public void verify(String arg0, SSLSocket arg1) throws IOException {}
			public void verify(String arg0, String[] arg1, String[] arg2) throws SSLException {}
			public void verify(String arg0, X509Certificate arg1) throws SSLException {}
		};
		try {
			//TLS1.0与SSL3.0基本上没有太大的差别，可粗略理解为TLS是SSL的继承者，但它们使用的是相同的SSLContext
			SSLContext ctx = SSLContext.getInstance("TLS");
			//使用TrustManager来初始化该上下文，TrustManager只是被SSL的Socket所使用
			ctx.init(null, new TrustManager[] { xtm }, null);
			//创建SSLSocketFactory
			SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);
			socketFactory.setHostnameVerifier(hostnameVerifier);
			//通过SchemeRegistry将SSLSocketFactory注册到我们的HttpClient上
			httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", socketFactory, 443));
			HttpPost httpPost = new HttpPost(url);
			List<NameValuePair> formParams = new ArrayList<NameValuePair>(); // 构建POST请求的表单参数
			for (Map.Entry<String, String> entry : params.entrySet()) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(formParams, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity(); // 获取响应实体
			if (entity != null) {
				responseContent = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			httpClient.getConnectionManager().shutdown();
		}
		return responseContent;
	}
	
    /** 
    * @Title: getIpAddr 
    * @Description: 获取访问ip 
    * @param @param request
    * @param @return    设定文件 
    * @return String    返回类型 
    * @throws 
    */ 
    public static String getIpAddr(HttpServletRequest request) { 
        String ip = request.getHeader("x-forwarded-for"); 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){ 
            ip = request.getHeader("WL-Proxy-Client-IP");
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr(); 
        }
        return ip; 
    }

	public static String getUrl(String url, Map<String, Object> params) {
		// 添加url参数
		if (params != null) {
			Iterator<String> it = params.keySet().iterator();
			StringBuffer sb = null;
			while (it.hasNext()) {
				String key = it.next();
				String value = (String)params.get(key);
				if (sb == null) {
					sb = new StringBuffer();
					sb.append("?");
				} else {
					sb.append("&");
				}
				sb.append(key);
				sb.append("=");
				sb.append(value);
			}
			url += sb.toString();
		}
		return url;
	}



	/**
	 * 把数组所有元素排序，并将值拼接成字符串连接
	 * @param params 需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 */
	public static String createLinkValueString(Map<String, Object> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			Object value = params.get(key);

			stringBuilder.append(value.toString());
		}

		return stringBuilder.toString();
	}






	public static String httpRequest(String requestUrl,String requestMethod,String outputStr){
		StringBuffer buffer=null;
		try{
			URL url=new URL(requestUrl);
			HttpURLConnection conn=(HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod(requestMethod);
			conn.connect();
			//往服务器端写内容 也就是发起http请求需要带的参数
			if(null!=outputStr){
				OutputStream os=conn.getOutputStream();
				os.write(outputStr.getBytes("utf-8"));
				os.close();
			}

			//读取服务器端返回的内容
			InputStream is=conn.getInputStream();
			InputStreamReader isr=new InputStreamReader(is,"utf-8");
			BufferedReader br=new BufferedReader(isr);
			buffer=new StringBuffer();
			String line=null;
			while((line=br.readLine())!=null){
				buffer.append(line);
			}
		}catch(Exception e){
			e.printStackTrace();
			return "post error";
		}
		return buffer.toString();
	}








	/**
	 * 发送HTTP请求
	 *
	 * @param url
	 * @param propsMap
	 *            发送的参数
	 */
	public static String httpSend(String url, Map<String, String> propsMap) {
		String responseMsg = "";

		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
		PostMethod postMethod = new PostMethod(url);// POST请求
		postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");

		// postMethod.
		// 参数设置
		Set<String> keySet = propsMap.keySet();
		org.apache.commons.httpclient.NameValuePair[] postData = new org.apache.commons.httpclient.NameValuePair[keySet.size()];
		int index = 0;
		for (String key : keySet) {
			postData[index++] = new org.apache.commons.httpclient.NameValuePair(key, propsMap.get(key).toString());
		}
		postMethod.addParameters(postData);
		try {
			httpClient.executeMethod(postMethod);// 发送请求
			// Log.info(postMethod.getStatusCode());
			// 读取内容
			byte[] responseBody = postMethod.getResponseBody();
			// 处理返回的内容
			responseMsg = new String(responseBody,"utf-8");

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();// 关闭连接
		}
		return responseMsg;
	}


public static  String PostJson(String urlstr, JSONObject obj){
	String responseMsg = "";
	try {
	// 创建url资源
	URL url = new URL(urlstr);
	// 建立http连接
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// 设置允许输出
	conn.setDoOutput(true);

	conn.setDoInput(true);

	// 设置不用缓存
	conn.setUseCaches(false);
	// 设置传递方式
	conn.setRequestMethod("POST");
	// 设置维持长连接
	conn.setRequestProperty("Connection", "Keep-Alive");
	// 设置文件字符集:
	conn.setRequestProperty("Charset", "UTF-8");
	//转换为字节数组
	byte[] data = (obj.toString()).getBytes();
	// 设置文件长度
	conn.setRequestProperty("Content-Length", String.valueOf(data.length));

	// 设置文件类型:
	conn.setRequestProperty("contentType", "application/json");


	// 开始连接请求
	conn.connect();
	OutputStream  out = conn.getOutputStream();
	// 写入请求的字符串
	out.write((obj.toString()).getBytes());
	out.flush();
	out.close();

	System.out.println(conn.getResponseCode());

	// 请求返回的状态
	if (conn.getResponseCode() == 200) {
		System.out.println("连接成功");

		// 请求返回的数据
		InputStream in = conn.getInputStream();
		String a = null;
		try {
			byte[] data1 = new byte[in.available()];
			in.read(data1);
			// 转成字符串
			a = new String(data1);
			System.out.println(a);
			responseMsg=a;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}
	} else {
//		System.out.println("no++");
		logger.info("Connect fail");
	}

 } catch (Exception e) {
		logger.error("Error Message:"+e.toString());

	}

	return responseMsg ;
}















	public static void main(String[] args) throws Exception {
//		String url = "https://test-cd.cherrycredits.com:9000/DirectCharging";
		String url = "https://116.12.134.130:9000/DirectCharging";
		Map<String, String> reqParams = new HashMap<String, String>();
		String serialNum = "8786853737514466";
		String serialCode = "10000119";
		String salt = "3h8&ak%sz";
		reqParams.put("serialNum", serialNum);
		reqParams.put("serialCode", serialCode);
		reqParams.put("partnerID", "4");
		reqParams.put("requesterSign", DigestUtils.sha256Hex(serialNum+serialCode+salt));
		System.out.println(sendHttpsRequestByPost(url, reqParams));

	}
}
