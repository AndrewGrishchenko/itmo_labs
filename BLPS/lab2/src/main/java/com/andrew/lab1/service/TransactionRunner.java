package com.andrew.lab1.service;

import java.util.function.Supplier;

import javax.naming.InitialContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import jakarta.transaction.UserTransaction;

// @Service
// public class TransactionRunner {
//     public <T> T execute(Supplier<T> action) {
//         UserTransaction utx = null;
//         try {
//             utx = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");

//             utx.begin();

//             T result = action.get();
            
//             utx.commit();
            
//             return result;
//         } catch (Exception e) {
//             try {
//                 if (utx != null) utx.rollback();
//             } catch (Exception rollbackEx) {
//                 throw new RuntimeException(rollbackEx);
//             }

//             throw new RuntimeException(e);
//         }
//     }
// }


@Service
public class TransactionRunner {
    private final PlatformTransactionManager txManager;

    public TransactionRunner(PlatformTransactionManager txManager) {
        this.txManager = txManager;
    }

    public <T> T execute(Supplier<T> action) {
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());

        try {
            T result = action.get();
            txManager.commit(status);
            return result;
        } catch (Exception e) {
            txManager.rollback(status);
            throw e;
        }
    }
}