package com.couple.app.common;

/**
 * 当前登录用户上下文（开发阶段使用 ThreadLocal 模拟）
 */
public class CurrentUser {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    public static void set(Long userId) {
        USER_ID.set(userId);
    }

    public static Long get() {
        return USER_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
    }
}
