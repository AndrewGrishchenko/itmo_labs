package com.andrew.bean;

import java.lang.management.ManagementFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

@ManagedBean(name = "pointStatsBean", eager = true)
@SessionScoped
public class PointStatsBean implements PointStatsBeanMBean, NotificationBroadcaster {
    private static boolean registered = false;
    
    private final NotificationBroadcasterSupport notifier = new NotificationBroadcasterSupport();
    private long sequenceNumber = 1;

    private int pointCount;
    private int hitCount;
    private int consMissCount;

    public PointStatsBean() {
        registerToJMX();
    }

    private void registerToJMX() {
        if (registered) return;

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("web3:type=PointStatsBean");
            if (!mbs.isRegistered(name)) {
                mbs.registerMBean(this, name);
                registered = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clicked(boolean hit) {
        pointCount++;
        
        if (hit) {
            hitCount++;
            consMissCount = 0;
        } else {
            consMissCount++;
            if (consMissCount >= 2) {
                sendNotification("User missed " + String.valueOf(consMissCount) + " times");
            }
        }
    }

    private void sendNotification(String message) {
        Notification notification = new Notification(
            "consecutive.misses",
            this,
            sequenceNumber++,
            System.currentTimeMillis(),
            message
        );
        notifier.sendNotification(notification);
    }

    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
        notifier.addNotificationListener(listener, filter, handback);
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        notifier.removeNotificationListener(listener);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] types = new String[] { "consecutive.misses" };
        String name = Notification.class.getName();
        String description = "Notification sent when 2 consecutive misses are recorded";
        return new MBeanNotificationInfo[] { new MBeanNotificationInfo(types, name, description) };
    }

    public int getPointCount() {
        return pointCount;
    }

    public int getHitCount() {
        return hitCount;
    }
}
