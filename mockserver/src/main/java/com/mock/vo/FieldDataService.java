package com.mock.vo;

public interface FieldDataService {
    String signTypes = "[{\"val\":\"MD5\",\"text\":\"MD5\"},{\"val\":\"RSA\",\"text\":\"RSA\"},{\"val\":\"DSA\",\"text\":\"DSA\"}]";

    String charsets = "[{\"val\":\"UTF-8\",\"text\":\"UTF-8\"}]";

    String contactTypes = "[{\"val\":\"LEGAL_PERSON\",\"text\":\"法人\"},{\"val\":\"CONTROLLER\",\"text\":\"实际控制人\"},{\"val\":\"AGENT\",\"text\":\"代理人\"},{\"val\":\"OTHER\",\"text\":\"其他\"}]";

    String msContactType= "[{\"val\":\"01\",\"text\":\"法人\"},{\"val\":\"02\",\"text\":\"实际控制人\"},{\"val\":\"03\",\"text\":\"代理人\"},{\"val\":\"00\",\"text\":\"其他\"}]";

    String languages = "[{\"val\":\"zh_CN\",\"text\":\"中文\"},{\"val\":\"en\",\"text\":\"英文\"}]";

    String dataTypes = "[{\"val\":\"json\",\"text\":\"json\"},{\"val\":\"xml\",\"text\":\"xml\"}]";
}
