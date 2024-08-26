package org.games.trace;

import org.slf4j.MDC;

import java.util.Objects;
import java.util.UUID;

public class TraceUtil {
    public static final String TRACE_TOKEN = "trace_uuid";
    public static void enableTrace(){
        if(isBlank(MDC.get(TRACE_TOKEN))){
            MDC.put(TRACE_TOKEN, UUID.randomUUID().toString().replace("-",""));
        }
    }
    public static String traceId(){
        return MDC.get(TraceUtil.TRACE_TOKEN);
    }
    public static void disableTrace() {
        MDC.remove(TRACE_TOKEN);
    }
    private static boolean isBlank(String s){
        return Objects.isNull(s) || s.trim().isEmpty();
    }
}
