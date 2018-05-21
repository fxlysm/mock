package com.mock.sign;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mock.excep.PayCenterThirdApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Administrator on 2017/6/7.
 */
public class ChinaCardPosUtil {
    private static final Logger logger = LoggerFactory.getLogger(ChinaCardPosUtil.class);

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static final ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.configure(SerializationFeature.WRITE_ENUMS_USING_INDEX,true);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static String signByPrivate(String content, String privateKey,
                                       String input_charset) {
        if (privateKey == null) {
            throw new PayCenterThirdApiException("Error", "加密私钥为空, 请设置");
        }
        PrivateKey privateKeyInfo = getPrivateKey(privateKey);
        return signByPrivate(content, privateKeyInfo, input_charset);
    }

    /**
     * 得到私钥
     *
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws Exception
     */
    private static PrivateKey getPrivateKey(String key) {
        byte[] keyBytes = buildPKCS8Key(key);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            return privateKey;
        } catch (NoSuchAlgorithmException  e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("Error", "转换密钥失败！");
        }catch ( InvalidKeySpecException e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("Error", "转换密钥失败！");
        }
    }

    private static byte[] buildPKCS8Key(String privateKey) {
        if (privateKey.contains("-----BEGIN PRIVATE KEY-----")) {
            return Base64.decode(privateKey.replaceAll("-----\\w+ PRIVATE KEY-----", "").getBytes());
        } else if (privateKey.contains("-----BEGIN RSA PRIVATE KEY-----")) {
            final byte[] innerKey = Base64.decode(privateKey.replaceAll("-----\\w+ RSA PRIVATE KEY-----", "").getBytes());
            final byte[] result = new byte[innerKey.length + 26];
            System.arraycopy(Base64.decode("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKY=".getBytes()), 0, result, 0, 26);
            System.arraycopy(BigInteger.valueOf(result.length - 4).toByteArray(), 0, result, 2, 2);
            System.arraycopy(BigInteger.valueOf(innerKey.length).toByteArray(), 0, result, 24, 2);
            System.arraycopy(innerKey, 0, result, 26, innerKey.length);
            return result;
        } else {
            return Base64.decode(privateKey.getBytes());
        }
    }

    private static String signByPrivate(String content, PrivateKey privateKey,
                                        String input_charset) {
        if (privateKey == null) {
            throw new PayCenterThirdApiException("Error", "加密私钥为空, 请设置");
        }

        Signature signature;
        try {
            signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initSign(privateKey);
            signature.update(content.getBytes(input_charset));

            return new String(Base64.encode(signature.sign()));
        } catch (NoSuchAlgorithmException  e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("", "不支持的算法");
        }catch ( UnsupportedEncodingException  e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("", "不支持的算法");
        }catch ( SignatureException  e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("", "不支持的算法");
        }catch (InvalidKeyException e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("", "不支持的算法");
        }
    }

    /**
     * RSA验签名检查
     *
     * @param content
     *            待签名数据
     * @param sign
     *            签名值
     * @param publicKey
     *            支付宝公钥
     * @param input_charset
     *            编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign,
                                 String publicKey, String input_charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = Base64.decode(publicKey.getBytes());
            PublicKey pubKey = keyFactory
                    .generatePublic(new X509EncodedKeySpec(encodedKey));
            return verify(content, sign, pubKey, input_charset);
        } catch (NoSuchAlgorithmException  e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("Error");
        } catch ( InvalidKeySpecException e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("Error");
        }

    }

    public static boolean verify(String content,String sign, PublicKey publicKey,String inputCharset){
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(inputCharset));
            boolean bverify = signature.verify(Base64.decode(sign.getBytes()));
            return bverify;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new PayCenterThirdApiException("");
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
            throw new PayCenterThirdApiException("");
        }
        catch(SignatureException e){
            e.printStackTrace();
            throw new PayCenterThirdApiException("");
        }
        catch(InvalidKeyException e){
            e.printStackTrace();
            throw new PayCenterThirdApiException("");
        }
    }
}