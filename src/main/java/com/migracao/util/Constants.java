package com.migracao.util;

/**
 * Classe com constantes utilizadas na aplicação
 */
public final class Constants {
    
    // Informações da aplicação
    public static final String APP_NAME = "Sistema de Migração de Dados";
    public static final String APP_VERSION = "v1.0.0";
    
    // Diretórios
    public static final String CONFIG_DIR = "config";
    public static final String BACKUP_DIR = "backups";
    public static final String SCRIPTS_DIR = "scripts";
    
    // Arquivos de configuração
    public static final String SOURCE_DB_PROPERTIES = "source-db.properties";
    public static final String TARGET_DB_PROPERTIES = "target-db.properties";
    
    // Configurações de batch
    public static final int DEFAULT_BATCH_SIZE = 1000;
    public static final int DEFAULT_FETCH_SIZE = 500;
    
    // Timeouts (em milissegundos)
    public static final int CONNECTION_TIMEOUT = 30000; // 30 segundos
    public static final int QUERY_TIMEOUT = 300000; // 5 minutos
    
    private Constants() {
        // Previne instanciação
        throw new AssertionError("Classe de constantes não deve ser instanciada");
    }
}