package com.crudzaso.crudpark.dao.interfaces;

import com.crudzaso.crudpark.model.Operator;

/**
 * Interface for Operator DAO operations
 */
public interface IOperatorDAO {

    /**
     * Finds an operator by username and password (for login)
     * @param username operator's username
     * @param password operator's password
     * @return Operator if found and active, null otherwise
     */
    Operator findByUsernameAndPassword(String username, String password);

    /**
     * Finds an operator by ID
     * @param id operator's ID
     * @return Operator if found, null otherwise
     */
    Operator findById(int id);
}