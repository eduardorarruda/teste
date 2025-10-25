package com.migracao.exception;

/**
 * Exceção lançada quando há problemas de configuração
 */
public class ConfigException extends Exception {
    
    public ConfigException(String message) {
        super(message);
    }
    
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
} 
