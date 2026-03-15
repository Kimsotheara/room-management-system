package com.room.management.security.context;

import com.room.management.entity.auth.User;
import com.room.management.entity.auth.UserToken;

import java.util.List;

public final class SecurityContext {

    private static final ThreadLocal<UserToken> CURRENT_TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<User> CURRENT_USER = new ThreadLocal<>();

    private SecurityContext() {}

    public static void set(UserToken token, User user) {
        CURRENT_TOKEN.set(token);
        CURRENT_USER.set(user);
    }

    public static UserToken getCurrentToken() {
        return CURRENT_TOKEN.get();
    }

    public static User getCurrentUser() {
        return CURRENT_USER.get();
    }

    public static Long getCurrentUserId() {
        User user = CURRENT_USER.get();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentUsername() {
        User user = CURRENT_USER.get();
        return user != null ? user.getUsername() : null;
    }

    public static List<String> getCurrentUserPermissions() {
        UserToken token = CURRENT_TOKEN.get();
        return token != null ? token.getPermissionCodesList() : List.of();
    }

    public static boolean hasPermission(String permissionCode) {
        return getCurrentUserPermissions().contains(permissionCode);
    }

    public static boolean isAuthenticated() {
        return CURRENT_USER.get() != null;
    }

    public static void clear() {
        CURRENT_TOKEN.remove();
        CURRENT_USER.remove();
    }
}
