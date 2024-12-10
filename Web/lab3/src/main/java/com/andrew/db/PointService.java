package com.andrew.db;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.andrew.model.Point;

public class PointService {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");

    public void insertPoint(Point point) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(point);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<Point> getAllPoints() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Point p", Point.class).getResultList();
        } finally {
            em.close();
        }
    }

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
