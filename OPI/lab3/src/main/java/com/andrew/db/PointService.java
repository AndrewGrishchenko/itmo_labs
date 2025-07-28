package com.andrew.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.andrew.model.Point;

/**
 * Service for points
 */
public class PointService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    /**
     * Default PointService constructor
     */
    public PointService() {
    }

    /**
     * Insert point
     * @param point point
     */
    public void insertPoint(Point point) {
        if (point == null) throw new RuntimeException();
        
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(point);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /**
     * Get all points
     * @return points
     */
    public List<Point> getAllPoints() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Point p", Point.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Clear all points
     */
    public void clearPoints() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Point").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
