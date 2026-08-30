package com.andrew.lab2.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration 
public class QuartzConfig {
    @Bean 
    public DataSource quartzDataSource() {
        JndiDataSourceLookup lookup = new JndiDataSourceLookup();
        lookup.setResourceRef(true);

        return lookup.getDataSource("java:/jdbc/QuartzDS");
    }

    // @Bean 
    // public SchedulerFactoryBean schedulerFactoryBean(DataSource quartzDataSource) {
    //     SchedulerFactoryBean factory = new SchedulerFactoryBean();

    //     factory.setDataSource(quartzDataSource);

    //     return factory;
    // }
}
