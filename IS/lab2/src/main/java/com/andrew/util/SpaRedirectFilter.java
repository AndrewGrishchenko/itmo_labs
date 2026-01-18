package com.andrew.util;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter("/*")
public class SpaRedirectFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        String context = r.getContextPath();
        String uri = r.getRequestURI();
        String path = uri.substring(context.length());

        if (path.startsWith("/api/") || path.equals("/api") ) {
            chain.doFilter(req, res);
            return;
        }

        if (path.startsWith("/websocket/") || path.equals("/websocket") ) {
            chain.doFilter(req, res);
            return;
        }

        if (path.equals("/") || path.equals("/index.html") || path.contains(".")) {
            chain.doFilter(req, res);
            return;
        }

        req.getRequestDispatcher("/index.html").forward(req, res);
    }
}
