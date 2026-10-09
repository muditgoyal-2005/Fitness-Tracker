package com.fitness.dao.impl;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.fitness.exception.DataAccessException;

abstract class JdbcSupport {
    protected final Logger logger = Logger.getLogger(getClass().getName());
    protected DataAccessException failure(String operation, SQLException cause) {
        logger.log(Level.SEVERE, operation, cause);
        return new DataAccessException(operation, cause);
    }
}