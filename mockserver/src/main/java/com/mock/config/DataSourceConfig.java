//package com.config;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.sql.DataSource;
//import java.sql.SQLException;
//
//@Configuration
//@Primary // 在同样的DataSource中，首先使用被标注的DataSource
//public class DataSourceConfig  extends DataSourceProperties {
//    private Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
//
//    @Value("${spring.datasource.testmysqlurl}")
//    private String TestdbUrl;
//
//    @Value("${spring.datasource.testmysqlusername}")
//    private String Testdbusername;
//
//    @Value("${spring.datasource.testmysqlpassword}")
//    private String Testdbpassword;
//
//    @Value("${spring.datasource.initial-size}")
//    private int initialSize;
//
//    @Value("${spring.datasource.min-idle}")
//    private int minIdle;
//
//    @Value("${spring.datasource.maxActive}")
//    private int maxActive;
//
//    @Value("${spring.datasource.max-wait}")
//    private int maxWait;
//
//
//    @Bean // 声明其为Bean实例
//    public DataSource dataSource() {
//        DruidDataSource datasource = new DruidDataSource();
//
//        datasource.setUrl(this.TestdbUrl);
//        datasource.setUsername(Testdbusername);
//        datasource.setPassword(Testdbpassword);
//
//
//        // configuration
//        datasource.setInitialSize(initialSize);
//        datasource.setMinIdle(minIdle);
//        datasource.setMaxActive(maxActive);
//        datasource.setMaxWait(maxWait);
//
//
//
//        return datasource;
//    }
//}
