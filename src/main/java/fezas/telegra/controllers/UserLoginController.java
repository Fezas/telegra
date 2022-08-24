/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

import fezas.telegra.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserLoginController {
    private static User user;
    private static UserLoginController instance;
    private static final Logger logger = LogManager.getLogger();
    public static UserLoginController getInstance() {
        if (instance == null) {
            instance = new UserLoginController();
        }
        return instance;
    }

    public UserLoginController() {
    }

    public static User getCurrentUser(){
        return user;
    }

    public static void setCurrentUser(User user) {
        UserLoginController.user = user;
    }
}
