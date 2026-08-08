package com.andrew.lab1.config;

import javax.naming.InitialContext;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import jakarta.transaction.TransactionManager;

@Component
public class TxInspector {
    private final PlatformTransactionManager tm;
    private static final Logger log = LoggerFactory.getLogger(TxInspector.class);

    @Autowired
    private EntityManagerFactory emf;

    public TxInspector(PlatformTransactionManager tm) {
        this.tm = tm;
    }

    @PostConstruct
    public void print() throws Exception {
        log.warn("TX Manager class = " + tm.getClass());

        TransactionManager tm =
            (TransactionManager) new InitialContext().lookup("java:/TransactionManager");

        log.warn("TM = " + tm.getClass());

        log.warn("EMF class = " + emf.getClass());
    
        SessionFactory sf = emf.unwrap(SessionFactory.class);
        log.warn("SF = " + sf.getClass());
    }
}
