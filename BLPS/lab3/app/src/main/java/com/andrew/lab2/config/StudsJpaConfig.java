package com.andrew.lab2.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.andrew.lab2.repository.studs",
    entityManagerFactoryRef = "studsEmf",
    transactionManagerRef = "transactionManager"
)
public class StudsJpaConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean studsEmf() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        
        Map<String, Object> properties = new HashMap<>();
        properties.put(
            "hibernate.hbm2ddl.auto",
            "update"
        );
        
        emf.setPersistenceUnitName("studsPU");
        emf.setPersistenceXmlLocation("classpath:META-INF/persistence.xml");
        emf.setJpaPropertyMap(properties);
        return emf;
    }
}
