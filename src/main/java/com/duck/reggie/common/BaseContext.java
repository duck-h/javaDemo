package com.duck.reggie.common;


//基於ThreadLocal封裝工具類 用戶保存和獲取當前登入用戶id
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
