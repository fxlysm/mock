package com.mock.vo;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/**
 * 第三方支付信息动态字段 - 字段属性
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface VoField {
    FieldType fieldType() default FieldType.INPUT; // 字段类型
    String name() default ""; // 字段变量名
    String desc() default ""; // 字段描述
    boolean require() default true; // 是否必填
    int maxLen() default Integer.MAX_VALUE; // 最大长度
    String options() default ""; // 下拉列表选择
    int colspan() default 1; // 在页面上的占宽
    boolean dynamic() default true; // 是否动态字段，非动态字段在getFields时不返回给页面
    String reg() default ""; // 正则表达式，给前端校验
    String placeholder() default ""; // 提示语
}