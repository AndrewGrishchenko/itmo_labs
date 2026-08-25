package com.andrew.lab2.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;

@Configuration
// @EnableTransactionManagement
public class JtaConfig {
    // @Bean
    // public PlatformTransactionManager transactionManager() {
    //     return new JpaTransactionManager();
    // }

    @Bean
    public JtaTransactionManager transactionManager() throws NamingException {
        JtaTransactionManager tm = new JtaTransactionManager();
        InitialContext ctx = new InitialContext();
        tm.setTransactionManager((TransactionManager) ctx.lookup("java:jboss/TransactionManager"));
        tm.setUserTransaction((UserTransaction) ctx.lookup("java:jboss/UserTransaction"));
        tm.setAllowCustomIsolationLevels(true);
        return tm;
    }
}
