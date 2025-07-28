package com.andrew.bean;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.management.MBeanServer;
import javax.management.ObjectName;

@ManagedBean(name = "avgIntervalBean", eager = true)
@SessionScoped
public class AvgIntervalBean implements AvgIntervalBeanMBean {
    private static boolean registered = false;
    
    private ArrayList<LocalDateTime> times = new ArrayList<>();
    private double avgInterval = 0;

    public AvgIntervalBean() {
        registerToJMX();
    }

    private void registerToJMX() {
        if (registered) return;

        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("web3:type=AvgIntervalBean");
            if (!mbs.isRegistered(name)) {
                mbs.registerMBean(this, name);
                registered = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clicked(LocalDateTime time) {
        times.add(time);

        if (times.size() >= 2) {
            long total = 0;
            for (int i = 1; i < times.size(); i++) {
                Duration duration = Duration.between(times.get(i - 1), times.get(i));
                total += duration.getSeconds();
            }

            avgInterval = (double) total / (times.size() - 1);
        }
    }

    public double getAvgInterval() {
        return avgInterval;
    }
}
