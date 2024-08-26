package org.games.trace;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LogTraceInterceptor implements HandlerInterceptor {
    private void createTraceId() {
        TraceUtil.enableTrace();
    }
    private void removeTraceId() {
        TraceUtil.disableTrace();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        this.createTraceId();
        return true;
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        this.removeTraceId();
    }
}
