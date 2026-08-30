package com.andrew.lab2.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.andrew.lab2.repository.studs2",
    entityManagerFactoryRef = "studs2Emf",
    transactionManagerRef = "transactionManager"
)
public class Studs2JpaConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean studs2Emf() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        
        Map<String, Object> properties = new HashMap<>();
        properties.put(
            "hibernate.hbm2ddl.auto",
            "update"
        );
        
        emf.setPersistenceUnitName("studs2PU");
        emf.setPersistenceXmlLocation("classpath:META-INF/persistence.xml");
        emf.setJpaPropertyMap(properties);
        return emf;
    }   
}
