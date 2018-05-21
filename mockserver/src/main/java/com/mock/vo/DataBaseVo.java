package com.mock.vo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class DataBaseVo {
    private static Logger logger = LoggerFactory.getLogger(DataBaseVo.class);

    public void validData() {
        this.validData(this, null);
    }

    private void validData(Object o, String cn) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            VoField vf = field.getAnnotation(VoField.class);
            if(vf == null){
                continue;
            }

            String desc = vf.desc();
            if(StringUtils.isBlank(desc)){
                desc = vf.name();
            }
            if(StringUtils.isBlank(desc)){
                desc = field.getName();
            }
            if(cn != null){
                desc = cn + desc;
            }
            Object val = null;
            try {
                val = field.get(o);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(vf.fieldType().equals(FieldType.OBJECT)){
                if(vf.require()){
                    if(val == null){
                        throw new RuntimeException(desc + "不能为空!");
                    }
                    this.validData(val, desc);
                }
                continue;
            }
            if(vf.require() && (val == null || val.toString().isEmpty())){
                throw new RuntimeException(desc + "不能为空!");
            }

            if(val != null && val.toString().getBytes().length > vf.maxLen()){
                throw new RuntimeException(desc + "长度超限!");
            }
        }
    }
}
