package com.mock.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class MapUtil {
    /**
     * 方法名称:transMapToString 传入参数:map 返回值:String 形如
     * username'chenziwen^password'1234
     */
    public static String transMapToString(Map map) {
        Map.Entry entry;
        StringBuffer sb = new StringBuffer();
        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
            entry = (Map.Entry) iterator.next();
            sb.append(entry.getKey().toString()).append("'")
                    .append(null == entry.getValue() ? "" : entry.getValue().toString())
                    .append(iterator.hasNext() ? "^" : "");
        }
        return sb.toString();
    }

    /**
     * 方法名称:transStringToMap 传入参数:mapString 形如 username'chenziwen^password'1234
     * 返回值:Map
     */
    public static Map transStringToMap(String mapString) {
        Map map = new HashMap();
        java.util.StringTokenizer items;
        for (StringTokenizer entrys = new StringTokenizer(mapString, "^"); entrys.hasMoreTokens(); map
                .put(items.nextToken(), items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
            items = new StringTokenizer(entrys.nextToken(), "'");
        return map;
    }
//    public static Map<String,String> readJson2Map(String json) {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//
//            //将json字符串转成map结合解析出来，并打印(这里以解析成map为例)
//            Map<String, Map<String, Object>> maps = objectMapper.readValue(
//                    json, Map.class);
//            System.out.println(maps.size());
//            Set<String> key = maps.keySet();
//            Iterator<String> iter = key.iterator();
//            while (iter.hasNext()) {
//                String field = iter.next();
//                System.out.println(field + ":" + maps.get(field));
//            }
//        } catch (JsonParseException e) {
//            e.printStackTrace();
//        } catch (JsonMappingException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    readJson2Map(json);
}
