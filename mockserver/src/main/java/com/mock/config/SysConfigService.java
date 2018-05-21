package com.mock.config;

/**
 * @author guoyaowu
 * @Description: 系统参数配置接口
 * @date 20170508
 */
public interface SysConfigService {
    /**
     * @Author: guoyaowu
     * @Description: 获取系统配置参数值
     * @param name 系统配置参数名
     * @return 系统配置参数值
     */
    String getByName(String name);

    public void init();
}