package com.example.demo.context;

import java.util.List;

public class BaseContext {

    //当前用户ID
    public static ThreadLocal<Long> currentUserId = new ThreadLocal<>();

    public static ThreadLocal<List<Long>> currentUserRoleIds = new ThreadLocal<>();


    public static Long getCurrentUserId() {
        return currentUserId.get();
    }

    public static void setCurrentUserId(Long id) {
        currentUserId.set(id);
    }

    public static List<Long> getCurrentUserRoleIds() {
        return currentUserRoleIds.get();
    }

    public static void setCurrentUserRoleIds(List<Long> roleIds) {
        currentUserRoleIds.set(roleIds);
    }

    public static void clear() {
        currentUserId.remove();
        currentUserRoleIds.remove();
    }


}
