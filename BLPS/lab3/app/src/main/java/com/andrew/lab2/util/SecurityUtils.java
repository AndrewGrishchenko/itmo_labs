package com.andrew.lab2.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.andrew.lab2.entity.enums.Role;
import com.andrew.lab2.entity.xml.XmlUserDetails;

@Component
public class SecurityUtils {
    public static XmlUserDetails getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof XmlUserDetails) {
            return (XmlUserDetails) authentication.getPrincipal();
        }

        return null;
    }

    public static Long getCurrentUserId() {
        return getCurrentPrincipal().getId();
    }

    public static String getCurrentUsername() {
        return getCurrentPrincipal().getUsername();
    }

    public static Role getCurrentUserRole() {
        return getCurrentPrincipal().getRole();
    }
}
