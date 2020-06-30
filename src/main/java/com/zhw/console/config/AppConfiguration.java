package com.zhw.console.config;

import com.zhw.console.utils.AppUtil;
import com.zhw.console.utils.AppUtilImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class AppConfiguration {
    @Bean
    public AppUtil appUtil(ApplicationContext context){
        AppUtilImpl impl = new AppUtilImpl(context);
        return impl;
    }


}
