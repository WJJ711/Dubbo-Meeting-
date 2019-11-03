package com.stylefeng.guns.rest.common;

public class CurrentUser {

    //线程绑定的存储空间,该线程独有，用于线程之间数据隔离，每一个线程都有ThreadLocal
    private static final InheritableThreadLocal<String> threadLocal=new InheritableThreadLocal<>();

    public static void saveUserInfo(String userId){
        threadLocal.set(userId);
    }

    public static String getCurrentUser(){
        return threadLocal.get();
    }


}
