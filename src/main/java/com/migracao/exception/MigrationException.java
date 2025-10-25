package com.migracao.exception;

/**
 * Exceção lançada quando há problemas durante a migração
 */
public class MigrationException extends Exception {
    
    public MigrationException(String message) {
        super(message);
    }
    
    public MigrationException(String message, Throwable cause) {
        super(message, cause);
    }
}