package org.example.baitapcnpm.controller;

import jakarta.servlet.http.HttpSession;
import org.example.baitapcnpm.model.User;
import org.example.baitapcnpm.util.SessionHelper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public User populateCurrentUser(HttpSession session) {
        return SessionHelper.getCurrentUser(session);
    }
}
