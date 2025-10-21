package com.crudzaso.crudpark.service;

import com.crudzaso.crudpark.dao.OperatorDAO;
import com.crudzaso.crudpark.model.Operator;

/**
 * Service for managing operator authentication
 */
public class AuthService {
    private final OperatorDAO operatorDAO;
    private Operator currentOperator;

    public AuthService() {
        this.operatorDAO = new OperatorDAO();
    }

    public Operator login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        Operator operator = operatorDAO.findByUsernameAndPassword(username, password);

        if (operator != null && operator.getActive()) {
            currentOperator = operator;
            System.out.println("Login successful: " + operator.getName());
            return operator;
        }

        return null;
    }

    public void logout() {
        if (currentOperator != null) {
            System.out.println("Logout: " + currentOperator.getName());
            currentOperator = null;
        }
    }

    public Operator getCurrentOperator() {
        return currentOperator;
    }

    public boolean isAuthenticated() {
        return currentOperator != null;
    }
}