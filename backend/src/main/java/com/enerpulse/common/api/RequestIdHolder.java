package com.enerpulse.common.api;

public class RequestIdHolder {
    private static final ThreadLocal<String> HOLDER = new ThreadLocal<>();

    public static void set(String requestId) {
        HOLDER.set(requestId);
    }

    public static String get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
