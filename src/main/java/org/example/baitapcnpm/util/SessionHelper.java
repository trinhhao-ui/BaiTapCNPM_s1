package org.example.baitapcnpm.util;

import jakarta.servlet.http.HttpSession;
import org.example.baitapcnpm.model.Role;
import org.example.baitapcnpm.model.User;

public class SessionHelper {

    public static final String SESSION_USER_KEY = "CURRENT_USER";

    public static User getCurrentUser(HttpSession session) {
        if (session == null) return null;
        return (User) session.getAttribute(SESSION_USER_KEY);
    }

    public static void setCurrentUser(HttpSession session, User user) {
        if (session != null) {
            session.setAttribute(SESSION_USER_KEY, user);
        }
    }

    public static void clear(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_USER_KEY);
        }
    }

    public static boolean hasRole(User user, Role role) {
        return user != null && user.getRole() == role;
    }
}
