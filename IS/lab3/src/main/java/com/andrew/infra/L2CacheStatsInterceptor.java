package com.andrew.infra;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.*;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;

@LogL2CacheStats
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class L2CacheStatsInterceptor {

    @Inject
    SessionFactory sessionFactory;

    private static volatile boolean enabled = true;

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    @AroundInvoke
    public Object logStats(InvocationContext ctx) throws Exception {
        // System.err.println("INTERCEPTOR CALLED");
        System.err.println("INTERCEPTOR CALLED: " + ctx.getMethod().getName() + " Thread: " + Thread.currentThread().getId());
        
        Object result = ctx.proceed();

        if (enabled) {
            Statistics stats = sessionFactory.getStatistics();

            System.out.println("=== L2 CACHE STATS ===");
            System.out.println("Entity Hits:   " + stats.getSecondLevelCacheHitCount());
            System.out.println("Entity Misses: " + stats.getSecondLevelCacheMissCount());
            System.out.println("Entity Puts:   " + stats.getSecondLevelCachePutCount());
            System.out.println("Query Hits:    " + stats.getQueryCacheHitCount());
            System.out.println("Query Misses:  " + stats.getQueryCacheMissCount());
            System.out.println("======================");
        }

        return result;
    }
}